package cc.pineclone.workflow.trigger.jnativehook.gesture;

import cc.pineclone.interaction.api.ClickEdge;
import cc.pineclone.interaction.api.Gesture;
import cc.pineclone.workflow.trigger.jnativehook.api.GestureInterpreter;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public abstract class ClickGesture<T extends NativeInputEvent> implements GestureInterpreter<T> {

    protected final ClickEdge edge;  /* 基于下降沿或上升沿触发 */

    private final long clickThresholdMs;  /* 点击检测时间窗口 */
    private final int maxClickCount;  /* 点击上限 */

    private final long debounceMs;  /* 防抖时间窗口 */
    private final AtomicLong lastFiredTime = new AtomicLong(0);  /* 最后触发时间，用于计算防抖 */

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;

    private final AtomicInteger clickCount = new AtomicInteger(0);
    private final AtomicLong firstClickTime = new AtomicLong(0);
    private final AtomicBoolean fired = new AtomicBoolean(false);

    private final Logger log = LoggerFactory.getLogger(getClass());

    public ClickGesture(
            ClickEdge edge,
            long clickThresholdMs,
            int maxClickCount,
            long debounceMs,
            ScheduledExecutorService scheduler
    ) {
        if (maxClickCount < 1) throw new IllegalArgumentException("maxClickCount >= 1");
        this.edge = edge;
        this.clickThresholdMs = clickThresholdMs;
        this.maxClickCount = maxClickCount;
        this.debounceMs = debounceMs;
        this.scheduler = scheduler;
    }

    protected abstract boolean isEdgeMatch(T event);

    @Override
    public void interpret(T event, Consumer<Gesture> callback) {
        if (!isEdgeMatch(event)) return;  /* 上升沿 下降沿过滤 */
//        log.debug("ClickGesture edge matched: {}", event.paramString());

        long now = System.currentTimeMillis();  /* 多次点击基于时间窗口判断 */
        if (now - lastFiredTime.get() < debounceMs) {  /* 防抖检查 */
//            log.debug("Hit debounceMs check");
            return; // 短时间内忽略重复触发
        }

        if (maxClickCount <= 1 || clickThresholdMs <= 0) {  /* 单次点击立即触发 */
            callback.accept(Gesture.SINGLE_CLICK);
            lastFiredTime.set(now);
            return;
        }

        if (now - firstClickTime.get() > clickThresholdMs) {
            /* 时间窗口过期，重置计数并启动新的时间窗口 */
            clickCount.set(1);
            fired.set(false); // 允许下一次触发
            future = scheduler.schedule(
                    () -> fireGesture(callback),
                    clickThresholdMs,
                    TimeUnit.MILLISECONDS);
            firstClickTime.set(now);
            return;
        }
        /* 时间窗口未过期，仅增加点击计数 */
        int currentClickCount = clickCount.incrementAndGet();/* 计数 + 1 */
        if (currentClickCount >= maxClickCount) {
            /* 点击次数已经达到上线，结束时间窗口并立即返回 */
            if (future != null && !future.isDone()) future.cancel(false);
            fireGesture(callback);
        }
    }

    private void fireGesture(Consumer<Gesture> callback) {
        /* 若 Gesture 已经被 Trigger 线程抢占触发，那么拒绝二次触发 Gesture */
        if (!fired.compareAndSet(false, true)) return;

        Gesture gesture = mapClickCountToGesture(clickCount.get());
        if (gesture != null) {
            callback.accept(gesture);
            lastFiredTime.set(System.currentTimeMillis());  /* 更新防抖时间 */
        }

        clickCount.set(0);  /* 重置计数 */
        firstClickTime.set(0);  /* 重置第一次点击时间点，避免由于第二次触发过快导致的异常 */
    }

    private Gesture mapClickCountToGesture(int count) {
        return switch (count) {
            case 1 -> Gesture.SINGLE_CLICK;
            case 2 -> Gesture.DOUBLE_CLICK;
            case 3 -> Gesture.TRIPLE_CLICK;
            default -> Gesture.MULTI_CLICK; // 超过三次统一 MULTI_CLICK
        };
    }

    public static class KeyClickGesture extends ClickGesture<NativeKeyEvent> {
        public KeyClickGesture(
                ClickEdge edge,
                long clickThresholdMs,
                int maxClickCount,
                long debounceMs,
                ScheduledExecutorService scheduler) {
            super(edge, clickThresholdMs, maxClickCount, debounceMs, scheduler);
        }

        @Override
        protected boolean isEdgeMatch(NativeKeyEvent event)  {
            return (edge == ClickEdge.FALLING && event.getID() == NativeKeyEvent.NATIVE_KEY_PRESSED)
                    || (edge == ClickEdge.RISING  && event.getID() == NativeKeyEvent.NATIVE_KEY_RELEASED);
        }
    }

    public static class MouseButtonClickGesture extends ClickGesture<NativeMouseEvent> {
        private final Logger log = LoggerFactory.getLogger(getClass());

        public MouseButtonClickGesture(
                ClickEdge edge,
                long clickThresholdMs,
                int maxClickCount,
                long debounceMs,
                ScheduledExecutorService scheduler) {
            super(edge, clickThresholdMs, maxClickCount, debounceMs, scheduler);
        }

        @Override
        protected boolean isEdgeMatch(NativeMouseEvent event) {
//            log.debug("Try matching event: {}", event.paramString());
            return (edge == ClickEdge.FALLING && event.getID() == NativeMouseEvent.NATIVE_MOUSE_PRESSED)
                    || (edge == ClickEdge.RISING  && event.getID() == NativeMouseEvent.NATIVE_MOUSE_RELEASED);
        }
    }

}
