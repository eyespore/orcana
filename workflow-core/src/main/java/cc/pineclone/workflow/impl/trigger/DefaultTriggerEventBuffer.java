package cc.pineclone.workflow.impl.trigger;

import cc.pineclone.workflow.api.trigger.event.TriggerEvent;
import cc.pineclone.workflow.api.trigger.event.TriggerEventBuffer;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultTriggerEventBuffer implements TriggerEventBuffer {

    private static final int DEFAULT_CAPACITY = 4096;  /* 默认队列长度 */
    private volatile boolean closed = false;

    private final BlockingQueue<TriggerEvent> queue;

    public DefaultTriggerEventBuffer() {
        this(DEFAULT_CAPACITY);
    }

    public DefaultTriggerEventBuffer(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    public DefaultTriggerEventBuffer(BlockingQueue<TriggerEvent> queue) {
        this.queue = queue;
    }

    @Override
    public void emit(TriggerEvent event) {
        if (closed) return;  // Buffer 已经被关闭，无法继续提交事件

        try {
            queue.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while emitting TriggerEvent", e);
        }
    }

    @Override
    public TriggerEvent take() throws InterruptedException {
        if (closed) {  // Buffer 被关闭，无法获取事件
            throw new IllegalStateException("TriggerEventBuffer is closed");
        }
        return queue.take();
    }

    @Nullable
    @Override
    public TriggerEvent poll() {
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
