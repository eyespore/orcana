package cc.pineclone.workflow.impl.trigger;

import cc.pineclone.workflow.api.trigger.TriggerAdmin;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.api.trigger.event.TriggerEventSink;
import cc.pineclone.workflow.api.trigger.factory.TriggerDefinition;
import cc.pineclone.workflow.api.trigger.registry.TriggerRegistry;
import cc.pineclone.workflow.api.trigger.registry.TriggerRegistryRuntimeAccess;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 受控管理端口：对外提供 deploy/undeploy/activate/deactivate。
 * <p>
 * 设计要点：
 * <ul>
 *   <li>attach/detach 不直接暴露 Trigger 实例，而是通过 TriggerRegistryRuntimeAccess 受控执行</li>
 *   <li>activate/deactivate 幂等：用 activeCount 做边界，0->1 attach + retain；1->0 detach + release</li>
 * </ul>
 */
public class DefaultTriggerAdmin implements TriggerAdmin {

    private final TriggerRegistry registry;
    private final TriggerRegistryRuntimeAccess runtimeAccess;
    private final TriggerEventSink sink;

    /**
     * Admin 层“激活计数”（与 registry.refCount 语义不同）：
     * - activeCount：是否接入事件管线（attach/detach 的边界）
     * - registry.refCount：是否持有运行时资源（init/destroy 的边界）
     */
    private final Map<TriggerIdentity, Integer> activeCounts = new HashMap<>();

    public DefaultTriggerAdmin(
            TriggerRegistry registry,
            TriggerRegistryRuntimeAccess runtimeAccess,
            TriggerEventSink sink) {

        this.registry = Objects.requireNonNull(registry, "registry");
        this.runtimeAccess = Objects.requireNonNull(runtimeAccess, "runtimeAccess");
        this.sink = Objects.requireNonNull(sink, "sink");
    }

    @Override
    public TriggerIdentity deploy(TriggerDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        return registry.register(definition);
    }

    @Override
    public void undeploy(TriggerIdentity rootId) {
        Objects.requireNonNull(rootId, "rootId");

        int active = activeCounts.getOrDefault(rootId, 0);
        if (active > 0) {
            throw new IllegalStateException("Cannot undeploy an active root trigger: " + rootId + " (activeCount=" + active + ")");
        }

        registry.unregister(rootId);
        activeCounts.remove(rootId);
    }

    @Override
    public synchronized void activate(TriggerIdentity rootId) {
        Objects.requireNonNull(rootId, "rootId");
        if (!registry.contains(rootId)) {
            throw new IllegalArgumentException("Unknown root identity " + rootId);
        }

        int next = activeCounts.getOrDefault(rootId, 0) + 1;
        activeCounts.put(rootId, next);

        if (next != 1) return; // 幂等：只有 0->1 才真的接入事件管线

        // 先 retain，确保 trigger tree init 完成；然后 attach 到 sink
        registry.retain(rootId);
        try {
            runtimeAccess.withRootTrigger(rootId, handle -> handle.attach(sink));
        } catch (RuntimeException e) {
            // attach 失败要回滚 retain，避免 refCount 泄漏
            try {
                registry.release(rootId);
            } finally {
                activeCounts.put(rootId, 0);
            }
            throw e;
        }
    }

    @Override
    public synchronized void deactivate(TriggerIdentity rootId) {
        Objects.requireNonNull(rootId, "rootId");

        Integer current = activeCounts.get(rootId);
        if (current == null || current <= 0) return; // 幂等：多次 deactivate 没影响

        int next = current - 1;
        activeCounts.put(rootId, next);

        if (next != 0) return;  /* 仍然持有引用 */

        // 1->0：先 detach 再 release（把事件源断开，再释放资源）
        try {
            runtimeAccess.withRootTrigger(rootId, TriggerRegistryRuntimeAccess.RootTriggerHandle::detach);
        } finally {
            registry.release(rootId);
            activeCounts.remove(rootId);
        }
    }
}
