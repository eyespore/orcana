package cc.pineclone.eventflow.core.api.context;

import cc.pineclone.eventflow.core.api.ComponentId;

import java.util.Set;

public interface FlowSession extends Session {

    ComponentId originalRootTriggerId();

    Set<String> originalRootTriggerGroups();

    Status status();

    default boolean isActive() {
        return status() == Status.ACTIVE;
    }

    enum Status {
        ACTIVE,
        CANCEL_REQUESTED,
        COMPLETED,
        CANCELED,
        FAILED
    }
}
