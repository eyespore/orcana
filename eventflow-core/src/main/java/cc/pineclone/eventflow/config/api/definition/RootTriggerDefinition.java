package cc.pineclone.eventflow.config.api.definition;

import cc.pineclone.eventflow.core.api.ConcurrencyPolicy;

import java.util.Objects;
import java.util.Set;

public record RootTriggerDefinition(
        Set<String> groups,  /* 大小写敏感 */
        TriggerDefinition triggerDefinition,
        ConcurrencyPolicy concurrencyPolicy
) {

    public RootTriggerDefinition {
        Objects.requireNonNull(triggerDefinition, "triggerDefinition");
        Objects.requireNonNull(concurrencyPolicy, "concurrencyPolicy");
    }

}
