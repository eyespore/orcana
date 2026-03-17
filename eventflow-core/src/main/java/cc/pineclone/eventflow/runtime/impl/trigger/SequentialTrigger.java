package cc.pineclone.eventflow.runtime.impl.trigger;

import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.core.api.ComponentId;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SequentialTrigger extends DefaultCompositeTrigger {

    private final String eventType;

    private final List<Map.Entry<EventSelector, Integer>> steps; // 按顺序的步骤
    private final Set<EventSelector> requiredKeys;              // 用于快速判断“是否属于序列事件集合”

    private final Set<EventSelector> forbiddenEvents;  // 禁止事件集合
    private final long timeoutMs;

    private int stepIndex = 0;
    private int stepProgress = 0;

    private final List<EventSelector> receivedEvents = new ArrayList<>();
    private ScheduledFuture<?> timeoutFuture;
    private ScheduledExecutorService scheduler;

    public SequentialTrigger(
            ComponentId identity,
            String eventType,
            Map<EventSelector, Integer> sequentialCounts,
            Set<EventSelector> forbiddenEvents,
            long timeoutMs) {
        super(identity);
        this.eventType = Objects.requireNonNull(eventType, "eventType");

        // 固化顺序：无论外界给什么 Map，实现内都用 LinkedHashMap，保序：事件 + 次数
        Map<EventSelector, Integer> ordered = Collections.unmodifiableMap(
                        new LinkedHashMap<>(Objects.requireNonNull(
                                sequentialCounts, "sequentialCounts")));

        this.steps = List.copyOf(ordered.entrySet().stream().toList());
        this.requiredKeys = Set.copyOf(ordered.keySet());

        this.forbiddenEvents = Set.copyOf(Objects.requireNonNull(forbiddenEvents, "forbiddenEvents"));
        this.timeoutMs = timeoutMs;
    }

    /* 通过对 handleChildTriggerEvent 和 reset 同时加锁避免由于 Trigger 本身线程和 scheduler线程 */
    /* 触发的对 receivedEvents 的竞态修改问题 */
    @Override
    public synchronized void handleChildTriggerEvent(Event event) {
        EventSelector eventKey = event.getEventKey();

        if (forbiddenEvents.contains(eventKey)) {
            reset();
            return;
        }

        // 不属于序列集合的事件：忽略，不打断
        if (!requiredKeys.contains(eventKey)) return;

        // 已经处于完成态（理论上不会，因为完成时会 reset）
        if (stepIndex >= steps.size()) {
            reset();
            return;
        }

        Map.Entry<EventSelector, Integer> step = steps.get(stepIndex);
        EventSelector expectedKey = step.getKey();
        int expectedCount = step.getValue();

        // 序列事件集合内，但不是当前期望：打断并 reset
        if (!expectedKey.equals(eventKey)) {
            reset();
            return;
        }

        // 匹配当前 step
        stepProgress++;

        // 第一次命中（序列开始）时启动计时
        if (stepIndex == 0 && stepProgress == 1) {
            timeoutFuture = scheduler.schedule(this::reset, timeoutMs, TimeUnit.MILLISECONDS);
        }

        // 当前 step 达标，进入下一 step
        if (stepProgress >= expectedCount) {
            stepIndex++;
            stepProgress = 0;
        }

        // 全部 step 完成
        if (stepIndex >= steps.size()) {
            emit(this.eventType);
            reset();
        }
    }

    private synchronized void reset() {
        stepIndex = 0;
        stepProgress = 0;

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
