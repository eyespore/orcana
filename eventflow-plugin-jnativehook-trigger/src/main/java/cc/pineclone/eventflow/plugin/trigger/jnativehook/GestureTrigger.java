package cc.pineclone.eventflow.plugin.trigger.jnativehook;

import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.*;
import cc.pineclone.eventflow.interaction.NeuKeySpec;
import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.NeuMouseSpec;
import cc.pineclone.eventflow.interaction.api.Gesture;
import cc.pineclone.eventflow.interaction.api.InteractionSpec;
import cc.pineclone.eventflow.interaction.api.ModifierConstraint;
import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.trigger.jnativehook.api.*;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

/* 手势触发器 */
public abstract class GestureTrigger<
                T extends InteractionSpec,  /* 外部统一 Spec 接口 */
                E extends JNativeHookSpec,  /* 内部统一 Spec 接口 */
                M extends ModifierConstraint,  /* 外部统一 Modifier 接口 */
                N extends JNativeHookModifierConstraint,  /* 内部统一 Modifier 接口 */
                P extends NativeInputEvent>  /* 监听事件 */
        extends FilterableTrigger<T, E, M, N, P> {

            private final GestureInterpreter<P> gesture;  /* 触发手势 */
    private final Function<Gesture, EventSelector> eventIdentityFunction;
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected GestureTrigger(
            ComponentId identity,
            Function<Gesture, EventSelector> eventIdentityFunction,
            SpecFilter<T, E, M, N, P> filter,
            GestureInterpreter<P> gesture,
            NativeInputEventSource<P> source) {
        super(identity, filter, source);
        this.eventIdentityFunction = eventIdentityFunction;
        this.gesture = gesture;
    }

    @Override
    public void handleNativeInputEvent(P event, T originalSpec) {
//        log.debug("GestureTrigger received event: {}", event.paramString());
        gesture.interpret(event, gesture -> emit(
                        eventIdentityFunction.apply(gesture),
                        Map.of("source_spec", originalSpec)));
    }

    public static class NeuKeyTrigger extends GestureTrigger<
            NeuKeySpec,
            JNativeHookKeySpec,
            NeuModifierConstraint,
            JNativeHookModifierConstraint,
            NativeKeyEvent> {

        protected NeuKeyTrigger(
                ComponentId identity,
                Function<Gesture, EventSelector> eventIdentityFunction,
                SpecFilter<NeuKeySpec, JNativeHookKeySpec, NeuModifierConstraint, JNativeHookModifierConstraint, NativeKeyEvent> filter,
                GestureInterpreter<NativeKeyEvent> gesture,
                NativeInputEventSource<NativeKeyEvent> source) {
            super(identity, eventIdentityFunction, filter, gesture, source);
        }

    }

    public static class NeuMouseTrigger extends GestureTrigger<
            NeuMouseSpec,
            JNativeHookMouseSpec,
            NeuModifierConstraint,
            JNativeHookModifierConstraint,
            NativeMouseEvent> {

        protected NeuMouseTrigger(ComponentId identity, Function<Gesture, EventSelector> eventIdentityFunction, SpecFilter<NeuMouseSpec, JNativeHookMouseSpec, NeuModifierConstraint, JNativeHookModifierConstraint, NativeMouseEvent> filter, GestureInterpreter<NativeMouseEvent> gesture, NativeInputEventSource<NativeMouseEvent> source) {
            super(identity, eventIdentityFunction, filter, gesture, source);
        }
    }
}
