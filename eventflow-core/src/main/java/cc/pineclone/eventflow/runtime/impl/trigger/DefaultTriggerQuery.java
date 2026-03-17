package cc.pineclone.eventflow.runtime.impl.trigger;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.runtime.api.TriggerQuery;
import cc.pineclone.eventflow.runtime.api.TriggerRegistry;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class DefaultTriggerQuery implements TriggerQuery {

    private final TriggerRegistry registry;

    public DefaultTriggerQuery(TriggerRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public List<ComponentId> listDeployed() {
        // registry 已经返回 root ids；这里额外排序保证稳定输出（可选）
        return registry.listRootIdentities().stream()
                .sorted(Comparator.comparing(ComponentId::toString))
                .toList();
    }

    @Override
    public boolean isDeployed(ComponentId rootId) {
        Objects.requireNonNull(rootId, "rootId");
        return registry.contains(rootId);
    }
}
