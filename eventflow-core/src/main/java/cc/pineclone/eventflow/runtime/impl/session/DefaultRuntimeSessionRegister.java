package cc.pineclone.eventflow.runtime.impl.session;

import cc.pineclone.eventflow.core.api.Action;
import cc.pineclone.eventflow.runtime.api.session.RuntimeSessionRegistry;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Deprecated
public class DefaultRuntimeSessionRegister implements RuntimeSessionRegistry {

    /* ActionIdentity 到其当前 Session 的映射 */
    private final ConcurrentHashMap<ActionIdentity, DefaultActionSession> ActionIdentityToSession = new ConcurrentHashMap<>();
    /* SessionId 到 ActionIdentity 的映射 */
    private final ConcurrentHashMap<UUID, ActionIdentity> sessionIdToActionIdentity = new ConcurrentHashMap<>();

    /* 执行器，实现异步执行 */
    private final Executor executor;

    /* 上下文工厂，用于构建会话上下文 */
    private final SessionContextFactory sessionContextFactory;

    /* 时钟，用于生成时间戳 */
    private final Clock clock;

    public DefaultRuntimeSessionRegister(
            Executor executor,
            SessionContextFactory sessionContextFactory,
            Clock clock
    ) {
        this.executor = Objects.requireNonNull(executor, "executor");
        this.sessionContextFactory = Objects.requireNonNull(sessionContextFactory, "sessionContextFactory");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public ActionSession getOrCreateNewSession(Action action) {
        Objects.requireNonNull(action, "action");

        // 若已存在 session，则忽略传入的 sessionId；这里不新增 identitySessionById 映射，避免脏数据
        return ActionIdentityToSession.computeIfAbsent(action.id(), id -> {
            /* 会话不存在，构建新的会话 */
            Instant now = Instant.now(clock);  /* 生成时间戳 */
            UUID sessionId = UUID.randomUUID();  /* 生成会话 ID */

            SessionContext sessionContext = sessionContextFactory.createContext(action.id(), sessionId, now);

            /* 先构建再加入 Map，避免由于构建对象时出现异常导致脏数据 */
            DefaultActionSession newSession = new DefaultActionSession(
                    sessionId,
                    action,
                    sessionContext  /* 构建会话上下文 */
            );

            sessionIdToActionIdentity.put(sessionId, id);  /* 建立会话 ID 到 ActionIdentity 的映射信息 */
            return newSession;
        });
    }

    @Override
    public Optional<ActionSession> findSessionByIdentity(ActionIdentity identity) {
        Objects.requireNonNull(identity, "identity");
        return Optional.ofNullable(ActionIdentityToSession.get(identity));
    }

    @Override
    public Optional<ActionSession> findSessionById(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId");
        ActionIdentity identity = sessionIdToActionIdentity.get(sessionId);
        if (identity == null) return Optional.empty();

        DefaultActionSession s = ActionIdentityToSession.get(identity);
        if (s == null) return Optional.empty();

        // 防御：映射表可能有历史脏数据，确保 sessionId 匹配
        if (!s.sessionId().equals(sessionId)) return Optional.empty();

        return Optional.of(s);
    }

    @Override
    public boolean removeSession(ActionIdentity identity, UUID sessionId) {
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(sessionId, "sessionId");

        DefaultActionSession s = ActionIdentityToSession.get(identity);
        if (s == null) return false;
        if (!s.sessionId().equals(sessionId)) return false;

        boolean removed = ActionIdentityToSession.remove(identity, s);
        if (removed) {
            sessionIdToActionIdentity.remove(sessionId, identity);
            s.setState(ActionSession.State.FINISHED);
        }
        return removed;
    }

    @Override
    public <T> CompletionStage<T> attachSessionAsync(UUID sessionId, Function<ActionSession, T> task) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(task, "task");

        CompletableFuture<T> result = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                T val = attachSessionSync(sessionId, task);
                result.complete(val);
            } catch (Throwable t) {
                result.completeExceptionally(t);
            }
        });
        return result;
    }

    @Override
    public <T> T attachSessionSync(UUID sessionId, Function<ActionSession, T> task) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(task, "task");

        DefaultActionSession currentSession = (DefaultActionSession) findSessionById(sessionId)
                .orElseThrow(() -> new IllegalStateException("Unknown sessionId: " + sessionId));

        currentSession.getLock().lock();
        try {
            return task.apply(currentSession);
        } finally {
            currentSession.getLock().unlock();
        }
    }
}
