package cc.pineclone.workflow.api.plugin;

public interface PluginManagerLifecycle extends AutoCloseable {

    void init();

    @Override
    void close();

}
