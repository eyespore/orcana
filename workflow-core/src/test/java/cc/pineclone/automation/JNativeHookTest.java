package cc.pineclone.automation;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JNativeHookTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void testListener() throws NativeHookException, InterruptedException {
        GlobalScreen.registerNativeHook();

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                log.info("Pressed: {}", NativeKeyEvent.getKeyText(e.getKeyCode()));
            }
        });

        Thread.sleep(5000);
    }

}
