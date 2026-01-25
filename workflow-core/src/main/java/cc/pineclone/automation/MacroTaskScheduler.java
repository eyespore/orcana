package cc.pineclone.automation;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 宏任务调度
 */
public class MacroTaskScheduler {

    private final ScheduledExecutorService scheduler;
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected MacroTaskScheduler() {
        log.info("Loading macro task scheduler for handling macro multiple-threads task");
        this.scheduler = Executors.newScheduledThreadPool(
                Runtime.getRuntime().availableProcessors(),
                new NamedThreadFactory("macro-task-scheduler-%d"));
    }

    public Future<?> submit(Runnable task) {
        return this.scheduler.submit(task);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable command, long initialDelay, long period, @NotNull TimeUnit unit) {
        return this.scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public ScheduledFuture<?> schedule(@NotNull Runnable command, long delay, @NotNull TimeUnit unit) {
        return this.scheduler.schedule(command, delay, unit);
    }

    protected void shutdown() {
        this.scheduler.shutdownNow();
    }

    private static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger count = new AtomicInteger();
        private final String pattern;
        public NamedThreadFactory(String pattern) { this.pattern = pattern; }
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread t = new Thread(r, String.format(pattern, count.incrementAndGet()));
            t.setDaemon(true);
            return t;
        }
    }
}
