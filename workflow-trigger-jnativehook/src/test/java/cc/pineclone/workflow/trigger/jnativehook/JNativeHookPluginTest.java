package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuKeySpec;
import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.NeuMouseSpec;
import cc.pineclone.workflow.impl.DefaultRuntime;
import cc.pineclone.workflow.api.Runtime;
import cc.pineclone.workflow.api.trigger.*;
import cc.pineclone.workflow.api.trigger.event.TriggerEvent;
import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.impl.trigger.factory.UnionTriggerDefinition;
import cc.pineclone.workflow.trigger.jnativehook.api.SpecFilterDefinition;
import cc.pineclone.workflow.trigger.jnativehook.gesture.GestureDefinition;
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
        definition1.setIdentity(new TriggerIdentity("test_domain", "my_neu_key_trigger"));
        definition1.setEventIdentity(new TriggerEventIdentity("pineclone", "test", "gesture:{{GESTURE}}"));
        definition1.setFilterDefinition(new SpecFilterDefinition<>(
                new NeuKeySpec(NeuKeySpec.NeuKey.N, NeuKeySpec.NeuKeyLocation.STANDARD),
                new NeuModifierConstraint(Set.of(), Set.of())
        ));

        UnionTriggerDefinition definition2 = new UnionTriggerDefinition();
        definition2.setIdentity(new TriggerIdentity("test_domain", "my_union_trigger"));
        definition2.setEventMapping(Map.of(
                new TriggerEventIdentity("test_domain", "my_union_trigger_0", "SINGLE_CLICK"),
                new TriggerEventIdentity("test_domain", "my_union_trigger", "MAPPED_CLICK")
        ));

        NeuMouseGestureTriggerDefinition definition2_1 = new NeuMouseGestureTriggerDefinition();
        definition2_1.setFilterDefinition(new SpecFilterDefinition<>(
                new NeuMouseSpec(NeuMouseSpec.NeuMouseButton.BUTTON_1),
                new NeuModifierConstraint(Set.of(), Set.of())
        ));

        definition2.setChildDefinitions(List.of(
                definition2_1
        ));

        TriggerIdentity identity1 = runtime.triggerService().admin().deploy(definition1);
        TriggerIdentity identity2 = runtime.triggerService().admin().deploy(definition2);

        runtime.triggerService().admin().activate(identity1);
        runtime.triggerService().admin().activate(identity2);

        while (true) {
            TriggerEvent event = runtime.triggerService().events().take();
            log.debug("{}", event);
        }
    }
}
