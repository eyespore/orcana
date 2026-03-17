package cc.pineclone.eventflow.runtime.impl.action.ctx;

public class DefaultGlobalContextFactory implements GlobalContextFactory {
    @Override
    public GlobalContext createContext() {
        return new DefaultGlobalContext();
    }
}
