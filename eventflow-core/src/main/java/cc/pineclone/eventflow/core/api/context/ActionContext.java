package cc.pineclone.eventflow.core.api.context;

import cc.pineclone.eventflow.core.api.event.EventSink;

public interface ActionContext {

    ContextControl control();

    ContextReader reader();

    ContextWriter writer();

    EventSink eventSink();

}
