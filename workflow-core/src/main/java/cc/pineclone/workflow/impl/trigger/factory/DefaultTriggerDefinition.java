package cc.pineclone.workflow.impl.trigger.factory;

import cc.pineclone.workflow.api.trigger.factory.TriggerDefinition;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DefaultTriggerDefinition implements TriggerDefinition {

    @EqualsAndHashCode.Include
    private TriggerIdentity identity;

}
