package cc.pineclone.eventflow.runtime.impl.trigger.factory;

import cc.pineclone.eventflow.config.api.CompositeTriggerDefinition;
import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;
import cc.pineclone.eventflow.core.api.ComponentId;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Deprecated
public abstract class DefaultCompositeTriggerDefinition
        implements CompositeTriggerDefinition {

    @EqualsAndHashCode.Include
    private ComponentId identity;
    private List<TriggerDefinition> children;

    @Override
    public ComponentId identity() {
        return identity;
    }

    @Override
    public List<TriggerDefinition> children() {
        return children;
    }
}
