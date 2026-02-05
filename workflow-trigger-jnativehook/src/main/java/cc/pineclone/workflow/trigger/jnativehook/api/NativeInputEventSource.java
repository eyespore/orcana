package cc.pineclone.workflow.trigger.jnativehook.api;

import com.github.kwhat.jnativehook.NativeInputEvent;

import java.util.HashSet;
import java.util.Set;

public abstract class NativeInputEventSource<E extends NativeInputEvent> {

    protected final Set<NativeInputEventListener<E>> listeners;

    public NativeInputEventSource() {
        this.listeners = new HashSet<>();
    }

    public final void registerListener(NativeInputEventListener<E> listener) {
        listeners.add(listener);
    }

    public final void unregisterListener(NativeInputEventListener<E> listener) {
        listeners.remove(listener);
    }

    public abstract void install();

    public abstract void uninstall();

    public void fire(E event) {
        this.listeners.forEach(l -> l.onNativeInputEvent(event));
    }
}
