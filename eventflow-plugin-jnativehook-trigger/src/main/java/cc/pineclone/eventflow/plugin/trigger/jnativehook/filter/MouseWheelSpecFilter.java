package cc.pineclone.eventflow.plugin.trigger.jnativehook.filter;

import cc.pineclone.eventflow.interaction.api.InteractionSpec;
import cc.pineclone.eventflow.interaction.api.InteractionSpecAdapter;
import cc.pineclone.eventflow.interaction.api.ModifierConstraint;
import cc.pineclone.eventflow.interaction.api.ModifierConstraintAdapter;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.JNativeHookModifierConstraint;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.JNativeHookMouseWheelSpec;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.SpecFilter;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;

import java.util.Optional;

public class MouseWheelSpecFilter<
        T extends InteractionSpec,
        M extends ModifierConstraint>
        extends SpecFilter<
                                T, JNativeHookMouseWheelSpec,
                                M, JNativeHookModifierConstraint,
                                NativeMouseWheelEvent> {

    public MouseWheelSpecFilter(
            T originalSpec, InteractionSpecAdapter<T, JNativeHookMouseWheelSpec> specAdapter,
            M originalModifier, ModifierConstraintAdapter<M, JNativeHookModifierConstraint> modifierAdapter) {
        super(originalSpec, specAdapter, originalModifier, modifierAdapter);
    }

    @Override
    public Optional<T> test(NativeMouseWheelEvent event) {
        if (!compiledSpec.match(event.getWheelRotation())) return Optional.empty();
        if (!compiledModifier.match(event.getModifiers())) return Optional.empty();
        return Optional.of(this.originalSpec);
    }
}
