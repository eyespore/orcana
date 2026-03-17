package cc.pineclone.eventflow.core.api.trigger;

import cc.pineclone.eventflow.core.api.FlowComponent;
import cc.pineclone.eventflow.core.api.event.EventSink;

public interface Trigger extends FlowComponent, TriggerLifecycle {

    void bind(EventSink sink);

    void unbind();

    boolean isBound();

}
