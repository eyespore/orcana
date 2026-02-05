package cc.pineclone.interaction;

import cc.pineclone.interaction.api.ModifierConstraint;

import java.util.Set;

public record NeuModifierConstraint(
        Set<NeuModifierKey> required,
        Set<NeuModifierKey> forbidden
) implements ModifierConstraint {

    public enum NeuModifierKey {
        SHIFT,
        SHIFT_L,  /* 左 SHIFT */
        SHIFT_R,
        CONTROL,
        CONTROL_L,
        CONTROL_R,
        ALT,
        ALT_L,
        ALT_R,
        META,
        META_L,
        META_R,
        BUTTON_1,    // 鼠标左键
        BUTTON_2,    // 鼠标中键
        BUTTON_3,    // 鼠标右键
        BUTTON_4,    // 鼠标额外键1
        BUTTON_5,    // 鼠标额外键2
        NUM_LOCK,   // 数字锁定键
        CAPS_LOCK,  // 大写锁定键
        SCROLL_LOCK // 滚动锁定键
    }
}
