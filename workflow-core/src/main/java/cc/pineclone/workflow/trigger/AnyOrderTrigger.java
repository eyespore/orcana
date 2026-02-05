package cc.pineclone.workflow.trigger;

import cc.pineclone.workflow.api.trigger.TriggerEvent;
import cc.pineclone.workflow.api.trigger.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AnyOrderTrigger extends DefaultCompositeTrigger {

    private final TriggerIdentity identity;
    private final TriggerEventIdentity eventIdentity;

    private final Map<TriggerEventIdentity, Integer> requiredCounts; // 用户定义每个事件需要出现的次数
    private final Set<TriggerEventIdentity> forbiddenEvents;  // 禁止事件集合
    private final long timeoutMs;

    private final Map<TriggerEventIdentity, Integer> receivedCounts = new HashMap<>();
    private ScheduledFuture<?> timeoutFuture;
    private ScheduledExecutorService scheduler;

    public AnyOrderTrigger(
            TriggerIdentity identity,
            TriggerEventIdentity eventIdentity,
            Map<TriggerEventIdentity, Integer> requiredCounts,
            Set<TriggerEventIdentity> forbiddenEvents,
            long timeoutMs) {
        this.identity = identity;  /* 触发器描述 */
        this.eventIdentity = eventIdentity;  /* 触发的事件描述 */
        this.requiredCounts = Map.copyOf(requiredCounts);
        this.forbiddenEvents = Set.copyOf(forbiddenEvents);
        this.timeoutMs = timeoutMs;
    }

    /* 通过对 handleChildTriggerEvent 和 reset 同时加锁避免由于 Trigger 本身线程和 scheduler线程 */
    /* 触发的对 receivedEvents 的竞态修改问题 */
    @Override
    public synchronized void handleChildTriggerEvent(TriggerEvent event) {
        TriggerEventIdentity id = event.getIdentity();
        if (forbiddenEvents.contains(id)) {
            reset();
            return;
        }

        if (!requiredCounts.containsKey(id)) return;

        receivedCounts.merge(id, 1, Integer::sum);

        if (receivedCounts.size() == 1) { // 第一个事件开始计时
            timeoutFuture = scheduler.schedule(this::reset, timeoutMs, TimeUnit.MILLISECONDS);
        }

        if (isComplete()) {
            emit(eventIdentity);  // TODO: AnyOrderTrigger 也许需要保留原始事件的数据信息？
            reset();
        }
    }

    private boolean isComplete() {
        if (requiredCounts.isEmpty()) return false; // 若 requiredCounts 定义为空，那么永远不会触发
        return requiredCounts.entrySet().stream().allMatch(e ->
                        receivedCounts.getOrDefault(e.getKey(), 0) >= e.getValue());
    }

    private synchronized void reset() {
        receivedCounts.clear();
        if (timeoutFuture != null) {
            timeoutFuture.cancel(false);
            timeoutFuture = null;
        }
    }

    @Override
    public void init() {
        super.init();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void destroy() {
        this.scheduler.shutdown();
        super.destroy();
    }

    @Override
    public TriggerIdentity getIdentity() {
        return this.identity;
    }
}
