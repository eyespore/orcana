package cc.pineclone.workflow.impl.trigger.factory;

import cc.pineclone.workflow.api.trigger.factory.CompositeTriggerDefinition;
import cc.pineclone.workflow.api.trigger.factory.TriggerDefinition;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DefaultCompositeTriggerDefinition implements CompositeTriggerDefinition {

    @EqualsAndHashCode.Include
    private TriggerIdentity identity;
    private List<TriggerDefinition> childDefinitions;

}
