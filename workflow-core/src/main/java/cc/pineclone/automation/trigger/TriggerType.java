package cc.pineclone.automation.trigger;

import cc.pineclone.automation.input.Key;

public enum TriggerType {
    KEYBOARD,       // 键盘按键触发
    MOUSE_BUTTON,   // 鼠标按钮触发（左键、右键、中键等）
    SCROLL_WHEEL;    // 鼠标滚轮触发（滚动方向）

    // 根据按键返回执行类型
    public static TriggerType of(Key key) {
        if (key.key != null) return KEYBOARD;
        else if (key.scroll != null) return SCROLL_WHEEL;
        else if (key.button != null) return MOUSE_BUTTON;
        throw new IllegalArgumentException();
    }
}
