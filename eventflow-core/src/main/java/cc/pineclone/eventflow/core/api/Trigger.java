package cc.pineclone.eventflow.core.api;

import cc.pineclone.eventflow.core.api.event.EventSink;

public interface Trigger extends CoreComponent, TriggerLifecycle {

    void bind(EventSink sink);

    void unbind();

    boolean isBound();

}
