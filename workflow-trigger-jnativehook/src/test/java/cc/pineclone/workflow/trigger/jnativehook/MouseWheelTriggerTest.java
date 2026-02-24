package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.NeuMouseWheelSpec;
import cc.pineclone.workflow.impl.trigger.DefaultTriggerEventBuffer;
import cc.pineclone.workflow.api.trigger.event.TriggerEvent;
import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.trigger.jnativehook.filter.NeuMouseWheelSpecFilter;
import cc.pineclone.workflow.trigger.jnativehook.source.NativeMouseWheelEventSource;
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

    private final BlockingQueue<TriggerEvent> queue = new LinkedBlockingQueue<>();
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
                new TriggerIdentity("test_domain", "jnativehook_mouse_wheel_trigger"),
                s -> new TriggerEventIdentity("test_domain", "jnativehook_mouse_wheel_trigger", s),
                new NeuMouseWheelSpecFilter(
                        new NeuMouseWheelSpec(NeuMouseWheelSpec.ScrollDirection.UP),
                        new NeuModifierConstraint(
                                Set.of(NeuModifierConstraint.NeuModifierKey.BUTTON_1),
                                Set.of(NeuModifierConstraint.NeuModifierKey.CONTROL_R))),
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
