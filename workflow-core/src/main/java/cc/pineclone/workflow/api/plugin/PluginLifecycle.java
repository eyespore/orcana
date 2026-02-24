package cc.pineclone.workflow.api.plugin;

public interface PluginLifecycle extends AutoCloseable {

    default void init() {}

    @Override
    default void close() {}

    int order();

}
