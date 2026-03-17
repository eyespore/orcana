package cc.pineclone.eventflow.plugin.trigger.jnativehook;

import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.NeuMouseWheelSpec;
import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.runtime.impl.trigger.factory.DefaultTriggerDefinition;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.SpecFilterDefinition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NeuMouseWheelTriggerDefinition extends DefaultTriggerDefinition {

    private EventSelector eventIdentity;  /* 触发后产生的事件对象 */
    private SpecFilterDefinition<NeuMouseWheelSpec, NeuModifierConstraint> filterDefinition;

}
