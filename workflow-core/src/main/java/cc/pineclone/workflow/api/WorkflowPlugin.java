package cc.pineclone.workflow.api;

public interface WorkflowPlugin {

    String getPluginId();

    default void onInitialized() {}

    default void onStarted() {}

    default void onStopped() {}

    default void onDestroyed() {}

    int order();

}
