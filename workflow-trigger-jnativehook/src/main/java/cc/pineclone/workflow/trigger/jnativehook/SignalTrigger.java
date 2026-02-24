package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.NeuMouseWheelSpec;
import cc.pineclone.interaction.api.InteractionSpec;
import cc.pineclone.interaction.api.ModifierConstraint;
import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.trigger.jnativehook.api.*;
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
            TriggerIdentity identity,
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

        private final Function<String, TriggerEventIdentity> eventIdentityFunction;

        protected MouseWheelTrigger(
                TriggerIdentity identity,
                Function<String, TriggerEventIdentity> eventIdentityFunction,
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
                TriggerIdentity identity,
                Function<String, TriggerEventIdentity> eventIdentityFunction,
                SpecFilter<NeuMouseWheelSpec, JNativeHookMouseWheelSpec, NeuModifierConstraint, JNativeHookModifierConstraint, NativeMouseWheelEvent> filter,
                NativeInputEventSource<NativeMouseWheelEvent> source) {
            super(identity, eventIdentityFunction, filter, source);
        }
    }
}
