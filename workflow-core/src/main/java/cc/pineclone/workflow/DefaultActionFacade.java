package cc.pineclone.workflow;

import cc.pineclone.workflow.api.action.ActionFacade;
import cc.pineclone.workflow.api.action.ExecutionProfile;
import cc.pineclone.workflow.api.action.ExecutionStatus;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class DefaultActionFacade implements ActionFacade {

    private final ExecutionProfile profile;
    @Setter private Runnable task;
    private DefaultExecutionState state;
    private final List<Runnable> callbacks = new ArrayList<>();

    public DefaultActionFacade(ExecutionProfile profile) {
        this.profile = profile;
        this.state = new ReadyState();
    }

    private void transitionTo(DefaultExecutionState state) {
        this.state = state;
    }

    @Override
    public ExecutionStatus status() {
        return this.state.status();
    }

    @Override
    public ExecutionProfile profile() {
        return this.profile;
    }

    @Override
    public void halt() {
        this.state.onHalted(this);
    }

    @Override
    public void cont() {
        this.state.onContinued(this);
    }

    @Override
    public void cancel() {
        this.state.onCanceled(this);
    }

    @Override
    public void setCompleted() {
        this.state.onCompleted(this);
    }

    @Override
    public void addCompletedCallback(Runnable callback) {
        callbacks.add(callback);
    }

    private static final class ReadyState implements DefaultExecutionState {
        @Override
        public ExecutionStatus status() {
            return ExecutionStatus.READY;
        }

        @Override
        public void onContinued(DefaultActionFacade facade) {
            facade.transitionTo(new RunningState());
            if (facade.task != null) facade.task.run();
        }

        @Override
        public void onCanceled(DefaultActionFacade facade) {
            facade.transitionTo(new CanceledState());
            facade.callbacks.forEach(Runnable::run);
        }
    }

    private static final class RunningState implements DefaultExecutionState {
        @Override
        public ExecutionStatus status() {
            return ExecutionStatus.RUNNING;
        }

        @Override
        public void onHalted(DefaultActionFacade facade) {
            facade.transitionTo(new HaltedState());
        }

        @Override
        public void onCompleted(DefaultActionFacade facade) {
            facade.transitionTo(new CompletedState());
            facade.callbacks.forEach(Runnable::run);
        }

        @Override
        public void onCanceled(DefaultActionFacade facade) {
            facade.transitionTo(new CanceledState());
            facade.callbacks.forEach(Runnable::run);
        }
    }

    private static final class HaltedState implements DefaultExecutionState {
        @Override
        public ExecutionStatus status() {
            return ExecutionStatus.HALTED;
        }

        @Override
        public void onContinued(DefaultActionFacade facade) {
            facade.transitionTo(new RunningState());
            if (facade.task != null) facade.task.run();
        }

        @Override
        public void onCanceled(DefaultActionFacade facade) {
            facade.transitionTo(new CanceledState());
            facade.callbacks.forEach(Runnable::run);
        }
    }

    private static final class CompletedState implements DefaultExecutionState {
        @Override
        public ExecutionStatus status() {
            return ExecutionStatus.COMPLETED;
        }
    }

    private static final class CanceledState implements DefaultExecutionState {
        @Override
        public ExecutionStatus status() {
            return ExecutionStatus.CANCELED;
        }
    }
}
