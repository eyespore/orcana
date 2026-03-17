package cc.pineclone.eventflow.plugin.trigger.jnativehook.filter;

import cc.pineclone.eventflow.interaction.api.InteractionSpec;
import cc.pineclone.eventflow.interaction.api.InteractionSpecAdapter;
import cc.pineclone.eventflow.interaction.api.ModifierConstraint;
import cc.pineclone.eventflow.interaction.api.ModifierConstraintAdapter;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.JNativeHookModifierConstraint;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.JNativeHookMouseSpec;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.SpecFilter;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class MouseSpecFilter<
        T extends InteractionSpec,
        M extends ModifierConstraint>
        extends SpecFilter<
        T, JNativeHookMouseSpec,
        M, JNativeHookModifierConstraint,
        NativeMouseEvent> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public MouseSpecFilter(
            T originalSpec, InteractionSpecAdapter<T, JNativeHookMouseSpec> specAdapter,
            M originalModifier, ModifierConstraintAdapter<M, JNativeHookModifierConstraint> modifierAdapter) {
        super(originalSpec, specAdapter, originalModifier, modifierAdapter);
    }

    @Override
    public Optional<T> test(NativeMouseEvent event) {
        if (!compiledSpec.match(event.getButton())) return Optional.empty();
        if (!compiledModifier.match(event.getModifiers())) return Optional.empty();
        return Optional.of(this.originalSpec);
    }

}
