package cc.pineclone.eventflow.runtime.impl.trigger;

import cc.pineclone.eventflow.core.api.trigger.Trigger;
import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.core.api.event.EventSink;

import java.util.Map;
import java.util.Objects;

public abstract class DefaultTrigger implements Trigger {

    private volatile EventSink sink;

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

    protected final void emit(EventSelector identity) {
        this.emit(identity, Map.of());
    }

    protected final void emit(EventSelector identity, Map<String,Object> meta) {
        if (sink == null) throw new IllegalStateException(id() + " has no sink to emit event");
        sink.emit(new Event(this, identity, meta));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DefaultTrigger that = (DefaultTrigger) o;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }
}
