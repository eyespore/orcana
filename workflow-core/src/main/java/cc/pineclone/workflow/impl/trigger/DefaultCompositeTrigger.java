package cc.pineclone.workflow.impl.trigger;

import cc.pineclone.workflow.api.trigger.*;
import cc.pineclone.workflow.api.trigger.event.TriggerEvent;
import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.event.TriggerEventSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public abstract class DefaultCompositeTrigger
        implements CompositeTrigger, TriggerLifecycle {

    private volatile TriggerEventSink sink;
    private final List<Trigger> children;

    private DefaultTriggerEventBuffer childEventBuffer;
    private ExecutorService eventLoopExecutor;

    private volatile boolean closed = false;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected DefaultCompositeTrigger() {
        this.children = new ArrayList<>();
    }

    @Override
    public synchronized void init() {
        checkNotDestroyed();
        if (childEventBuffer != null || eventLoopExecutor != null) {
            throw new IllegalStateException("Trigger already initialized: " + getIdentity());
        }

        childEventBuffer = new DefaultTriggerEventBuffer(new LinkedBlockingQueue<>());
        eventLoopExecutor = Executors.newSingleThreadExecutor();

        eventLoopExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    TriggerEvent event = childEventBuffer.take();  /* 单线程模型，不需要加锁 */
                    handleChildTriggerEvent(event);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (RuntimeException e) {
                    // 子类 handle 逻辑异常：记录并继续，避免线程悄悄退出导致整个 trigger 停摆
                    log.warn("Unhandled exception in event loop of {}", getIdentity(), e);
                }
            }
        });

        children.forEach(t -> t.attach(this.childEventBuffer));
    }

    @Override
    public synchronized void stop() {
        checkNotDestroyed();
        children.forEach(Trigger::detach);
        cleanEventLoopExecutor();
        cleanChildEventBuffer();
    }

    @Override
    public synchronized void close() {
        if (closed) return;
        closed = true;
        children.forEach(Trigger::detach);
        cleanEventLoopExecutor();
        cleanChildEventBuffer();
        children.clear();
        sink = null;
    }

    private void checkNotDestroyed() {
        if (closed) {
            throw new IllegalStateException("Trigger is destroyed: " + getIdentity());
        }
    }

    private void cleanEventLoopExecutor() {
        if (eventLoopExecutor != null) {
            eventLoopExecutor.shutdownNow();
            try {
                if (!eventLoopExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    log.warn("Event loop did not terminate in time for {}", getIdentity());
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
    public List<Trigger> getChildren() {
        return children;
    }

    @Override
    public void addChildren(Trigger trigger) {
        this.children.add(trigger);
    }

    @Override
    public void removeChildren(Trigger trigger) {
        this.children.remove(trigger);
    }

    @Override
    public final void attach(TriggerEventSink sink) {
        if (this.sink != null)
            throw new IllegalStateException("Trigger already attached to an existed sink");
        this.sink = sink;
    }

    @Override
    public final void detach() {
        this.sink = null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DefaultCompositeTrigger that = (DefaultCompositeTrigger) o;
        return Objects.equals(getIdentity(), that.getIdentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentity());
    }

    protected final void emit(TriggerEventIdentity identity) {
        this.emit(identity, Map.of());
    }

    protected final void emit(TriggerEventIdentity identity, Map<String,Object> meta) {
        if (sink == null) throw new IllegalStateException(getIdentity() + " has no sink to emit event");
        sink.emit(new TriggerEvent(this, identity, meta));
    }
}
