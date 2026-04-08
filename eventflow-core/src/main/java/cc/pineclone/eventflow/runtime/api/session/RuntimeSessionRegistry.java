package cc.pineclone.eventflow.runtime.api.session;

import java.util.Collection;
import java.util.Optional;

public interface RuntimeSessionRegistry {

    void register(RuntimeSession session);

    void remove(SessionId sessionId);

    Optional<RuntimeSession> find(SessionId sessionId);

    Collection<RuntimeSession> sessions();

}
