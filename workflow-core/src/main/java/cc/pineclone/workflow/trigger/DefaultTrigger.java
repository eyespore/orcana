package cc.pineclone.workflow.trigger;

import cc.pineclone.workflow.api.trigger.Trigger;
import cc.pineclone.workflow.api.trigger.TriggerEvent;
import cc.pineclone.workflow.api.trigger.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerEventSink;

import java.util.Map;
import java.util.Objects;

public abstract class DefaultTrigger implements Trigger {

    private volatile TriggerEventSink sink;

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

    protected final void emit(TriggerEventIdentity identity) {
        this.emit(identity, Map.of());
    }

    protected final void emit(TriggerEventIdentity identity, Map<String,Object> meta) {
        if (sink == null) throw new IllegalStateException(getIdentity() + " has no sink to emit event");
        sink.emit(new TriggerEvent(this, identity, meta));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DefaultTrigger that = (DefaultTrigger) o;
        return Objects.equals(getIdentity(), that.getIdentity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentity());
    }
}
