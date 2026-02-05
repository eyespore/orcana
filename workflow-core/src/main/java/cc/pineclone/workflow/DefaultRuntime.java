package cc.pineclone.workflow;

import cc.pineclone.workflow.api.*;
import cc.pineclone.workflow.api.Runtime;
import cc.pineclone.workflow.api.action.ExecutionCoordinator;
import cc.pineclone.workflow.api.action.Executor;
import cc.pineclone.workflow.api.trigger.Trigger;

public class DefaultRuntime implements Runtime {

    private DefaultRuntimeState state;
//    private final Trigger trigger;
    private final Executor executor;
    private final ExecutionCoordinator coordinator;

    public DefaultRuntime(Trigger trigger, Executor executor, ExecutionCoordinator coordinator) {
        this.state = new CreatedState();
//        this.trigger = trigger;
        this.executor = executor;
        this.coordinator = coordinator;
    }

    void transitionTo(DefaultRuntimeState state) {
        this.state = state;
    }

    @Override
    public void launch() {
        this.state.onLaunched(this);
    }

    @Override
    public void suspend() {
        this.state.onSuspended(this);
    }

    @Override
    public void resume() {
        this.state.onResumed(this);
    }

    @Override
    public void terminate() {
        this.state.onTerminated(this);
    }

    @Override
    public RuntimeStatus status() {
        return state.status();
    }

//    @Override
//    public void onTriggerEvent(TriggerEvent event) {
//        if (this.state.status() != RuntimeStatus.RUNNING) return;
//
//        ExecutionContext ctx = new ExecutionContext();
//        event.getMeta().forEach(ctx::put);
//        ExecutionFacade handler = executor.execute(ctx);
//
//        coordinator.decide(handler);  /* 由协调者决定执行逻辑 */
//        handler.addCompletedCallback(() -> coordinator.onCompleted(handler));  /* 执行完毕后触发回调 */
//    }

    private static final class CreatedState implements DefaultRuntimeState {
        @Override
        public RuntimeStatus status() {
            return RuntimeStatus.CREATED;
        }

        @Override
        public void onLaunched(DefaultRuntime context) {
            context.transitionTo(new RunningState());
//            context.trigger.registerListener(context);  // 注册为 Trigger listener
        }

        @Override
        public void onTerminated(DefaultRuntime context) {  /* 终止 Runtime */
            context.transitionTo(new TerminatedState());
//            context.trigger.unregisterListener(context);
        }
    }

    private static final class SuspendedState implements DefaultRuntimeState {
        @Override
        public RuntimeStatus status() {
            return RuntimeStatus.SUSPENDED;
        }

        @Override
        public void onResumed(DefaultRuntime context) {
            context.transitionTo(new RunningState());
        }

        @Override
        public void onTerminated(DefaultRuntime context) {
            context.transitionTo(new TerminatedState());
//            context.trigger.unregisterListener(context);
        }
    }

    private static final class RunningState implements DefaultRuntimeState {
        @Override
        public RuntimeStatus status() {
            return RuntimeStatus.RUNNING;
        }

        @Override
        public void onSuspended(DefaultRuntime context) {
            context.transitionTo(new SuspendedState());
        }

        @Override
        public void onTerminated(DefaultRuntime context) {
            context.transitionTo(new TerminatedState());
//            context.trigger.unregisterListener(context);
        }
    }

    private static final class TerminatedState implements DefaultRuntimeState {
        @Override
        public RuntimeStatus status() {
            return RuntimeStatus.TERMINATED;
        }
    }
}
