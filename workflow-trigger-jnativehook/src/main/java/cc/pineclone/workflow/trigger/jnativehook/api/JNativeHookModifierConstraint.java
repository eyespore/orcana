package cc.pineclone.workflow.trigger.jnativehook.api;

import cc.pineclone.interaction.api.ModifierConstraint;

import static com.github.kwhat.jnativehook.NativeInputEvent.*;

public record JNativeHookModifierConstraint(
        int requiredMask,
        int forbiddenMask
) implements ModifierConstraint {

    private static final int SHIFT_MASK_ALL = SHIFT_L_MASK | SHIFT_R_MASK;
    private static final int CTRL_MASK_ALL = CTRL_L_MASK | CTRL_R_MASK;
    private static final int ALT_MASK_ALL = ALT_L_MASK | ALT_R_MASK;
    private static final int META_MASK_ALL = META_L_MASK | META_R_MASK;

    public boolean match(int modifierMask) {
        if ((requiredMask & SHIFT_MASK_ALL) != 0 && (modifierMask & SHIFT_MASK_ALL) == 0) return false;
        if ((requiredMask & CTRL_MASK_ALL) != 0 && (modifierMask & CTRL_MASK_ALL) == 0) return false;
        if ((requiredMask & ALT_MASK_ALL) != 0 && (modifierMask & ALT_MASK_ALL) == 0) return false;
        if ((requiredMask & META_MASK_ALL) != 0 && (modifierMask & META_MASK_ALL) == 0) return false;

        int normalRequired = requiredMask & ~(SHIFT_MASK_ALL | CTRL_MASK_ALL | ALT_MASK_ALL | META_MASK_ALL);
        if ((modifierMask & normalRequired) != normalRequired) return false;
        return (modifierMask & forbiddenMask) == 0;
    }
}
