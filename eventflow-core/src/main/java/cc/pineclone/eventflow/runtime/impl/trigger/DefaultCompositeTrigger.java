package cc.pineclone.eventflow.runtime.impl.trigger;

import cc.pineclone.eventflow.core.api.trigger.CompositeTrigger;
import cc.pineclone.eventflow.core.api.trigger.Trigger;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.trigger.TriggerLifecycle;
import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.core.api.event.EventSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public abstract class DefaultCompositeTrigger
        implements CompositeTrigger, TriggerLifecycle {

    private final ComponentId identity;

    private volatile EventSink sink;
    private final List<Trigger> children;

    private DefaultTriggerEventBuffer childEventBuffer;
    private ExecutorService eventLoopExecutor;

    private volatile boolean closed = false;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected DefaultCompositeTrigger(ComponentId identity) {
        this.children = new ArrayList<>();
        this.identity = identity;
    }

    @Override
    public synchronized void init() {
        checkNotDestroyed();
        if (childEventBuffer != null || eventLoopExecutor != null) {
            throw new IllegalStateException("Trigger already initialized: " + id());
        }

        childEventBuffer = new DefaultTriggerEventBuffer(new LinkedBlockingQueue<>());
        eventLoopExecutor = Executors.newSingleThreadExecutor();

        eventLoopExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Event event = childEventBuffer.take();  /* 单线程模型，不需要加锁 */
                    handleChildTriggerEvent(event);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (RuntimeException e) {
                    // 子类 handle 逻辑异常：记录并继续，避免线程悄悄退出导致整个 trigger 停摆
                    log.warn("Unhandled exception in event loop of {}", id(), e);
                }
            }
        });

        children.forEach(t -> t.bind(this.childEventBuffer));
    }

    @Override
    public synchronized void stop() {
        checkNotDestroyed();
        children.forEach(Trigger::unbind);
        cleanEventLoopExecutor();
        cleanChildEventBuffer();
    }

    @Override
    public synchronized void close() {
        if (closed) return;
        closed = true;
        children.forEach(Trigger::unbind);
        cleanEventLoopExecutor();
        cleanChildEventBuffer();
        children.clear();
        sink = null;
    }

    private void checkNotDestroyed() {
        if (closed) {
            throw new IllegalStateException("Trigger is destroyed: " + id());
        }
    }

    private void cleanEventLoopExecutor() {
        if (eventLoopExecutor != null) {
            eventLoopExecutor.shutdownNow();
            try {
                if (!eventLoopExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    log.warn("Event loop did not terminate in time for {}", id());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                eventLoopExecutor = null;
            }
        }
    }

    private void cleanChildEventBuffer() {
        if (childEventBuffer != null) {
            childEventBuffer.close();
            childEventBuffer = null;
        }
    }


    @Override
    public List<Trigger> children() {
        return children;
    }

    @Override
    public void addChild(Trigger trigger) {
        this.children.add(trigger);
    }

    @Override
    public void removeChild(Trigger trigger) {
        this.children.remove(trigger);
    }

    @Override
    public final void bind(EventSink sink) {
        if (this.sink != null)
            throw new IllegalStateException("Trigger already attached to an existed sink");
        this.sink = sink;
    }

    @Override
    public final void unbind() {
        this.sink = null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DefaultCompositeTrigger that = (DefaultCompositeTrigger) o;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    protected final void emit(String eventType) {
        this.emit(new Event(this, eventType));
    }

    protected final void emit(String eventType, Map<String, Object> meta) {
        this.emit(new Event(this, eventType, meta));
    }

    protected final void emit(EventSelector eventKey) {
        this.emit(new Event(this, eventKey));
    }

    protected final void emit(EventSelector eventKey, Map<String, Object> meta) {
        this.emit(new Event(this, eventKey, meta));
    }

    private void emit(Event event) {
        if (sink == null) throw new IllegalStateException(id() + " has no sink to emit event");
        sink.emit(event);
    }

    @Override
    public final ComponentId id() {
        return identity;
    }

    public abstract void handleChildTriggerEvent(Event event);

}
