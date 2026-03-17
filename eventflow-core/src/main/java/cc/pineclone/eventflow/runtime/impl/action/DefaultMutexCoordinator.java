package cc.pineclone.eventflow.runtime.impl.action;

import cc.pineclone.eventflow.runtime.api.AdmissionSession;
import cc.pineclone.eventflow.runtime.api.MutexCoordinator;
import cc.pineclone.eventflow.runtime.api.MutexProfile;
import cc.pineclone.eventflow.runtime.api.PreemptionPolicy;

import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Deprecated
public class DefaultMutexCoordinator implements MutexCoordinator {

    private static final String IGNORED_MUTEX_GROUP = "NO_MUTEX_GROUP";

    private final ReentrantLock lock = new ReentrantLock();
    private final Clock clock;  /* 时钟，用于生成时间戳 */

    /** 权威实体表：decide() 即登记 */
    private final Map<UUID, AdmissionSession> admissionsById = new HashMap<>();

    /** admitted：谁正在占用资源（sessionId -> admission） */
    private final Map<UUID, AdmissionSession> admittedById = new HashMap<>();

    /** queued 真值表（sessionId -> admission），用于 O(1) 判断与 lazy deletion */
    private final Map<UUID, AdmissionSession> queuedById = new HashMap<>();

    /** group -> 当前 owner sessionId */
    private final Map<String, UUID> ownerByGroup = new HashMap<>();

    private static final Comparator<AdmissionSession> QUEUE_ORDER = Comparator.<AdmissionSession>comparingInt(
            r -> r.getMutexProfile().priority()).reversed()
                    .thenComparing(AdmissionSession::getCreatedAt)
                    .thenComparing(AdmissionSession::getSessionId);
    private final Map<String, PriorityQueue<AdmissionSession>> queuesByGroup = new HashMap<>();

    public DefaultMutexCoordinator(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public Decision decide(UUID sessionId, MutexProfile mutexProfile) {

        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(mutexProfile, "mutexProfile");

        lock.lock();
        try {
            Instant now = Instant.now(clock);
            /* 尝试获取目标 Session 映射到的互斥会话 */
            AdmissionSession admission = admissionsById.computeIfAbsent(
                    sessionId, id -> new AdmissionSession(sessionId, mutexProfile, now)
            );

            // 幂等保护：已经 admitted / queued 的不允许再次 decide
            if (admission.getState() == AdmissionSession.State.ADMITTED) {
                return new Decision.Deny("already admitted");
            }

            if (admission.getState() == AdmissionSession.State.QUEUED) {
                return new Decision.Deny("already queued");
            }

            String mutexGroup = mutexProfile.group();
            if (mutexGroup == null || mutexGroup.equalsIgnoreCase(IGNORED_MUTEX_GROUP)) {
                // 无互斥：保留状态为 IDLE，等待 Engine 真正执行后回调 onAdmitted 即可
                admission.setState(AdmissionSession.State.IDLE);
                return new Decision.Admit("no mutex group");
            }

            /* 存在互斥组，检查目标互斥组持有者的 UUID */
            UUID ownerSessionId = ownerByGroup.get(mutexGroup);
            if (ownerSessionId == null) {  /* 当前互斥组不存在持有者，将当前会话标记为持有者并允许执行 */
                admission.setState(AdmissionSession.State.IDLE);
                return new Decision.Admit("mutex group available");
            }

            /* 当前互斥组已经存在一个持有者，持有者不应该为 null，否则为状态错误 */
            AdmissionSession victim = admittedById.get(ownerSessionId);
            Objects.requireNonNull(victim, "victim is null");

            // 抢占测试：这里沿用你现有逻辑（incoming 高优先级且 victim policy != NEVER）
            MutexProfile incomingProfile = admission.getMutexProfile();

            MutexProfile victimProfile = victim.getMutexProfile();
            PreemptionPolicy victimPolicy = victimProfile.policy();

            /* 策略不应该为 null */
            Objects.requireNonNull(victimPolicy, "victimPolicy is null");

            /* 尝试比对抢占 */
            if (victimPolicy != PreemptionPolicy.NEVER && incomingProfile.priority() > victimProfile.priority()) {
                /* 抢占成立，给出被抢占者提供的抢占策略，以及被抢占者所在的会话 */
                admission.setState(AdmissionSession.State.IDLE);
                return new Decision.Preempt(new PreemptPlan(victim.getSessionId(), victimPolicy));
            }

            // 入队
            enqueueLocked(mutexGroup, admission);
            return new Decision.Queue("mutex busy, queued");

        } finally {
            lock.unlock();
        }
    }

    /* 标记某个 Session 持有调度资源 */
    @Override
    public void markSessionAsAdmitted(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId");

        lock.lock();
        try {
            AdmissionSession admission = admissionsById.get(sessionId);
            Objects.requireNonNull(admission, "admission is null");

            // 若之前 queued，先标记离队（lazy deletion：从 queuedById 移除，PQ 里残留后续 poll 会跳过）
            if (admission.getState() == AdmissionSession.State.QUEUED) {
                queuedById.remove(sessionId);
            }

            // IDLE/QUEUED -> ADMITTED（幂等：重复调用保持 ADMITTED）
            admission.setState(AdmissionSession.State.ADMITTED);
            admittedById.put(sessionId, admission);

            String mutexGroup = admission.getMutexProfile().group();
            if (mutexGroup != null && !mutexGroup.equalsIgnoreCase(IGNORED_MUTEX_GROUP)) {
                UUID owner = ownerByGroup.get(mutexGroup);
                if (owner != null && !owner.equals(sessionId)) {
                    /* 尝试标记某个 Session 获取了调度资源，但是实际上同组已经被另一个 Session 抢占，这种情况不应该发生 */
                    throw new IllegalStateException("group already owned: " + mutexGroup + " by " + owner);
                }

                /* 将当前会话标记为此互斥组的持有者 */
                ownerByGroup.put(mutexGroup, sessionId);
            }
        } finally {
            lock.unlock();
        }
    }

    /* 将某个会话标记为互斥资源已经释放完成 */
    @Override
    public ReleaseResult markSessionAsReleased(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId");

        lock.lock();
        try {
            AdmissionSession admission = admissionsById.get(sessionId);
            if (admission == null) {
                return new ReleaseResult.Nothing();
            }

            // queued 的 release = 取消排队（幂等）
            if (admission.getState() == AdmissionSession.State.QUEUED) {
                admission.setState(AdmissionSession.State.IDLE);
                queuedById.remove(sessionId);
                return new ReleaseResult.Nothing();
            }

            // admitted 的 release = 释放互斥组并挑选下一位
            if (admission.getState() == AdmissionSession.State.ADMITTED) {
                admission.setState(AdmissionSession.State.IDLE);
                admittedById.remove(sessionId);

                String mutexGroup = admission.getMutexProfile().group();
                if (mutexGroup == null || mutexGroup.equalsIgnoreCase(IGNORED_MUTEX_GROUP)) {
                    return new ReleaseResult.Nothing();
                }

                UUID owner = ownerByGroup.get(mutexGroup);
                if (sessionId.equals(owner)) {
                    ownerByGroup.remove(mutexGroup);
                }

                return tryAdmitNextFromGroupLocked(mutexGroup);
            }

            // IDLE/RELEASED：幂等无事可做
            return new ReleaseResult.Nothing();

        } finally {
            lock.unlock();
        }
    }

    /* 入队 */
    private void enqueueLocked(String mutexGroup, AdmissionSession admission) {
        admission.setState(AdmissionSession.State.QUEUED);
        queuedById.put(admission.getSessionId(), admission);

        PriorityQueue<AdmissionSession> q = queuesByGroup.computeIfAbsent(
                mutexGroup, g -> new PriorityQueue<>(QUEUE_ORDER)
        );
        q.add(admission);
    }

    private ReleaseResult tryAdmitNextFromGroupLocked(String mutexGroup) {
        PriorityQueue<AdmissionSession> queue = queuesByGroup.get(mutexGroup);
        if (queue == null) return new ReleaseResult.Nothing();

        // 若组还被占用（理论上 release 后应当空闲），直接 nothing
        if (ownerByGroup.containsKey(mutexGroup)) return new ReleaseResult.Nothing();

        while (true) {
            AdmissionSession head = queue.peek();
            if (head == null) {
                queuesByGroup.remove(mutexGroup);
                return new ReleaseResult.Nothing();
            }

            // lazy deletion：head 已不在 queuedById，则弹出继续
            if (!queuedById.containsKey(head.getSessionId())) {
                queue.poll();
                continue;
            }

            // 选中 head（出队由 onAdmitted 完成也可以；这里先从 queuedById 移除，保证不会重复被挑选）
            queuedById.remove(head.getSessionId());
            head.setState(AdmissionSession.State.IDLE);
            queue.poll();
            return new ReleaseResult.AdmitNext(head.getSessionId(), "admit from queue");
        }
    }
}
