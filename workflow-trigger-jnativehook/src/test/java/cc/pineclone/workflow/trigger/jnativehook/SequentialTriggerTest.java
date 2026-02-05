package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuKeySpec;
import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.api.ClickEdge;
import cc.pineclone.workflow.trigger.DefaultTriggerEventDispatcher;
import cc.pineclone.workflow.trigger.SequentialTrigger;
import cc.pineclone.workflow.api.trigger.*;
import cc.pineclone.workflow.trigger.jnativehook.filter.NeuKeySpecFilter;
import cc.pineclone.workflow.trigger.jnativehook.gesture.ClickGesture;
import cc.pineclone.workflow.trigger.jnativehook.source.NativeKeyEventSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class SequentialTriggerTest {

    @Rule
    public JNativeHookRule rule = new JNativeHookRule();

    private final BlockingQueue<TriggerEvent> queue = new LinkedBlockingQueue<>();
    DefaultTriggerEventDispatcher dispatcher = new DefaultTriggerEventDispatcher(queue);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final NativeKeyEventSource source = new NativeKeyEventSource();

    private final Logger log = LoggerFactory.getLogger(getClass());

    private SequentialTrigger sequentialTrigger;

    private GestureTrigger.NeuKeyTrigger keyTrigger1;
    private GestureTrigger.NeuKeyTrigger keyTrigger2;
    private GestureTrigger.NeuKeyTrigger keyTrigger3;

    private final TriggerEventIdentity identity1 = new TriggerEventIdentity("test_domain", "jnativehook_key_trigger", "key1");
    private final TriggerEventIdentity identity2 = new TriggerEventIdentity("test_domain", "jnativehook_key_trigger", "key2");
    private final TriggerEventIdentity identity3 = new TriggerEventIdentity("test_domain", "jnativehook_key_trigger", "key3");

    @Before
    public void before() {
        source.install();
    }

    @After
    public void after() {
        if (sequentialTrigger != null) sequentialTrigger.destroy();
        if (keyTrigger1 != null) keyTrigger1.destroy();
        if (keyTrigger2 != null) keyTrigger2.destroy();
        if (keyTrigger3 != null) keyTrigger3.destroy();
        source.uninstall();
        scheduler.shutdown();
    }

    @Test
    public void testSequenceCommand() throws InterruptedException {
        sequentialTrigger = new SequentialTrigger(
                new TriggerIdentity("test_domain", "jnativehook_sequence_command_trigger"),
                new TriggerEventIdentity("test_domain", "sequence_command", "combo_active"),
                List.of(
                        identity1,
                        identity2,
                        identity3
                ),
                Set.of(),
                2000
        );

        keyTrigger1 = new GestureTrigger.NeuKeyTrigger(
                new TriggerIdentity("test_domain", "jnativehook_key_trigger1"),
                gesture -> identity1,
                new NeuKeySpecFilter(
                        new NeuKeySpec(NeuKeySpec.NeuKey.N, NeuKeySpec.NeuKeyLocation.STANDARD),
                        new NeuModifierConstraint(Set.of(), Set.of())),
                new ClickGesture.KeyClickGesture(ClickEdge.FALLING, 0, 3, 50, scheduler),
                source
        );
        keyTrigger1.init();

        keyTrigger2 = new GestureTrigger.NeuKeyTrigger(
                new TriggerIdentity("test_domain", "jnativehook_key_trigger2"),
                gesture -> identity2,
                new NeuKeySpecFilter(
                        new NeuKeySpec(NeuKeySpec.NeuKey.M, NeuKeySpec.NeuKeyLocation.STANDARD),
                        new NeuModifierConstraint(Set.of(), Set.of())),
                new ClickGesture.KeyClickGesture(ClickEdge.FALLING, 0, 3, 50, scheduler),
                source
        );
        keyTrigger2.init();

        keyTrigger3 = new GestureTrigger.NeuKeyTrigger(
                new TriggerIdentity("test_domain", "jnativehook_key_trigger3"),
                gesture -> identity3,
                new NeuKeySpecFilter(
                        new NeuKeySpec(NeuKeySpec.NeuKey.B, NeuKeySpec.NeuKeyLocation.STANDARD),
                        new NeuModifierConstraint(Set.of(), Set.of())),
                new ClickGesture.KeyClickGesture(ClickEdge.FALLING, 0, 3, 50, scheduler),
                source
        );
        keyTrigger3.init();

        sequentialTrigger.addChildren(keyTrigger1);
        sequentialTrigger.addChildren(keyTrigger2);
        sequentialTrigger.addChildren(keyTrigger3);

        sequentialTrigger.init();
        sequentialTrigger.attach(dispatcher);
        while (true) {
            TriggerEvent event = dispatcher.take();
            log.debug("{}", event);
        }
    }

}
