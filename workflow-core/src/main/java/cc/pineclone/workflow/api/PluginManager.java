package cc.pineclone.workflow.api;

import java.util.List;

public interface PluginManager {

    void registerPlugin(WorkflowPlugin plugin);

    void initializePlugins();

    void startPlugins();

    void stopPlugins();

    void destroyPlugins();

}
