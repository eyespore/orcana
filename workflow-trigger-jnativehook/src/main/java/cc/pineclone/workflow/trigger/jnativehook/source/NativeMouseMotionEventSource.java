package cc.pineclone.workflow.trigger.jnativehook.source;

import cc.pineclone.workflow.trigger.jnativehook.api.NativeInputEventSource;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

public class NativeMouseMotionEventSource
        extends NativeInputEventSource<NativeMouseEvent>
        implements NativeMouseMotionListener {

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
        fire(nativeEvent);
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        fire(nativeEvent);
    }

    @Override
    public void install() {
        GlobalScreen.addNativeMouseMotionListener(this);
    }

    @Override
    public void uninstall() {
        GlobalScreen.removeNativeMouseMotionListener(this);
    }
}
