package cc.pineclone.eventflow.plugin.trigger.jnativehook;

import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.*;
import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.NeuMouseWheelSpec;
import cc.pineclone.eventflow.interaction.api.InteractionSpec;
import cc.pineclone.eventflow.interaction.api.ModifierConstraint;
import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.trigger.jnativehook.api.*;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;

import java.util.Map;
import java.util.function.Function;

public abstract class SignalTrigger<
                T extends InteractionSpec,  /* 外部统一 Spec 接口 */
                E extends JNativeHookSpec,  /* 内部统一 Spec 接口 */
                M extends ModifierConstraint,  /* 外部统一 Modifier 接口 */
                N extends JNativeHookModifierConstraint,  /* 内部统一 Modifier 接口 */
                P extends NativeInputEvent>  /* 监听事件 */
        extends FilterableTrigger<T, E, M, N, P> {

    protected SignalTrigger(
            ComponentId identity,
            SpecFilter<T, E, M, N, P> filter,
            NativeInputEventSource<P> source) {
        super(identity, filter, source);
    }

    public static class MouseWheelTrigger<
            T extends InteractionSpec,
            M extends ModifierConstraint>
            extends SignalTrigger<
                        T, JNativeHookMouseWheelSpec,
                        M, JNativeHookModifierConstraint, NativeMouseWheelEvent> {

        private final Function<String, EventSelector> eventIdentityFunction;

        protected MouseWheelTrigger(
                ComponentId identity,
                Function<String, EventSelector> eventIdentityFunction,
                SpecFilter<T, JNativeHookMouseWheelSpec, M, JNativeHookModifierConstraint, NativeMouseWheelEvent> filter,
                NativeInputEventSource<NativeMouseWheelEvent> source) {
            super(identity, filter, source);
            this.eventIdentityFunction = eventIdentityFunction;
        }

        @Override
        public void handleNativeInputEvent(NativeMouseWheelEvent event, T originalSpec) {
            String type = event.getWheelRotation() > 0 ? "SCROLL_UP" : "SCROLL_DOWN";
            emit(this.eventIdentityFunction.apply(type), Map.of("source_spec", originalSpec));
        }
    }

    public static class NeuMouseWheelTrigger extends MouseWheelTrigger<NeuMouseWheelSpec, NeuModifierConstraint> {
        protected NeuMouseWheelTrigger(
                ComponentId identity,
                Function<String, EventSelector> eventIdentityFunction,
                SpecFilter<NeuMouseWheelSpec, JNativeHookMouseWheelSpec, NeuModifierConstraint, JNativeHookModifierConstraint, NativeMouseWheelEvent> filter,
                NativeInputEventSource<NativeMouseWheelEvent> source) {
            super(identity, eventIdentityFunction, filter, source);
        }
    }
}
