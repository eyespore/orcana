package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuKeySpec;
import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.NeuMouseSpec;
import cc.pineclone.workflow.DefaultWorkflowCore;
import cc.pineclone.workflow.api.trigger.*;
import cc.pineclone.workflow.trigger.DefaultTriggerEventDispatcher;
import cc.pineclone.workflow.trigger.UnionTriggerDefinition;
import cc.pineclone.workflow.trigger.jnativehook.api.SpecFilterDefinition;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JNativeHookPluginTest {

    @Rule
    public JNativeHookRule rule = new JNativeHookRule();

    private final BlockingQueue<TriggerEvent> eventQueue = new LinkedBlockingQueue<>();
    private final TriggerEventDispatcher dispatcher = new DefaultTriggerEventDispatcher(eventQueue);

    @Test
    public void test() throws InterruptedException {
        DefaultWorkflowCore core = new DefaultWorkflowCore();
        core.registerPlugin(new JNativeHookTriggerPlugin());
        core.init();

        NeuKeyGestureTriggerDefinition definition1 = new NeuKeyGestureTriggerDefinition();
        definition1.setIdentity(new TriggerIdentity("test_domain", "my_neu_key_trigger"));
        definition1.setEventIdentity(new TriggerEventIdentity("pineclone", "test", "gesture:{{GESTURE}}"));
        definition1.setFilterDefinition(new SpecFilterDefinition<>(
                new NeuKeySpec(NeuKeySpec.NeuKey.N, NeuKeySpec.NeuKeyLocation.STANDARD),
                new NeuModifierConstraint(Set.of(), Set.of())
        ));

        UnionTriggerDefinition definition2 = new UnionTriggerDefinition();
        definition2.setIdentity(new TriggerIdentity("test_domain", "my_union_trigger"));

        NeuMouseGestureTriggerDefinition definition2_1 = new NeuMouseGestureTriggerDefinition();
        definition2_1.setFilterDefinition(new SpecFilterDefinition<>(
                new NeuMouseSpec(NeuMouseSpec.NeuMouseButton.BUTTON_1),
                new NeuModifierConstraint(Set.of(), Set.of())
        ));

        definition2.setChildDefinitions(List.of(
                definition2_1
        ));

        TriggerIdentity identity1 = core.registerTrigger(definition1);
        TriggerIdentity identity2 = core.registerTrigger(definition2);

//        core.retainTrigger(identity1);
//        core.retainTrigger(identity2);

        core.getRootTriggers().forEach(t -> {
            t.attach(dispatcher);
            if (t instanceof TriggerLifecycleAware aware) {
                aware.init();
            }
        });

        while (true) {
            TriggerEvent event = dispatcher.take();
            System.out.println(event);
        }


    }
}
