package cc.pineclone.eventflow.runtime.spi;

import cc.pineclone.eventflow.runtime.api.session.RuntimeSession;
import cc.pineclone.eventflow.runtime.api.RootTrigger;
import cc.pineclone.eventflow.runtime.api.session.SessionId;

import java.util.Set;

public interface RootTriggerControl extends RootTrigger {

    void markActive();

    void markInactive();

    void markPending();

    Set<RuntimeSession> currentSessions();

    void addSession(RuntimeSession session);

    void removeSession(SessionId sessionId);

    Status status();

    enum Status {
        ACTIVE,
        PENDING,
        INACTIVE,
    }

}
