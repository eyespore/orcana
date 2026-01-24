package cc.pineclone.automation.trigger.source;

/* 信号源 */

import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.MacroLifecycleAware;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;
import lombok.Setter;

/**
 * @see KeyboardSource
 */
public abstract class InputSource implements MacroLifecycleAware {

    private volatile boolean installed = false;
    @Setter protected InputSourceListener listener;

    /* 宏安装 */
    @Override
    public void onMacroLaunch(MacroEvent event) {
        if (installed) return;
        if (this instanceof NativeKeyListener) {
            GlobalScreen.addNativeKeyListener((NativeKeyListener) this);
        } else if (this instanceof NativeMouseListener) {
            GlobalScreen.addNativeMouseListener((NativeMouseListener) this);
        } else if (this instanceof NativeMouseWheelListener) {
            GlobalScreen.addNativeMouseWheelListener((NativeMouseWheelListener) this);
        } else return;
        installed = true;
    }

    /* 宏卸载 */
    @Override
    public void onMacroTerminate(MacroEvent event) {
        if (!installed) return;
        if (this instanceof NativeKeyListener) {
            GlobalScreen.removeNativeKeyListener((NativeKeyListener) this);
        } else if (this instanceof NativeMouseListener) {
            GlobalScreen.removeNativeMouseListener((NativeMouseListener) this);
        } else if (this instanceof NativeMouseWheelListener) {
            GlobalScreen.removeNativeMouseWheelListener((NativeMouseWheelListener) this);
        } else return;
        installed = false;
    }
}
