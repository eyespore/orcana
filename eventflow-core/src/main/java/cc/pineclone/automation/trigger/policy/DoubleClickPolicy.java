package cc.pineclone.automation.trigger.policy;

import cc.pineclone.automation.AutomationContext;
import cc.pineclone.automation.trigger.TriggerStatus;
import cc.pineclone.automation.trigger.source.InputSourceEvent;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/* 双击触发 */
public class DoubleClickPolicy implements ActivationPolicy {

    private final long interval;
//    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private volatile long lastPressedTime = 0;
    private volatile ScheduledFuture<?> future = null;
    private final Object lock = new Object();

    public DoubleClickPolicy(long interval) {
        this.interval = interval;
    }

    @Override
    public void decide(InputSourceEvent event, Consumer<Optional<TriggerStatus>> callback) {
        switch (event.getOperation()) {
            case KEY_PRESSED, MOUSE_PRESSED, MOUSE_WHEEL_MOVED -> {
                /* 触发事件 */
                long now = System.currentTimeMillis();
                synchronized (lock) {
                    if (future != null && !future.isDone()) {
                        // 第二次点击，判定为 DOUBLE_CLICK
                        future.cancel(false);
                        future = null;
                        lastPressedTime = 0;
                        callback.accept(Optional.of(TriggerStatus.DOUBLE_CLICK));
                    } else {
                        // 第一次点击，延迟判定为 CLICK
                        lastPressedTime = now;
                        future = AutomationContext.getInstance().getScheduler().schedule(() -> {
                            callback.accept(Optional.of(TriggerStatus.CLICK));
                            synchronized (lock) {
                                future = null;
                                lastPressedTime = 0;
                            }
                        }, interval, TimeUnit.MILLISECONDS);
                    }
                }
            }
            default -> callback.accept(Optional.empty());
        }
    }

//    @Override
//    public void onMarcoUninstall() {
//        synchronized (lock) {
//            if (future != null && !future.isDone()) {
//                future.cancel(false);
//            }
//        }
//        scheduler.shutdownNow();
//    }
}
