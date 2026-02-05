package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuKeySpec;
import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.api.ClickEdge;
import cc.pineclone.workflow.trigger.DefaultTriggerEventDispatcher;
import cc.pineclone.workflow.api.trigger.TriggerEvent;
import cc.pineclone.workflow.api.trigger.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.trigger.jnativehook.filter.NeuKeySpecFilter;
import cc.pineclone.workflow.trigger.jnativehook.gesture.ClickGesture;
import cc.pineclone.workflow.trigger.jnativehook.gesture.HoldGesture;
import cc.pineclone.workflow.trigger.jnativehook.source.NativeKeyEventSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class KeyTriggerTest {

    @Rule
    public JNativeHookRule rule = new JNativeHookRule();

    private final BlockingQueue<TriggerEvent> queue = new LinkedBlockingQueue<>();
    DefaultTriggerEventDispatcher dispatcher = new DefaultTriggerEventDispatcher(queue);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final NativeKeyEventSource source = new NativeKeyEventSource();

    private final Logger log = LoggerFactory.getLogger(getClass());

    private GestureTrigger.NeuKeyTrigger trigger;

    @Before
    public void before() {
        source.install();
    }

    @After
    public void after() {
        if (trigger != null) trigger.destroy();
        source.uninstall();
        scheduler.shutdown();
    }

    @Test
    public void testKeyClickGesture() throws InterruptedException {
        trigger = new GestureTrigger.NeuKeyTrigger(
                new TriggerIdentity("test_domain", "jnativehook_key_trigger"),
                gesture -> new TriggerEventIdentity("test_domain", "jnativehook_key_trigger", gesture.name()),
                new NeuKeySpecFilter(
                        new NeuKeySpec(
                                NeuKeySpec.NeuKey.N,
                                NeuKeySpec.NeuKeyLocation.STANDARD),
                        new NeuModifierConstraint(
                                Set.of(
                                        NeuModifierConstraint.NeuModifierKey.BUTTON_1),
                                Set.of(
                                        NeuModifierConstraint.NeuModifierKey.CONTROL_R
                                )
                        )),
                new ClickGesture.KeyClickGesture(ClickEdge.FALLING, 0, 3, 50, scheduler),
                source
        );
        trigger.init();
        trigger.attach(dispatcher);
        while (true) {
            TriggerEvent event = dispatcher.take();
            log.debug("{}", event);
        }
    }

    @Test
    public void testKeyHoldGesture() throws InterruptedException {
        trigger = new GestureTrigger.NeuKeyTrigger(
                new TriggerIdentity("test_domain", "jnativehook_key_trigger"),
                gesture -> new TriggerEventIdentity("test_domain", "jnativehook_key_trigger", gesture.name()),
                new NeuKeySpecFilter(
                        new NeuKeySpec(
                                NeuKeySpec.NeuKey.N,
                                NeuKeySpec.NeuKeyLocation.STANDARD),
                        new NeuModifierConstraint(
                                Set.of(
                                        NeuModifierConstraint.NeuModifierKey.SHIFT_L,
                                        NeuModifierConstraint.NeuModifierKey.ALT_L,
                                        NeuModifierConstraint.NeuModifierKey.CONTROL_L),
                                Set.of()
                        )),
                new HoldGesture.KeyHoldGesture(2000, scheduler),
                source
        );
        trigger.init();
        trigger.attach(dispatcher);
        while (true) {
            TriggerEvent event = dispatcher.take();
            log.debug("{}", event);
        }
    }

}
