package cc.pineclone.eventflow.trigger.jnativehook.source;

import cc.pineclone.eventflow.plugin.trigger.jnativehook.source.NativeMouseMotionEventSource;
import cc.pineclone.eventflow.trigger.jnativehook.JNativeHookRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeMouseMotionEventSourceTest {

    @Rule
    public JNativeHookRule rule = new JNativeHookRule();
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void testMouseMotionRawEventSource() throws InterruptedException {
        NativeMouseMotionEventSource source = new NativeMouseMotionEventSource();
        source.install();

        source.registerListener(event -> log.debug(event.paramString()));

        while (true) {
            Thread.sleep(1000);
        }
    }


}
