package cc.pineclone.eventflow.runtime.impl.action.ctx;

import cc.pineclone.eventflow.core.api.context.GlobalContext;
import cc.pineclone.eventflow.core.api.context.GlobalContextFactory;

public class DefaultGlobalContextFactory implements GlobalContextFactory {
    @Override
    public GlobalContext createContext() {
        return new DefaultGlobalContext();
    }
}
