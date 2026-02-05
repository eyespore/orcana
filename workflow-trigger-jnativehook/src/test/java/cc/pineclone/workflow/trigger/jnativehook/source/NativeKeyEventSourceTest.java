package cc.pineclone.workflow.trigger.jnativehook.source;

import cc.pineclone.workflow.trigger.jnativehook.JNativeHookRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeKeyEventSourceTest {

    @Rule
    public JNativeHookRule rule = new JNativeHookRule();
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void testKeyRawEventSource() throws InterruptedException {
        NativeKeyEventSource source = new NativeKeyEventSource();
        source.install();

        source.registerListener(event -> log.debug(event.paramString()));

        while (true) {
            Thread.sleep(1000);
        }
    }


}
