package cc.pineclone.eventflow.plugin.trigger.jnativehook.source;

import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.NativeInputEventSource;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;

public class NativeMouseWheelEventSource
        extends NativeInputEventSource<NativeMouseWheelEvent>
        implements NativeMouseWheelListener {

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeEvent) {
        this.fire(nativeEvent);
    }

    @Override
    public void install() {
        GlobalScreen.addNativeMouseWheelListener(this);
    }

    @Override
    public void uninstall() {
        GlobalScreen.removeNativeMouseWheelListener(this);
    }
}
