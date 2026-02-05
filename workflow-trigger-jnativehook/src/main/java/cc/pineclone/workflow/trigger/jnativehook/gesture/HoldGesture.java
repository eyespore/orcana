package cc.pineclone.workflow.trigger.jnativehook.gesture;

import cc.pineclone.interaction.api.Gesture;
import cc.pineclone.workflow.trigger.jnativehook.api.GestureInterpreter;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/* 按住触发 */
public abstract class HoldGesture<T extends NativeInputEvent> implements GestureInterpreter<T> {

    private final long holdThresholdMs;
    private final ScheduledExecutorService scheduler;

    private ScheduledFuture<?> future;

    private final AtomicBoolean fired = new AtomicBoolean(false);
    private final AtomicBoolean pressed = new AtomicBoolean(false);

    public HoldGesture(long holdThresholdMs, ScheduledExecutorService scheduler) {
        this.holdThresholdMs = holdThresholdMs;
        this.scheduler = scheduler;
    }

    protected abstract boolean isPress(T event);
    protected abstract boolean isRelease(T event);

    @Override
    public void interpret(T event, Consumer<Gesture> callback) {
        if (isPress(event)) {
            if (holdThresholdMs <= 0) {  // 阈值为0，立即触发 HOLD_ON
                callback.accept(Gesture.HOLD_ON);
                return;
            }

            pressed.set(true);  /* 按键按下 */
            fired.set(false);

            // 阈值 > 0，按原逻辑调度
            future = scheduler.schedule(() -> {  // 仅在按键仍然按下且未触发过的情况下才触发
                if (pressed.get() && fired.compareAndSet(false, true))
                    callback.accept(Gesture.HOLD_ON);
            }, holdThresholdMs, TimeUnit.MILLISECONDS);

        } else if (isRelease(event)) {
            if (holdThresholdMs <= 0) {  // 阈值为0，立即触发 HOLD_OFF
                callback.accept(Gesture.HOLD_OFF);
                return;
            }

            pressed.set(false);  /* 按键松开 */
            if (future != null && !future.isDone()) future.cancel(false);
            if (fired.compareAndSet(true, false)) {
                callback.accept(Gesture.HOLD_OFF);
            }
        }
    }

    public static class KeyHoldGesture extends HoldGesture<NativeKeyEvent> {

        public KeyHoldGesture(long holdThresholdMs, ScheduledExecutorService scheduler) {
            super(holdThresholdMs, scheduler);
        }

        @Override
        protected boolean isPress(NativeKeyEvent event) {
            return event.getID() == NativeKeyEvent.NATIVE_KEY_PRESSED;
        }

        @Override
        protected boolean isRelease(NativeKeyEvent event) {
            return event.getID() == NativeKeyEvent.NATIVE_KEY_RELEASED;
        }
    }

    public static class MouseButtonHoldGesture extends HoldGesture<NativeMouseEvent> {

        public MouseButtonHoldGesture(long holdThresholdMs, ScheduledExecutorService scheduler) {
            super(holdThresholdMs, scheduler);
        }

        @Override
        protected boolean isPress(NativeMouseEvent event) {
            return event.getID() == NativeMouseEvent.NATIVE_MOUSE_PRESSED;
        }

        @Override
        protected boolean isRelease(NativeMouseEvent event) {
            return event.getID() == NativeMouseEvent.NATIVE_MOUSE_RELEASED;
        }
    }
}
