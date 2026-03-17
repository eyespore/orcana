package cc.pineclone.eventflow.plugin.trigger.jnativehook.filter;

import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.api.ModifierConstraintAdapter;
import cc.pineclone.eventflow.interaction.exception.ModifierConstraintAdapteeException;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.JNativeHookModifierConstraint;

import java.util.Set;

import static com.github.kwhat.jnativehook.NativeInputEvent.*;

public class NeuModifierAdapter implements ModifierConstraintAdapter<NeuModifierConstraint, JNativeHookModifierConstraint> {

    private static final NeuModifierAdapter INSTANCE = new NeuModifierAdapter();

    protected static NeuModifierAdapter getINSTANCE() {
        return INSTANCE;
    }

    private NeuModifierAdapter() {}

    @Override
    public JNativeHookModifierConstraint adaptee(NeuModifierConstraint neuModifierConstraint) throws ModifierConstraintAdapteeException {
        return new JNativeHookModifierConstraint(
                mapModifier(neuModifierConstraint.required()),
                mapModifier(neuModifierConstraint.forbidden()));
    }

    private int mapModifier(Set<NeuModifierConstraint.NeuModifierKey> neuModifierKeySet) {
        int mask = 0;
        for (NeuModifierConstraint.NeuModifierKey key : neuModifierKeySet) {
            switch (key) {
                case SHIFT_L -> mask |= SHIFT_L_MASK;
                case SHIFT_R -> mask |= SHIFT_R_MASK;
                case SHIFT -> mask |= SHIFT_L_MASK | SHIFT_R_MASK;
                case CONTROL_L -> mask |= CTRL_L_MASK;
                case CONTROL_R -> mask |= CTRL_R_MASK;
                case CONTROL -> mask |= CTRL_L_MASK | CTRL_R_MASK;
                case ALT_L -> mask |= ALT_L_MASK;
                case ALT_R -> mask |= ALT_R_MASK;
                case ALT -> mask |= ALT_L_MASK | ALT_R_MASK;
                case META_L -> mask |= META_L_MASK;
                case META_R -> mask |= META_R_MASK;
                case META -> mask |= META_L_MASK | META_R_MASK;
                case BUTTON_1 -> mask |= BUTTON1_MASK;
                case BUTTON_2 -> mask |= BUTTON2_MASK;
                case BUTTON_3 -> mask |= BUTTON3_MASK;
                case BUTTON_4 -> mask |= BUTTON4_MASK;
                case BUTTON_5 -> mask |= BUTTON5_MASK;
                case NUM_LOCK -> mask |= NUM_LOCK_MASK;
                case CAPS_LOCK -> mask |= CAPS_LOCK_MASK;
                case SCROLL_LOCK -> mask |= SCROLL_LOCK_MASK;
            }
        }
        return mask;
    }
}
