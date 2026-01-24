package cc.pineclone.automation.trigger.source;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

/**
 * 基于 JNativeHook 实现的信号源
 */
public abstract class JNativeHookInputSource extends InputSource {

    @Override
    protected void init() {
        if (this instanceof NativeKeyListener) {
            GlobalScreen.addNativeKeyListener((NativeKeyListener) this);
        } else if (this instanceof NativeMouseListener) {
            GlobalScreen.addNativeMouseListener((NativeMouseListener) this);
        } else if (this instanceof NativeMouseWheelListener) {
            GlobalScreen.addNativeMouseWheelListener((NativeMouseWheelListener) this);
        }
    }

    @Override
    protected void stop() {
        if (this instanceof NativeKeyListener) {
            GlobalScreen.removeNativeKeyListener((NativeKeyListener) this);
        } else if (this instanceof NativeMouseListener) {
            GlobalScreen.removeNativeMouseListener((NativeMouseListener) this);
        } else if (this instanceof NativeMouseWheelListener) {
            GlobalScreen.removeNativeMouseWheelListener((NativeMouseWheelListener) this);
        }
    }
}
