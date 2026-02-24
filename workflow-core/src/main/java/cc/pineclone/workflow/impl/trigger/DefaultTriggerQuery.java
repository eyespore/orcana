package cc.pineclone.workflow.impl.trigger;

import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.api.trigger.TriggerQuery;
import cc.pineclone.workflow.api.trigger.registry.TriggerRegistry;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class DefaultTriggerQuery implements TriggerQuery {

    private final TriggerRegistry registry;

    public DefaultTriggerQuery(TriggerRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    @Override
    public List<TriggerIdentity> listDeployed() {
        // registry 已经返回 root ids；这里额外排序保证稳定输出（可选）
        return registry.listRootIdentities().stream()
                .sorted(Comparator.comparing(TriggerIdentity::toString))
                .toList();
    }

    @Override
    public boolean isDeployed(TriggerIdentity rootId) {
        Objects.requireNonNull(rootId, "rootId");
        return registry.contains(rootId);
    }
}
