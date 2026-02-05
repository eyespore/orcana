package cc.pineclone.workflow.trigger;

import cc.pineclone.workflow.api.trigger.TriggerEvent;
import cc.pineclone.workflow.api.trigger.TriggerEventDispatcher;
import cc.pineclone.workflow.api.trigger.TriggerEventSink;
import cc.pineclone.workflow.api.trigger.TriggerEventSource;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.BlockingQueue;

public class DefaultTriggerEventDispatcher implements TriggerEventDispatcher {

    private final BlockingQueue<TriggerEvent> queue;

    public DefaultTriggerEventDispatcher(BlockingQueue<TriggerEvent> queue) {
        this.queue = queue;
    }

    @Override
    public void emit(TriggerEvent event) {
        try {
            queue.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while emitting TriggerEvent", e);
        }
    }

    @Override
    public TriggerEvent take() throws InterruptedException {
        return queue.take();
    }

    @Nullable
    @Override
    public TriggerEvent poll() {
        return queue.poll();
    }

    public void clear() {
        this.queue.clear();
    }
}
