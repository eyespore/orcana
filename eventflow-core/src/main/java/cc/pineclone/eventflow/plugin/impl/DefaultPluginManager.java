package cc.pineclone.eventflow.plugin.impl;

import cc.pineclone.eventflow.plugin.api.ComponentTemplateRegistrar;
import cc.pineclone.eventflow.plugin.api.Plugin;
import cc.pineclone.eventflow.plugin.api.PluginLifecycle;
import cc.pineclone.eventflow.plugin.api.PluginManager;
import cc.pineclone.eventflow.plugin.api.PluginManagerLifecycle;

import java.util.*;
import java.util.function.Supplier;

public class DefaultPluginManager implements PluginManager, PluginManagerLifecycle {

    private final Set<String> pluginIds = new HashSet<>();
    private final List<Plugin> plugins = new ArrayList<>();

    private final Supplier<ComponentTemplateRegistrar> triggerTemplateRegistrar;

    public DefaultPluginManager(Supplier<ComponentTemplateRegistrar> triggerTemplateRegistrar) {
        this.triggerTemplateRegistrar = triggerTemplateRegistrar;
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
        ComponentTemplateRegistrar registrar = triggerTemplateRegistrar.get();
        plugins.forEach(p -> p.registerComponentTemplate(registrar));

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
