package cc.pineclone.eventflow.trigger.jnativehook;

import cc.pineclone.eventflow.runtime.api.selector.EventSelector;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.GestureTrigger;
import cc.pineclone.eventflow.interaction.NeuKeySpec;
import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.api.ClickEdge;
import cc.pineclone.eventflow.runtime.impl.trigger.DefaultTriggerEventBuffer;
import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.filter.NeuKeySpecFilter;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.gesture.ClickGesture;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.gesture.HoldGesture;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.source.NativeKeyEventSource;
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

    private final BlockingQueue<Event> queue = new LinkedBlockingQueue<>();
    DefaultTriggerEventBuffer dispatcher = new DefaultTriggerEventBuffer(queue);

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
        if (trigger != null) trigger.close();
        source.uninstall();
        scheduler.shutdown();
    }

    @Test
    public void testKeyClickGesture() throws InterruptedException {
        trigger = new GestureTrigger.NeuKeyTrigger(
                new ComponentId("test_domain", "jnativehook_key_trigger"),
                gesture -> new EventSelector("test_domain", "jnativehook_key_trigger", gesture.name()),
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
        trigger.bind(dispatcher);
        while (true) {
            Event event = dispatcher.take();
            log.debug("{}", event);
        }
    }

    @Test
    public void testKeyHoldGesture() throws InterruptedException {
        trigger = new GestureTrigger.NeuKeyTrigger(
                new ComponentId("test_domain", "jnativehook_key_trigger"),
                gesture -> new EventSelector("test_domain", "jnativehook_key_trigger", gesture.name()),
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
        trigger.bind(dispatcher);
        while (true) {
            Event event = dispatcher.take();
            log.debug("{}", event);
        }
    }

}
