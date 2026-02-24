package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuKeySpec;
import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.api.ClickEdge;
import cc.pineclone.workflow.impl.trigger.AnyOrderTrigger;
import cc.pineclone.workflow.impl.trigger.DefaultTriggerEventBuffer;
import cc.pineclone.workflow.api.trigger.event.TriggerEvent;
import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.trigger.jnativehook.filter.NeuKeySpecFilter;
import cc.pineclone.workflow.trigger.jnativehook.gesture.ClickGesture;
import cc.pineclone.workflow.trigger.jnativehook.source.NativeKeyEventSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class AnyOrderTriggerTest {

    @Rule
    public JNativeHookRule rule = new JNativeHookRule();

    private final BlockingQueue<TriggerEvent> queue = new LinkedBlockingQueue<>();
    DefaultTriggerEventBuffer dispatcher = new DefaultTriggerEventBuffer(queue);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final NativeKeyEventSource source = new NativeKeyEventSource();

    private final Logger log = LoggerFactory.getLogger(getClass());

    private AnyOrderTrigger anyOrderTrigger;

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
        if (anyOrderTrigger != null) anyOrderTrigger.close();
        if (keyTrigger1 != null) keyTrigger1.close();
        if (keyTrigger2 != null) keyTrigger2.close();
        if (keyTrigger3 != null) keyTrigger3.close();
        source.uninstall();
        scheduler.shutdown();
    }

    @Test
    public void testSequenceCommand() throws InterruptedException {
        anyOrderTrigger = new AnyOrderTrigger(
                new TriggerIdentity("test_domain", "jnativehook_sequence_command_trigger"),
                new TriggerEventIdentity("test_domain", "sequence_command", "combo_active"),
                Map.of(
                        identity1, 2,
                        identity2, 1,
                        identity3, 1
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

        anyOrderTrigger.addChildren(keyTrigger1);
        anyOrderTrigger.addChildren(keyTrigger2);
        anyOrderTrigger.addChildren(keyTrigger3);

        anyOrderTrigger.init();
        anyOrderTrigger.attach(dispatcher);

        while (true) {
            TriggerEvent event = dispatcher.take();
            log.debug("{}", event);
        }
    }

}
