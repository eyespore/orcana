package cc.pineclone.eventflow.runtime.impl.context;

import cc.pineclone.eventflow.core.api.context.ActionContext;
import cc.pineclone.eventflow.core.api.context.ContextControl;
import cc.pineclone.eventflow.core.api.context.ContextReader;
import cc.pineclone.eventflow.core.api.context.ContextWriter;
import cc.pineclone.eventflow.core.api.event.EventSink;

public class DefaultActionContext implements ActionContext {

    private final SessionContext sessionContext;
    private final EventSink eventSink;

    public DefaultActionContext(SessionContext sessionContext, EventSink eventSink) {
        this.sessionContext = sessionContext;
        this.eventSink = eventSink;
    }

    @Override
    public ContextControl control() {
        return sessionContext;
    }

    @Override
    public ContextReader reader() {
        return sessionContext;
    }

    @Override
    public ContextWriter writer() {
        return sessionContext;
    }

    @Override
    public EventSink eventSink() {
        return eventSink;
    }
}
