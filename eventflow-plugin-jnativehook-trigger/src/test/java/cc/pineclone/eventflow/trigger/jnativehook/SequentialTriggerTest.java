package cc.pineclone.eventflow.trigger.jnativehook;

import cc.pineclone.eventflow.runtime.api.selector.EventSelector;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.GestureTrigger;
import cc.pineclone.eventflow.interaction.NeuKeySpec;
import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.api.ClickEdge;
import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.runtime.impl.trigger.DefaultTriggerEventBuffer;
import cc.pineclone.eventflow.runtime.impl.trigger.SequentialTrigger;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.filter.NeuKeySpecFilter;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.gesture.ClickGesture;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.source.NativeKeyEventSource;
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

    private final BlockingQueue<Event> queue = new LinkedBlockingQueue<>();
    DefaultTriggerEventBuffer dispatcher = new DefaultTriggerEventBuffer(queue);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final NativeKeyEventSource source = new NativeKeyEventSource();

    private final Logger log = LoggerFactory.getLogger(getClass());

    private SequentialTrigger sequentialTrigger;

    private GestureTrigger.NeuKeyTrigger keyTrigger1;
    private GestureTrigger.NeuKeyTrigger keyTrigger2;
    private GestureTrigger.NeuKeyTrigger keyTrigger3;

    private final EventSelector identity1 = new EventSelector("test_domain", "jnativehook_key_trigger", "key1");
    private final EventSelector identity2 = new EventSelector("test_domain", "jnativehook_key_trigger", "key2");
    private final EventSelector identity3 = new EventSelector("test_domain", "jnativehook_key_trigger", "key3");

    @Before
    public void before() {
        source.install();
    }

    @After
    public void after() {
        if (sequentialTrigger != null) sequentialTrigger.close();
        if (keyTrigger1 != null) keyTrigger1.close();
        if (keyTrigger2 != null) keyTrigger2.close();
        if (keyTrigger3 != null) keyTrigger3.close();
        source.uninstall();
        scheduler.shutdown();
    }

    @Test
    public void testSequenceCommand() throws InterruptedException {
        sequentialTrigger = new SequentialTrigger(
                new ComponentId("test_domain", "jnativehook_sequence_command_trigger"),
                new EventSelector("test_domain", "sequence_command", "combo_active"),
                List.of(
                        identity1,
                        identity2,
                        identity3
                ),
                Set.of(),
                2000
        );

        keyTrigger1 = new GestureTrigger.NeuKeyTrigger(
                new ComponentId("test_domain", "jnativehook_key_trigger1"),
                gesture -> identity1,
                new NeuKeySpecFilter(
                        new NeuKeySpec(NeuKeySpec.NeuKey.N, NeuKeySpec.NeuKeyLocation.STANDARD),
                        new NeuModifierConstraint(Set.of(), Set.of())),
                new ClickGesture.KeyClickGesture(ClickEdge.FALLING, 0, 3, 50, scheduler),
                source
        );
        keyTrigger1.init();

        keyTrigger2 = new GestureTrigger.NeuKeyTrigger(
                new ComponentId("test_domain", "jnativehook_key_trigger2"),
                gesture -> identity2,
                new NeuKeySpecFilter(
                        new NeuKeySpec(NeuKeySpec.NeuKey.M, NeuKeySpec.NeuKeyLocation.STANDARD),
                        new NeuModifierConstraint(Set.of(), Set.of())),
                new ClickGesture.KeyClickGesture(ClickEdge.FALLING, 0, 3, 50, scheduler),
                source
        );
        keyTrigger2.init();

        keyTrigger3 = new GestureTrigger.NeuKeyTrigger(
                new ComponentId("test_domain", "jnativehook_key_trigger3"),
                gesture -> identity3,
                new NeuKeySpecFilter(
                        new NeuKeySpec(NeuKeySpec.NeuKey.B, NeuKeySpec.NeuKeyLocation.STANDARD),
                        new NeuModifierConstraint(Set.of(), Set.of())),
                new ClickGesture.KeyClickGesture(ClickEdge.FALLING, 0, 3, 50, scheduler),
                source
        );
        keyTrigger3.init();

        sequentialTrigger.addChild(keyTrigger1);
        sequentialTrigger.addChild(keyTrigger2);
        sequentialTrigger.addChild(keyTrigger3);

        sequentialTrigger.init();
        sequentialTrigger.bind(dispatcher);
        while (true) {
            Event event = dispatcher.take();
            log.debug("{}", event);
        }
    }

}
