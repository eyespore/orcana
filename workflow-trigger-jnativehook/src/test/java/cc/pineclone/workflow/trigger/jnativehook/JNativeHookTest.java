package cc.pineclone.workflow.trigger.jnativehook;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JNativeHookTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void beforeTest() throws NativeHookException {
        GlobalScreen.registerNativeHook();
    }

    @After
    public void afterTest() throws NativeHookException {
        GlobalScreen.unregisterNativeHook();
        GlobalScreen.setEventDispatcher(null);
    }

    @Test
    public void testListener() throws NativeHookException, InterruptedException {
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
//                log.info("Key pressed: {}", e.getModifiers());
            }
        });

        GlobalScreen.addNativeMouseMotionListener(new NativeMouseMotionListener() {
            @Override
            public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
//                log.info("Mouse dragged: {}", nativeEvent);
            }

            @Override
            public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
//                log.info("Mouse moved: {}", nativeEvent.getPoint());
            }
        });

        GlobalScreen.addNativeMouseWheelListener(new NativeMouseWheelListener() {
            @Override
            public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
                log.info("{}", nativeEvent.paramString());
            }
        });

        GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
            @Override
            public void nativeMouseClicked(NativeMouseEvent nativeEvent) {
//                log.debug("{}", nativeEvent.paramString());
            }

            @Override
            public void nativeMousePressed(NativeMouseEvent nativeEvent) {
//                log.debug("{}", nativeEvent.paramString());
            }

            @Override
            public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
//                log.debug("{}", nativeEvent.paramString());
            }
        });

        while (true) {
            Thread.sleep(1000);
        }
    }

}
