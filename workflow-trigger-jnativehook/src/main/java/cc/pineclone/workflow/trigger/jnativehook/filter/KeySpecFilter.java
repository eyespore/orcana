package cc.pineclone.workflow.trigger.jnativehook.filter;

import cc.pineclone.interaction.api.InteractionSpec;
import cc.pineclone.interaction.api.InteractionSpecAdapter;
import cc.pineclone.interaction.api.ModifierConstraint;
import cc.pineclone.interaction.api.ModifierConstraintAdapter;
import cc.pineclone.workflow.trigger.jnativehook.api.JNativeHookKeySpec;
import cc.pineclone.workflow.trigger.jnativehook.api.JNativeHookModifierConstraint;
import cc.pineclone.workflow.trigger.jnativehook.api.SpecFilter;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.util.Optional;

public class KeySpecFilter<
        T extends InteractionSpec,
        M extends ModifierConstraint>
        extends SpecFilter<
        T, JNativeHookKeySpec,
        M, JNativeHookModifierConstraint,
        NativeKeyEvent> {

    public KeySpecFilter(
            T originalSpec, InteractionSpecAdapter<T, JNativeHookKeySpec> specAdapter,
            M originalModifier, ModifierConstraintAdapter<M, JNativeHookModifierConstraint> modifierAdapter) {
        super(originalSpec, specAdapter, originalModifier, modifierAdapter);
    }

    @Override
    public Optional<T> test(NativeKeyEvent event) {
        if (!compiledSpec.match(event.getKeyCode(), event.getKeyLocation())) return Optional.empty();
        if (!compiledModifier.match(event.getModifiers())) return Optional.empty();
        return Optional.of(this.originalSpec);
    }
}
