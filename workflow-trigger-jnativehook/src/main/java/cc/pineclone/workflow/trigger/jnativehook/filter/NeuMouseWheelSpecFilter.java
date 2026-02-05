package cc.pineclone.workflow.trigger.jnativehook.filter;

import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.NeuMouseWheelSpec;
import cc.pineclone.interaction.api.InteractionSpecAdapter;
import cc.pineclone.workflow.trigger.jnativehook.api.JNativeHookMouseWheelSpec;

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
