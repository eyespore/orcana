package cc.pineclone.eventflow.trigger.jnativehook;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.JNativeHookTriggerPlugin;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.NeuKeyGestureTriggerDefinition;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.NeuMouseGestureTriggerDefinition;
import cc.pineclone.eventflow.interaction.NeuKeySpec;
import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.NeuMouseSpec;
import cc.pineclone.eventflow.runtime.impl.DefaultRuntime;
import cc.pineclone.eventflow.runtime.api.Runtime;
import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.runtime.impl.trigger.factory.UnionTriggerDefinition;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.SpecFilterDefinition;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class JNativeHookPluginTest {

    @Rule
    public JNativeHookRule rule = new JNativeHookRule();

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void test() throws InterruptedException {
        Runtime runtime = new DefaultRuntime();
        runtime.registerPlugin(new JNativeHookTriggerPlugin());
        runtime.init();

        NeuKeyGestureTriggerDefinition definition1 = new NeuKeyGestureTriggerDefinition();
        definition1.setIdentity(new ComponentId("test_domain", "my_neu_key_trigger"));
        definition1.setEventIdentity(new EventSelector("pineclone", "test", "gesture:{{GESTURE}}"));
        definition1.setFilterDefinition(new SpecFilterDefinition<>(
                new NeuKeySpec(NeuKeySpec.NeuKey.N, NeuKeySpec.NeuKeyLocation.STANDARD),
                new NeuModifierConstraint(Set.of(), Set.of())
        ));

        UnionTriggerDefinition definition2 = new UnionTriggerDefinition();
        definition2.setIdentity(new ComponentId("test_domain", "my_union_trigger"));
        definition2.setEventMapping(Map.of(
                new EventSelector("test_domain", "my_union_trigger_0", "SINGLE_CLICK"),
                new EventSelector("test_domain", "my_union_trigger", "MAPPED_CLICK")
        ));

        NeuMouseGestureTriggerDefinition definition2_1 = new NeuMouseGestureTriggerDefinition();
        definition2_1.setFilterDefinition(new SpecFilterDefinition<>(
                new NeuMouseSpec(NeuMouseSpec.NeuMouseButton.BUTTON_1),
                new NeuModifierConstraint(Set.of(), Set.of())
        ));

        definition2.setChildren(List.of(
                definition2_1
        ));

        ComponentId identity1 = runtime.triggerService().admin().deploy(definition1);
        ComponentId identity2 = runtime.triggerService().admin().deploy(definition2);

        runtime.triggerService().admin().activate(identity1);
        runtime.triggerService().admin().activate(identity2);

        while (true) {
            Event event = runtime.triggerService().events().take();
            log.debug("{}", event);
        }
    }
}
