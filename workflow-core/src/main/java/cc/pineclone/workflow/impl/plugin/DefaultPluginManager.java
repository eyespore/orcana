package cc.pineclone.workflow.impl.plugin;

import cc.pineclone.workflow.api.plugin.Plugin;
import cc.pineclone.workflow.api.plugin.PluginLifecycle;
import cc.pineclone.workflow.api.plugin.PluginManager;
import cc.pineclone.workflow.api.plugin.PluginManagerLifecycle;
import cc.pineclone.workflow.api.trigger.*;
import cc.pineclone.workflow.api.trigger.factory.TriggerFactoryRegistrar;

import java.util.*;
import java.util.function.Supplier;

public class DefaultPluginManager implements PluginManager, PluginManagerLifecycle {

    private final Set<String> pluginIds = new HashSet<>();
    private final List<Plugin> plugins = new ArrayList<>();

    private final Supplier<TriggerFactoryRegistrar> triggerFactoryRegistrar;

    public DefaultPluginManager(Supplier<TriggerFactoryRegistrar> triggerFactoryRegistrar) {
        this.triggerFactoryRegistrar = triggerFactoryRegistrar;
    }

    @Override
    public void registerPlugin(Plugin plugin) {
        String pluginId = plugin.getPluginId();  /* 将插件标记为已注册 */
        if (!pluginIds.add(pluginId)) {
            throw new IllegalStateException("Plugin already registered: " + pluginId);
        }
        plugins.add(plugin);
    }

    @Override
    public void init() {
        TriggerFactoryRegistrar registrar = triggerFactoryRegistrar.get();
        plugins.stream()
                .filter(p -> p instanceof TriggerPlugin)
                .map(TriggerPlugin.class::cast)
                .forEach(p -> p.registerTriggerFactories(registrar));

        plugins.stream()
                .filter(p -> p instanceof PluginLifecycle)
                .map(PluginLifecycle.class::cast)
                .sorted(Comparator.comparingInt(PluginLifecycle::order))
                .forEach(PluginLifecycle::init);
    }

    @Override
    public void close() {
        plugins.stream()
                .filter(p -> p instanceof PluginLifecycle)
                .map(PluginLifecycle.class::cast)
                .sorted(Comparator.comparingInt(PluginLifecycle::order).reversed())
                .forEach(PluginLifecycle::close);
    }
}
