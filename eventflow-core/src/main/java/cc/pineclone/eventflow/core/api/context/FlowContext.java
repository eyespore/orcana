package cc.pineclone.eventflow.core.api.context;

public interface FlowContext {

    RuntimeSession runtime();

    FlowSession session();

    default boolean isActive() {
        return runtime().isActive() && session().isActive();
    }

}
