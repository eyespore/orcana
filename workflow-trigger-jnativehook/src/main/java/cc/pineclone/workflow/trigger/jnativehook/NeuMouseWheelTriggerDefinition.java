package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.NeuMouseWheelSpec;
import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.impl.trigger.factory.DefaultTriggerDefinition;
import cc.pineclone.workflow.trigger.jnativehook.api.SpecFilterDefinition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NeuMouseWheelTriggerDefinition extends DefaultTriggerDefinition {

    private TriggerEventIdentity eventIdentity;  /* 触发后产生的事件对象 */
    private SpecFilterDefinition<NeuMouseWheelSpec, NeuModifierConstraint> filterDefinition;

}
