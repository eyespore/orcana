package cc.pineclone.workflow.trigger;

import cc.pineclone.workflow.api.trigger.*;

import java.util.*;
import java.util.concurrent.*;

public abstract class DefaultCompositeTrigger implements CompositeTrigger, TriggerLifecycleAware {

    private volatile TriggerEventSink sink;
    private final List<Trigger> children;

    private DefaultTriggerEventDispatcher childEventDispatcher;
    private ExecutorService eventLoopExecutor;

    protected DefaultCompositeTrigger() {
        this.children = new ArrayList<>();
    }

    @Override
    public void init() {
        childEventDispatcher = new DefaultTriggerEventDispatcher(new LinkedBlockingQueue<>());
        eventLoopExecutor = Executors.newSingleThreadScheduledExecutor();

        eventLoopExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    TriggerEvent event = childEventDispatcher.take();  /* 单线程模型，不需要加锁 */
                    handleChildTriggerEvent(event);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        children.forEach(t -> t.attach(this.childEventDispatcher));
    }

    @Override
    public void destroy() {
        children.forEach(Trigger::detach);
        childEventDispatcher.clear();
        eventLoopExecutor.shutdown();
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
