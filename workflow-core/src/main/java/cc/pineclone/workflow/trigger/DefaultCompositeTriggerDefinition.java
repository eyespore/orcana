package cc.pineclone.workflow.trigger;

import cc.pineclone.workflow.api.trigger.CompositeTriggerDefinition;
import cc.pineclone.workflow.api.trigger.TriggerDefinition;
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
