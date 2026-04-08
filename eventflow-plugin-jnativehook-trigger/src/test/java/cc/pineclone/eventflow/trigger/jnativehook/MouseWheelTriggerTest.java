package cc.pineclone.eventflow.trigger.jnativehook;

import cc.pineclone.eventflow.runtime.api.selector.EventSelector;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.SignalTrigger;
import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.NeuMouseWheelSpec;
import cc.pineclone.eventflow.runtime.impl.trigger.DefaultTriggerEventBuffer;
import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.filter.NeuMouseWheelSpecFilter;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.source.NativeMouseWheelEventSource;
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

public class MouseWheelTriggerTest {

    @Rule
    public JNativeHookRule rule = new JNativeHookRule();

    private final BlockingQueue<Event> queue = new LinkedBlockingQueue<>();
    DefaultTriggerEventBuffer dispatcher = new DefaultTriggerEventBuffer(queue);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final NativeMouseWheelEventSource source = new NativeMouseWheelEventSource();

    private final Logger log = LoggerFactory.getLogger(getClass());

    private SignalTrigger.NeuMouseWheelTrigger trigger;

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
    public void testMouseWheelTrigger() throws InterruptedException {
        trigger = new SignalTrigger.NeuMouseWheelTrigger(
                new ComponentId("test_domain", "jnativehook_mouse_wheel_trigger"),
                s -> new EventSelector("test_domain", "jnativehook_mouse_wheel_trigger", s),
                new NeuMouseWheelSpecFilter(
                        new NeuMouseWheelSpec(NeuMouseWheelSpec.ScrollDirection.UP),
                        new NeuModifierConstraint(
                                Set.of(NeuModifierConstraint.NeuModifierKey.BUTTON_1),
                                Set.of(NeuModifierConstraint.NeuModifierKey.CONTROL_R))),
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
