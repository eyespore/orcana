package cc.pineclone.eventflow.runtime.impl.trigger;

import cc.pineclone.eventflow.core.api.event.EventSink;
import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.event.EventSource;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultTriggerEventBuffer
        implements EventSource, EventSink, AutoCloseable {

    private static final int DEFAULT_CAPACITY = 4096;  /* 默认队列长度 */
    private volatile boolean closed = false;

    private final BlockingQueue<Event> queue;

    public DefaultTriggerEventBuffer() {
        this(DEFAULT_CAPACITY);
    }

    public DefaultTriggerEventBuffer(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    public DefaultTriggerEventBuffer(BlockingQueue<Event> queue) {
        this.queue = queue;
    }

    @Override
    public void emit(Event event) {
        if (closed) return;  // Buffer 已经被关闭，无法继续提交事件

        try {
            queue.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while emitting TriggerEvent", e);
        }
    }

    @Override
    public Event take() throws InterruptedException {
        if (closed) {  // Buffer 被关闭，无法获取事件
            throw new IllegalStateException("TriggerEventBuffer is closed");
        }
        return queue.take();
    }

    @Nullable
    @Override
    public Event poll() {
        if (closed) {  // Buffer 被关闭，无法获取事件
            throw new IllegalStateException("TriggerEventBuffer is closed");
        }
        return queue.poll();
    }

    @Override
    public void close() {
        closed = true;
    }
}
