package cc.pineclone.eventflow.plugin.trigger.jnativehook.filter;

import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.NeuMouseWheelSpec;
import cc.pineclone.eventflow.interaction.api.InteractionSpecAdapter;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.JNativeHookMouseWheelSpec;

public class NeuMouseWheelSpecFilter
        extends MouseWheelSpecFilter<NeuMouseWheelSpec, NeuModifierConstraint> {

    private static final InteractionSpecAdapter<NeuMouseWheelSpec, JNativeHookMouseWheelSpec> NEU_SPEC_ADAPTER = neuMouseWheelSpec -> {
        int wheelDirection = switch (neuMouseWheelSpec.direction()) {
            case UP -> -1;
            case DOWN -> 1;
        };
        return new JNativeHookMouseWheelSpec(wheelDirection);
    };

    public NeuMouseWheelSpecFilter(
            NeuMouseWheelSpec originalSpec,
            NeuModifierConstraint originalModifier) {
        super(originalSpec, NEU_SPEC_ADAPTER, originalModifier, NeuModifierAdapter.getINSTANCE());
    }
}
