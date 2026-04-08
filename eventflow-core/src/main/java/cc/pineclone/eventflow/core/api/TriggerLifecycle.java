package cc.pineclone.eventflow.core.api;

public interface TriggerLifecycle {

    default void init() {}

    default void start() {}

    default void stop() {}

}
