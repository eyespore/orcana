package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.NeuMouseSpec;
import cc.pineclone.interaction.api.ClickEdge;
import cc.pineclone.workflow.trigger.DefaultTriggerEventDispatcher;
import cc.pineclone.workflow.api.trigger.TriggerEvent;
import cc.pineclone.workflow.api.trigger.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.trigger.jnativehook.api.NativeInputEventSource;
import cc.pineclone.workflow.trigger.jnativehook.filter.NeuMouseSpecFilter;
import cc.pineclone.workflow.trigger.jnativehook.gesture.ClickGesture;
import cc.pineclone.workflow.trigger.jnativehook.gesture.HoldGesture;
import cc.pineclone.workflow.trigger.jnativehook.source.NativeMouseEventSource;
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

    private final BlockingQueue<TriggerEvent> queue = new LinkedBlockingQueue<>();
    DefaultTriggerEventDispatcher dispatcher = new DefaultTriggerEventDispatcher(queue);

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
        if (trigger != null) trigger.destroy();
        source.uninstall();
        scheduler.shutdown();
    }

    @Test
    public void testMouseButtonClickGesture() throws InterruptedException {
        trigger = new GestureTrigger.NeuMouseTrigger(
                new TriggerIdentity("test_domain", "jnativehook_mouse_button_trigger"),
                gesture -> new TriggerEventIdentity("test_domain", "jnativehook_mouse_button_trigger", gesture.name()),
                new NeuMouseSpecFilter(
                        new NeuMouseSpec(NeuMouseSpec.NeuMouseButton.BUTTON_1),
                        new NeuModifierConstraint(
                                Set.of(),
                                Set.of(NeuModifierConstraint.NeuModifierKey.BUTTON_1)
                        )),
                new ClickGesture.MouseButtonClickGesture(ClickEdge.FALLING, 2000, 3, 50, scheduler),
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
        trigger = new GestureTrigger.NeuMouseTrigger(
                new TriggerIdentity("test_domain", "jnativehook_mouse_button_trigger"),
                gesture -> new TriggerEventIdentity("test_domain", "jnativehook_mouse_button_trigger", gesture.name()),
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
        trigger.attach(dispatcher);
        while (true) {
            TriggerEvent event = dispatcher.take();
            log.debug("{}", event);
        }
    }

}
