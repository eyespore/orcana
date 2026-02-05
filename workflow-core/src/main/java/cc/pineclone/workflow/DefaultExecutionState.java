package cc.pineclone.workflow;

import cc.pineclone.workflow.api.action.ExecutionStatus;

public interface DefaultExecutionState {

    ExecutionStatus status();

    default void onContinued(DefaultActionFacade facade) {
        throw illegal("cont");
    }

    default void onHalted(DefaultActionFacade facade) {
        throw illegal("halt");
    }

    default void onCanceled(DefaultActionFacade facade) {
        throw illegal("cancel");
    }

    default void onCompleted(DefaultActionFacade facade) {
        throw illegal("complete");
    }

    default RuntimeException illegal(String action) {
        return new IllegalStateException(
                getClass().getSimpleName() + " does not support " + action
        );
    }
}
