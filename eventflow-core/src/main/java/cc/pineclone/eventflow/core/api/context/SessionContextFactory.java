package cc.pineclone.eventflow.core.api.context;

import cc.pineclone.eventflow.core.api.action.ActionIdentity;

import java.time.Instant;
import java.util.UUID;

@Deprecated
public interface SessionContextFactory {

    SessionContext createContext(
            ActionIdentity actionIdentity,
            UUID sessionId,
            Instant createdAt
    );

}
