package cc.pineclone.workflow.impl.trigger;

import cc.pineclone.workflow.api.trigger.event.TriggerEvent;
import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SequentialTrigger extends DefaultCompositeTrigger {

    private final TriggerIdentity identity;
    private final TriggerEventIdentity eventIdentity;

    private final List<TriggerEventIdentity> sequentialEvents; // 用户定义的事件顺序
    private final Set<TriggerEventIdentity> forbiddenEvents;  // 禁止事件集合
    private final long timeoutMs;

    private final List<TriggerEventIdentity> receivedEvents = new ArrayList<>();
    private ScheduledFuture<?> timeoutFuture;
    private ScheduledExecutorService scheduler;

    public SequentialTrigger(
            TriggerIdentity identity,
            TriggerEventIdentity eventIdentity,
            List<TriggerEventIdentity> sequentialEvents,
            Set<TriggerEventIdentity> forbiddenEvents,
            long timeoutMs) {
        this.identity = identity;  /* 触发器描述 */
        this.eventIdentity = eventIdentity;  /* 触发的事件描述 */
        this.sequentialEvents = List.copyOf(sequentialEvents);
        this.forbiddenEvents = Set.copyOf(forbiddenEvents);
        this.timeoutMs = timeoutMs;
    }

    /* 通过对 handleChildTriggerEvent 和 reset 同时加锁避免由于 Trigger 本身线程和 scheduler线程 */
    /* 触发的对 receivedEvents 的竞态修改问题 */
    @Override
    public synchronized void handleChildTriggerEvent(TriggerEvent event) {
        TriggerEventIdentity identity = event.getIdentity();

        if (forbiddenEvents.contains(identity)) {
            reset();
            return;
        }

        if (!sequentialEvents.contains(identity)) return;

        int nextIndex = receivedEvents.size();
        if (!sequentialEvents.get(nextIndex).equals(identity)) {
            reset();
            return;
        }

        receivedEvents.add(identity);
        if (receivedEvents.size() == 1) {  // 在序列开始时计时
            timeoutFuture = scheduler.schedule(this::reset, timeoutMs, TimeUnit.MILLISECONDS);
        }

        if (receivedEvents.size() == sequentialEvents.size()) {
            emit(this.eventIdentity);  // TODO: SequentialTrigger 也许需要保留原始事件的数据信息？
            reset();
        }
    }

    private synchronized void reset() {
        receivedEvents.clear();
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

    @Override
    public TriggerIdentity getIdentity() {
        return this.identity;
    }
}
