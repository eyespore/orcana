package cc.pineclone.eventflow.runtime.impl.context;

import cc.pineclone.eventflow.core.api.context.ContextControl;
import cc.pineclone.eventflow.core.api.context.ContextReader;
import cc.pineclone.eventflow.core.api.context.ContextWriter;
import cc.pineclone.eventflow.core.api.context.MapperContext;

public class DefaultMapperContext implements MapperContext {

    private final SessionContext sessionContext;

    public DefaultMapperContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
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
}
