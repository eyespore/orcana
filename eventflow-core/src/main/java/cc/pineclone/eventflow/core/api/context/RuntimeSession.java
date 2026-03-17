package cc.pineclone.eventflow.core.api.context;

public interface RuntimeSession extends Session {

    Status status();

    default boolean isActive() {
        return status() == Status.RUNNING;
    }

    enum Status {
        RUNNING,
        STOPPING,
        STOPPED,
    }
}
