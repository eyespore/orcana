package cc.pineclone.eventflow.runtime.impl.context;

import cc.pineclone.eventflow.common.api.value.ValueBinding;
import cc.pineclone.eventflow.core.api.context.ContextControl;
import cc.pineclone.eventflow.core.api.context.ContextReader;
import cc.pineclone.eventflow.core.api.context.ContextWriter;
import cc.pineclone.eventflow.runtime.api.session.GlobalSession;
import cc.pineclone.eventflow.runtime.api.session.PathValueAccessor;
import cc.pineclone.eventflow.runtime.api.session.RuntimeSession;

import java.util.Map;
import java.util.Objects;

public final class SessionContext
        implements ContextControl, ContextWriter, ContextReader {

    private final RuntimeSession runtimeSession;
    private final GlobalSession globalSession;

    private final Map<String, Object> eventPayload;
    private final Map<String, Object> commandParams;

    private final PathValueAccessor pathValueAccessor;

    public SessionContext(RuntimeSession runtimeSession, GlobalSession globalSession, PathValueAccessor pathValueAccessor, Map<String, Object> eventPayload, Map<String, Object> commandParams) {
        this.runtimeSession = Objects.requireNonNull(runtimeSession, "session");
        this.globalSession = globalSession;
        this.pathValueAccessor = pathValueAccessor;
        this.eventPayload = eventPayload;
        this.commandParams = commandParams;
    }

    @Override
    public boolean isActive() {
        return runtimeSession.isActive();
    }

    @Override
    public Object read(ValueBinding ref) {
        return null;
    }

    @Override
    public void write(String key, Object val) {

    }
}
