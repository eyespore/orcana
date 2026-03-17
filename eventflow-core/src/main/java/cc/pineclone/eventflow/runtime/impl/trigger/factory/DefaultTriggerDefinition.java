package cc.pineclone.eventflow.runtime.impl.trigger.factory;

import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;
import cc.pineclone.eventflow.core.api.ComponentId;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Deprecated
public class DefaultTriggerDefinition implements TriggerDefinition {

    @EqualsAndHashCode.Include
    private ComponentId identity;

    @Override
    public ComponentId identity() {
        return identity;
    }
}
