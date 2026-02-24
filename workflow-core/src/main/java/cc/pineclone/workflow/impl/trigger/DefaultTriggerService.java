package cc.pineclone.workflow.impl.trigger;

import cc.pineclone.workflow.api.trigger.*;
import cc.pineclone.workflow.api.trigger.event.TriggerEventBuffer;
import cc.pineclone.workflow.api.trigger.event.TriggerEventSource;
import cc.pineclone.workflow.api.trigger.factory.TriggerFactoryManager;
import cc.pineclone.workflow.api.trigger.factory.TriggerFactoryRegistrar;
import cc.pineclone.workflow.api.trigger.registry.TriggerRegistry;
import cc.pineclone.workflow.api.trigger.registry.TriggerRegistryRuntimeAccess;
import cc.pineclone.workflow.impl.trigger.factory.DefaultTriggerFactoryManager;
import cc.pineclone.workflow.impl.trigger.registry.DefaultTriggerRegistry;

import java.util.Objects;

public class DefaultTriggerService implements TriggerService, TriggerServiceLifecycle {

    private final TriggerFactoryManager factoryManager;
    private final TriggerRegistry registry;

    private final TriggerEventBuffer buffer;  /* 事件消息队列 */

    private final TriggerAdmin admin;
    private final TriggerQuery query;

    private volatile boolean initialized = false;
    private volatile boolean closed = false;

    public DefaultTriggerService() {
        this(new DefaultTriggerFactoryManager());
    }

    public DefaultTriggerService(TriggerFactoryManager factoryManager) {
        this.factoryManager = Objects.requireNonNull(factoryManager, "factoryManager");

        this.registry = new DefaultTriggerRegistry(factoryManager);
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

        // 生产侧收尾：尽力停掉所有已部署的 root triggers（幂等）
        // 注意：消费线程由 Runtime 管理（IoC），这里不负责唤醒 take()
        try {
            for (TriggerIdentity rootId : registry.listRootIdentities()) {
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
    public TriggerEventSource events() {
        if (!initialized) throw new IllegalStateException("TriggerService not initialized");
        if (closed) throw new IllegalStateException("TriggerService is closed");
        return buffer;
    }

    @Override
    public TriggerFactoryRegistrar factories() {
        if (!initialized) throw new IllegalStateException("TriggerService not initialized");
        if (closed) throw new IllegalStateException("TriggerService is closed");
        return factoryManager;
    }
}
