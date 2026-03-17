package cc.pineclone.eventflow.plugin.api;

public interface PluginLifecycle extends AutoCloseable {

    default void init() {}

    @Override
    default void close() {}

    int order();

}
