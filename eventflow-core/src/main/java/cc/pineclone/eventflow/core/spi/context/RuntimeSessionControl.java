package cc.pineclone.eventflow.core.spi.context;

import cc.pineclone.eventflow.core.api.context.RuntimeSession;

public interface RuntimeSessionControl extends RuntimeSession {

    void cancel();

    void cancel(String reason);

}
