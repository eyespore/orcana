package cc.pineclone.workflow.trigger.jnativehook.source;

import cc.pineclone.workflow.trigger.jnativehook.api.NativeInputEventSource;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class NativeKeyEventSource
        extends NativeInputEventSource<NativeKeyEvent>
        implements NativeKeyListener {

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
        fire(nativeEvent);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        fire(nativeEvent);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        fire(nativeEvent);
    }

    @Override
    public void install() {
        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void uninstall() {
        GlobalScreen.removeNativeKeyListener(this);
    }
}
