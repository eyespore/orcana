package cc.pineclone.eventflow.runtime.spi;

import cc.pineclone.eventflow.runtime.api.session.RuntimeSession;

public interface RuntimeSessionControl extends RuntimeSession {

    boolean retain();

    void release();

    void markCancelRequested();

    void markFailed(Throwable error);

    void onTerminated(Runnable callback);

}
