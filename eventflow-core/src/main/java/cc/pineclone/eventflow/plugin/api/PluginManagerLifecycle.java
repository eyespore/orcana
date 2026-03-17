package cc.pineclone.eventflow.plugin.api;

public interface PluginManagerLifecycle extends AutoCloseable {

    void init();

    @Override
    void close();

}
