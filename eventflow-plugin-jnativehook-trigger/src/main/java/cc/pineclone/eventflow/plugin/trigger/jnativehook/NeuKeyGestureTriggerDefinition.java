package cc.pineclone.eventflow.plugin.trigger.jnativehook;

import cc.pineclone.eventflow.runtime.api.selector.EventSelector;
import cc.pineclone.eventflow.interaction.NeuKeySpec;
import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.SpecFilterDefinition;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.gesture.GestureDefinition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NeuKeyGestureTriggerDefinition extends DefaultTriggerDefinition {

    private EventSelector eventIdentity;  /* 触发后产生的事件对象 */
    private SpecFilterDefinition<NeuKeySpec, NeuModifierConstraint> filterDefinition;
    private GestureDefinition gestureDefinition;

}
