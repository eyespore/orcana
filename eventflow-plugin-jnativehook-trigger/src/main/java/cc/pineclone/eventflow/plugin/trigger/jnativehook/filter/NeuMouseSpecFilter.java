package cc.pineclone.eventflow.plugin.trigger.jnativehook.filter;

import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.NeuMouseSpec;
import cc.pineclone.eventflow.interaction.api.InteractionSpecAdapter;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.JNativeHookMouseSpec;

import static com.github.kwhat.jnativehook.mouse.NativeMouseEvent.*;

public class NeuMouseSpecFilter extends MouseSpecFilter<NeuMouseSpec, NeuModifierConstraint> {

    private static final InteractionSpecAdapter<NeuMouseSpec, JNativeHookMouseSpec> NEU_SPEC_ADAPTER = neuMouseButtonSpec -> {
        int button = switch (neuMouseButtonSpec.button()) {
            case BUTTON_1 -> BUTTON1;
            case BUTTON_2 -> BUTTON2;
            case BUTTON_3 -> BUTTON3;
            case BUTTON_4 -> BUTTON4;
            case BUTTON_5 -> BUTTON5;
        };
        return new JNativeHookMouseSpec(button);
    };

    public NeuMouseSpecFilter(NeuMouseSpec scope, NeuModifierConstraint modifier) {
        super(scope, NEU_SPEC_ADAPTER, modifier, NeuModifierAdapter.getINSTANCE());
    }
}
