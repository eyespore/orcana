package cc.pineclone.workflow;

import cc.pineclone.workflow.api.RuntimeStatus;

public interface DefaultRuntimeState {

    RuntimeStatus status();

    default void onLaunched(DefaultRuntime context) {
        throw illegal("launch");
    }

    default void onSuspended(DefaultRuntime context) {
        throw illegal("suspend");
    }

    default void onResumed(DefaultRuntime context) {
        throw illegal("resume");
    }

    default void onTerminated(DefaultRuntime context) {
        throw illegal("terminate");
    }

    default void onCreated(DefaultRuntime context) {
        throw illegal("created");
    }

    default RuntimeException illegal(String action) {
        return new IllegalStateException(
                getClass().getSimpleName() + " does not support " + action
        );
    }
}
