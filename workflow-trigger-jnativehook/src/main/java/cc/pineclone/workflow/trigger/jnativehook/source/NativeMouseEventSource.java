package cc.pineclone.workflow.trigger.jnativehook.source;

import cc.pineclone.workflow.trigger.jnativehook.api.NativeInputEventSource;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

public class NativeMouseEventSource
        extends NativeInputEventSource<NativeMouseEvent>
        implements NativeMouseListener {

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeEvent) {
        fire(nativeEvent);
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        fire(nativeEvent);
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        fire(nativeEvent);
    }

    @Override
    public void install() {
        GlobalScreen.addNativeMouseListener(this);
    }

    @Override
    public void uninstall() {
        GlobalScreen.removeNativeMouseListener(this);
    }
}
