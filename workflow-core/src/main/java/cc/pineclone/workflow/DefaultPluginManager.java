package cc.pineclone.workflow;

import cc.pineclone.workflow.api.*;
import cc.pineclone.workflow.api.trigger.*;
import cc.pineclone.workflow.trigger.AnyOrderTriggerFactory;

import java.util.*;

public class DefaultPluginManager implements PluginManager {

    private final Set<String> pluginIds = new HashSet<>();
    private final List<WorkflowPlugin> plugins = new ArrayList<>();

    private final TriggerFactoryRegistrar triggerFactoryRegistrar;

    public DefaultPluginManager(TriggerFactoryRegistrar triggerFactoryRegistrar) {
        this.triggerFactoryRegistrar = triggerFactoryRegistrar;
    }

    @Override
    public void registerPlugin(WorkflowPlugin plugin) {
        String pluginId = plugin.getPluginId();  /* 将插件标记为已注册 */
        if (!pluginIds.add(pluginId)) {
            throw new IllegalStateException("Plugin already registered: " + pluginId);
        }

        if (plugin instanceof TriggerPlugin triggerPlugin) {  /* 注册 TriggerPlugin */
            triggerPlugin.registerTriggerFactories(triggerFactoryRegistrar);
        }
        plugins.add(plugin);
    }

    @Override
    public void initializePlugins() {
        plugins.stream()
                .sorted(Comparator.comparingInt(WorkflowPlugin::order))
                .forEach(WorkflowPlugin::onInitialized);
    }

    @Override
    public void startPlugins() {
        plugins.stream()
                .sorted(Comparator.comparingInt(WorkflowPlugin::order))
                .forEach(WorkflowPlugin::onStarted);
    }

    @Override
    public void stopPlugins() {
        plugins.stream()
                .sorted(Comparator.comparingInt(WorkflowPlugin::order).reversed())
                .forEach(WorkflowPlugin::onStopped);
    }

    @Override
    public void destroyPlugins() {
        plugins.stream()
                .sorted(Comparator.comparingInt(WorkflowPlugin::order).reversed())
                .forEach(WorkflowPlugin::onDestroyed);
    }
}
