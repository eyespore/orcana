package cc.pineclone.eventflow.runtime.impl.trigger;

import cc.pineclone.eventflow.plugin.api.ComponentTemplateProvider;
import cc.pineclone.eventflow.plugin.api.ComponentTemplateRegistrar;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.event.EventSource;
import cc.pineclone.eventflow.plugin.api.template.TriggerTemplate;
import cc.pineclone.eventflow.runtime.api.*;
import cc.pineclone.eventflow.runtime.impl.trigger.factory.DefaultTriggerTemplateManager;
import cc.pineclone.eventflow.runtime.impl.trigger.registry.DefaultTriggerRegistry;

import java.util.Objects;

public class DefaultTriggerService implements TriggerService, TriggerServiceLifecycle {

    private final ComponentTemplateRegistrar<TriggerTemplate> triggerTemplateRegistrar;
    private final ComponentTemplateProvider<TriggerTemplate> triggerTemplateProvider;

    private final TriggerRegistry registry;

    private final DefaultTriggerEventBuffer buffer;  /* 事件消息队列 */

    private final TriggerAdmin admin;
    private final TriggerQuery query;

    private volatile boolean initialized = false;
    private volatile boolean closed = false;

    public DefaultTriggerService() {
        DefaultTriggerTemplateManager triggerTemplateManager = new DefaultTriggerTemplateManager();

        this.triggerTemplateRegistrar = triggerTemplateManager;
        this.triggerTemplateProvider = triggerTemplateManager;

        this.registry = new DefaultTriggerRegistry(triggerTemplateManager);
        TriggerRegistryRuntimeAccess runtimeAccess = (TriggerRegistryRuntimeAccess) registry;

        this.buffer = new DefaultTriggerEventBuffer();

        this.admin = new DefaultTriggerAdmin(registry, runtimeAccess, buffer);
        this.query = new DefaultTriggerQuery(registry);

    }

    @Deprecated
    public DefaultTriggerService(
            ComponentTemplateRegistrar<TriggerTemplate> triggerTemplateRegistrar,
            ComponentTemplateProvider<TriggerTemplate> triggerTemplateProvider
    ) {
        this.triggerTemplateRegistrar = Objects.requireNonNull(triggerTemplateRegistrar, "triggerTemplateRegistrar");
        this.triggerTemplateProvider = Objects.requireNonNull(triggerTemplateProvider, "triggerTemplateProvider");

        this.registry = new DefaultTriggerRegistry(triggerTemplateProvider);
        TriggerRegistryRuntimeAccess runtimeAccess = (TriggerRegistryRuntimeAccess) registry;

        this.buffer = new DefaultTriggerEventBuffer();

        this.admin = new DefaultTriggerAdmin(registry, runtimeAccess, buffer);
        this.query = new DefaultTriggerQuery(registry);
    }

    @Override
    public void init() {
        if (closed) throw new IllegalStateException("TriggerService is closed");
        initialized = true;
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;

        // 生产侧收尾：尽力停掉所有已部署的 root rootTriggers（幂等）
        // 注意：消费线程由 Runtime 管理（IoC），这里不负责唤醒 take()
        try {
            for (ComponentId rootId : registry.listRootIdentities()) {
                try {
                    admin.deactivate(rootId);
                } catch (RuntimeException ignored) {
                    // close 阶段尽力而为：不让单个 root 影响整体收尾
                }
            }
        } finally {
            buffer.close();
        }
    }

    @Override
    public TriggerAdmin admin() {
        if (!initialized) throw new IllegalStateException("TriggerService not initialized");
        if (closed) throw new IllegalStateException("TriggerService is closed");
        return admin;
    }

    @Override
    public TriggerQuery query() {
        if (!initialized) throw new IllegalStateException("TriggerService not initialized");
        if (closed) throw new IllegalStateException("TriggerService is closed");
        return query;
    }

    @Override
    public EventSource events() {
        if (!initialized) throw new IllegalStateException("TriggerService not initialized");
        if (closed) throw new IllegalStateException("TriggerService is closed");
        return buffer;
    }

    @Override
    public ComponentTemplateRegistrar<TriggerTemplate> triggerTemplateRegistrar() {
        if (!initialized) throw new IllegalStateException("TriggerService not initialized");
        if (closed) throw new IllegalStateException("TriggerService is closed");
        return triggerTemplateRegistrar;
    }
}
