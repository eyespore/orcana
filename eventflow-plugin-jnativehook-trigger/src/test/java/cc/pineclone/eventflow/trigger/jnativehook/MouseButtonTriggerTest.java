package cc.pineclone.eventflow.trigger.jnativehook;

import cc.pineclone.eventflow.runtime.api.selector.EventSelector;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.GestureTrigger;
import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.NeuMouseSpec;
import cc.pineclone.eventflow.interaction.api.ClickEdge;
import cc.pineclone.eventflow.runtime.impl.trigger.DefaultTriggerEventBuffer;
import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.NativeInputEventSource;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.filter.NeuMouseSpecFilter;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.gesture.ClickGesture;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.gesture.HoldGesture;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.source.NativeMouseEventSource;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
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

public class MouseButtonTriggerTest {

    @Rule
    public JNativeHookRule rule = new JNativeHookRule();

    private final BlockingQueue<Event> queue = new LinkedBlockingQueue<>();
    DefaultTriggerEventBuffer buffer = new DefaultTriggerEventBuffer(queue);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final NativeInputEventSource<NativeMouseEvent> source = new NativeMouseEventSource();

    private final Logger log = LoggerFactory.getLogger(getClass());

    private GestureTrigger.NeuMouseTrigger trigger;

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
    public void testMouseButtonClickGesture() throws InterruptedException {
        trigger = new GestureTrigger.NeuMouseTrigger(
                new ComponentId("test_domain", "jnativehook_mouse_button_trigger"),
                gesture -> new EventSelector("test_domain", "jnativehook_mouse_button_trigger", gesture.name()),
                new NeuMouseSpecFilter(
                        new NeuMouseSpec(NeuMouseSpec.NeuMouseButton.BUTTON_1),
                        new NeuModifierConstraint(
                                Set.of(),
                                Set.of()
                        )),
                new ClickGesture.MouseButtonClickGesture(ClickEdge.FALLING, 0, 1, 2000, scheduler),
                source
        );
        trigger.init();
        trigger.bind(buffer);
        while (true) {
            Event event = buffer.take();
            log.debug("{}", event);
        }
    }

    @Test
    public void testKeyHoldGesture() throws InterruptedException {
        trigger = new GestureTrigger.NeuMouseTrigger(
                new ComponentId("test_domain", "jnativehook_mouse_button_trigger"),
                gesture -> new EventSelector("test_domain", "jnativehook_mouse_button_trigger", gesture.name()),
                new NeuMouseSpecFilter(
                        new NeuMouseSpec(NeuMouseSpec.NeuMouseButton.BUTTON_1),
                        new NeuModifierConstraint(
                                Set.of(
                                        NeuModifierConstraint.NeuModifierKey.SHIFT_L,
                                        NeuModifierConstraint.NeuModifierKey.ALT_L,
                                        NeuModifierConstraint.NeuModifierKey.CONTROL_L),
                                Set.of()
                        )),
                new HoldGesture.MouseButtonHoldGesture(0, scheduler),
                source
        );
        trigger.init();
        trigger.bind(buffer);
        while (true) {
            Event event = buffer.take();
            log.debug("{}", event);
        }
    }

}
