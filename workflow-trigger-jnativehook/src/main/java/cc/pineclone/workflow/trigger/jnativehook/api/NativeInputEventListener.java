package cc.pineclone.workflow.trigger.jnativehook.api;

import com.github.kwhat.jnativehook.NativeInputEvent;

public interface NativeInputEventListener<E extends NativeInputEvent> {

    void onNativeInputEvent(E event);

}
