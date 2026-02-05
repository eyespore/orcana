package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuKeySpec;
import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.workflow.api.trigger.TriggerEventIdentity;
import cc.pineclone.workflow.trigger.DefaultTriggerDefinition;
import cc.pineclone.workflow.trigger.jnativehook.api.SpecFilterDefinition;
import cc.pineclone.workflow.trigger.jnativehook.gesture.GestureDefinition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NeuKeyGestureTriggerDefinition extends DefaultTriggerDefinition {

    private TriggerEventIdentity eventIdentity;  /* 触发后产生的事件对象 */
    private SpecFilterDefinition<NeuKeySpec, NeuModifierConstraint> filterDefinition;
    private GestureDefinition gestureDefinition;

}
