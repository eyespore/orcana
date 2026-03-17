package cc.pineclone.eventflow.plugin.trigger.jnativehook.api;

import cc.pineclone.eventflow.interaction.api.Gesture;
import com.github.kwhat.jnativehook.NativeInputEvent;

import java.util.function.Consumer;

/* 手势解释器，对Source产生的事件进行解释 */
public interface GestureInterpreter<E extends NativeInputEvent> {

    void interpret(E event, Consumer<Gesture> callback);

}
