package cc.pineclone.eventflow.runtime.impl.trigger;

import cc.pineclone.eventflow.runtime.api.selector.EventSelector;
import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.ComponentId;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AnyOrderTrigger extends DefaultCompositeTrigger {

    /* args */
    private final Map<EventSelector, Integer> requiredCounts; // 用户定义每个事件需要出现的次数
    private final Set<EventSelector> forbiddenEvents;  // 禁止事件集合
    private final long timeoutMs;
    private final String eventType;

    private final Map<EventSelector, Integer> receivedCounts = new HashMap<>();
    private ScheduledFuture<?> timeoutFuture;
    private ScheduledExecutorService scheduler;

    /**
     * Constructs an {@code AnyOrderTrigger} instance that activates based on specified trigger
     * and event identities, required occurrence counts for specific events, forbidden events,
     * and a timeout constraint.
     *
     * @param identity the unique identity describing this trigger.
     * @param requiredCounts a map defining the required occurrence count for each event identity
     *                       that contributes to triggering. Each entry maps a {@code TriggerEventIdentity}
     *                       to the required number of occurrences.
     * @param forbiddenEvents a set of event identities that, if detected, will prevent the trigger
     *                        from activating and cause a reset.
     * @param timeoutMs the time in milliseconds after which an incomplete sequence of events will timeout,
     *                  causing any current progress towards triggering to reset.
     */
    public AnyOrderTrigger(
            ComponentId identity,
            String eventType,
            Map<EventSelector, Integer> requiredCounts,
            Set<EventSelector> forbiddenEvents,
            long timeoutMs) {
        super(identity);
        this.eventType = eventType;  /* 触发的事件类型 */
        this.requiredCounts = Map.copyOf(requiredCounts);
        this.forbiddenEvents = Set.copyOf(forbiddenEvents);
        this.timeoutMs = timeoutMs;
    }

    /* 通过对 handleChildTriggerEvent 和 reset 同时加锁避免由于 Trigger 本身线程和 scheduler线程 */
    /* 触发的对 receivedEvents 的竞态修改问题 */
    @Override
    public synchronized void handleChildTriggerEvent(Event event) {
        EventSelector key = event.getEventKey();
        if (forbiddenEvents.contains(key)) {
            reset();
            return;
        }

        if (!requiredCounts.containsKey(key)) return;
        receivedCounts.merge(key, 1, Integer::sum);

        if (receivedCounts.size() == 1) { // 第一个事件开始计时
            timeoutFuture = scheduler.schedule(this::reset, timeoutMs, TimeUnit.MILLISECONDS);
        }

        if (isComplete()) {
            emit(eventType);  // TODO: AnyOrderTrigger 也许需要保留原始事件的数据信息？
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
    public void close() {
        this.scheduler.shutdown();
        super.close();
    }
}
