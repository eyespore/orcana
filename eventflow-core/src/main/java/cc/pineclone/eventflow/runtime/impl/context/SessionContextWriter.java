package cc.pineclone.eventflow.runtime.impl.context;

import cc.pineclone.eventflow.core.api.context.ContextWriter;
import cc.pineclone.eventflow.runtime.api.session.RuntimeSession;

import java.util.Objects;

@Deprecated
public final class SessionContextWriter implements ContextWriter {

    private final RuntimeSession session;

    public SessionContextWriter(RuntimeSession session) {
        this.session = Objects.requireNonNull(session, "session");
    }

    @Override
    public void write(String key, Object val) {
        session.vars().put(key, val);
    }
}
