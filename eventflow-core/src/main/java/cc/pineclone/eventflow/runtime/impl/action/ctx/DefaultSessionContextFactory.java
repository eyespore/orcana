package cc.pineclone.eventflow.runtime.impl.action.ctx;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class DefaultSessionContextFactory implements SessionContextFactory {

    @Override
    public SessionContext createContext(ActionIdentity actionIdentity, UUID sessionId, Instant createdAt) {
        Objects.requireNonNull(actionIdentity, "actionIdentity");
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(createdAt, "createdAt");

        DefaultSessionContext ctx = new DefaultSessionContext();
        ctx.put("action.domain", actionIdentity.domain());
        ctx.put("action.name", actionIdentity.name());
        ctx.put("session.id", sessionId);
        ctx.put("session.createdAt", createdAt);
        return ctx;
    }



}
