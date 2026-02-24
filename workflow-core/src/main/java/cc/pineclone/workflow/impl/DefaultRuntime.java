package cc.pineclone.workflow.impl;

import cc.pineclone.workflow.api.plugin.PluginManager;
import cc.pineclone.workflow.api.Runtime;
import cc.pineclone.workflow.api.plugin.Plugin;
import cc.pineclone.workflow.api.plugin.PluginManagerLifecycle;
import cc.pineclone.workflow.api.trigger.*;
import cc.pineclone.workflow.impl.plugin.DefaultPluginManager;
import cc.pineclone.workflow.impl.trigger.DefaultTriggerService;

public class DefaultRuntime implements Runtime {

    private final TriggerService triggerService;
    private final PluginManager pluginManager;

    public DefaultRuntime() {
        this.triggerService = new DefaultTriggerService();
        this.pluginManager = new DefaultPluginManager(this.triggerService::factories);
    }

    public DefaultRuntime(
            TriggerService triggerService,
            PluginManager pluginManager
    ) {
        this.triggerService = triggerService;
        this.pluginManager = pluginManager;
    }

    @Override
    public void registerPlugin(Plugin plugin) {
        this.pluginManager.registerPlugin(plugin);
    }

    @Override
    public TriggerService triggerService() {
        return triggerService;
    }

    @Override
    public void init() {
        if (this.triggerService instanceof TriggerServiceLifecycle lifecycle) {
            lifecycle.init();
        }

        if (this.pluginManager instanceof PluginManagerLifecycle lifecycle) {
            lifecycle.init();
        }
    }

    @Override
    public void start() {
        // 事件消费线程由 Runtime 自己启动与管理（此处可按你的 ActionCoordinator/Router 逻辑接入）
        // 例如：new Thread(() -> loop(eventSource)).start();
    }

    @Override
    public void stop() {
        if (this.pluginManager instanceof PluginManagerLifecycle lifecycle) {
            lifecycle.close();
        }

        if (this.triggerService instanceof TriggerServiceLifecycle lifecycle) {
            lifecycle.close();
        }
    }
}
