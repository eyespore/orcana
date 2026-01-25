package cc.pineclone.automation.utils;

import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.KeyCode;
import cc.pineclone.automation.input.MouseButton;
import cc.pineclone.automation.input.MouseWheelScroll;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class KeyUtils {

    /* -------------------- 按键字符串序列化 & 反序列化 */

    private static final Map<String, MouseButton> stringToMouseButtonMap = new HashMap<>();
    private static final Map<MouseButton, String> mouseButtonToStringMap = new HashMap<>() {{
        put(MouseButton.PRIMARY, "LeftMouseButton");
        put(MouseButton.SECONDARY, "RightMouseButton");
        put(MouseButton.MIDDLE, "MiddleMouseButton");
        put(MouseButton.BACK, "BackMouseButton");
        put(MouseButton.FORWARD, "ForwardMouseButton");
    }};

    private static final Map<String, KeyCode> stringToKeyCodeMap = new HashMap<>();
    private static final Map<KeyCode, String> keyCodeToStringMap = new HashMap<>() {{
        for (var c : KeyCode.values()) {
            put(c, c.ueText);
        }
    }};

    private static final Set<KeyCode> requireLeftRightKeys = new HashSet<>() {{
        add(KeyCode.CONTROL);
        add(KeyCode.SHIFT);
        add(KeyCode.ALT);
    }};

    static {
        mouseButtonToStringMap.forEach((k, v) -> stringToMouseButtonMap.put(v, k));
        keyCodeToStringMap.forEach((k, v) -> stringToKeyCodeMap.put(v, k));
    }

    /* 将Key实例转变为字符串 */
    public static String toString(Key key) {
        return toString(key.button, key.scroll, key.key, key.isLeftKey);
    }

    /* 对Key实例各个字段进行字符串序列化 */
    private static String toString(MouseButton button, MouseWheelScroll scroll, KeyCode key, boolean isLeftKey) {
        String ret = null;
        if (button != null) {
            ret = mouseButtonToStringMap.get(button);
        } else if (scroll != null) {
            ret = scroll.toString();
        } else if (key != null) {
            ret = keyCodeToStringMap.get(key);
            if (ret != null) {
                if (requireLeftRightKeys.contains(key)) {
                    ret = (isLeftKey ? "Left" : "Right") + ret;
                }
            }
        }
        return ret;
    }

    /* 将字符串构建成Key实例 */
    public static Key fromString(String str) {
        /* 滚轮 */
        if (str.startsWith("scroll-")) {
            String body = str.substring("scroll-".length());
            String[] parts = body.split(":", 2);

            String directionStr = parts[0];
            int value = parts.length == 2 ? Integer.parseInt(parts[1]) : 0;

            for (MouseWheelScroll.Direction direction : MouseWheelScroll.Direction.values()) {
                if (direction.name().equalsIgnoreCase(directionStr)) {
                    return new Key(new MouseWheelScroll(direction, value));
                }
            }
        }

        /* 左右功能键 */
        if (str.startsWith("Left") || str.startsWith("Right")) {
            boolean isLeft = str.startsWith("Left");
            String prefix = isLeft ? "Left" : "Right";
            String keyCodeStr = str.substring(prefix.length());
            KeyCode keyCode = stringToKeyCodeMap.get(keyCodeStr);
            return new Key(keyCode, isLeft);
        }

        /* 一般按键 */
        KeyCode keyCode = stringToKeyCodeMap.get(str);
        if (keyCode != null) return new Key(keyCode);

        /* 鼠标按键 */
        MouseButton button = stringToMouseButtonMap.get(str);
        if (button != null) return new Key(button);

        throw new IllegalArgumentException();
    }

    /* -------------------- JavaFX <-> jnativehook <-> jwt 键码映射
     * VK: jwt库robot执行动作使用的键码
     * VC: jnativehook库监听自然事件使用的键码
     * FX: JavaFX键码(主要是鼠标按键)
     * */

    private static final Map<Integer, Integer> VCToVKKeyCodeMap = buildVCToVKMap();
    private static final Map<Integer, Integer> VKToVCKeyCodeMap = VCToVKKeyCodeMap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    /* 通过反射建立 VCCode 到 VKCode 的映射 */
    private static Map<Integer, Integer> buildVCToVKMap() {
        Map<Integer, Integer> map = new HashMap<>();

        for (Field nf : NativeKeyEvent.class.getFields()) {  /* 通过反射获取所有 VC 开头的字段 */
            if (!Modifier.isStatic(nf.getModifiers()) || !nf.getName().startsWith("VC_")) continue;
            String vcName = nf.getName();  // e.g. "VC_A"
            String vkName = "VK_" + vcName.substring(3);  // "VK_A"
            try {
                int vcCode = nf.getInt(null);
                Field af = KeyEvent.class.getField(vkName);
                int vkCode = af.getInt(null);
                map.put(vcCode, vkCode);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        /* 补充一些 */
        map.put(NativeKeyEvent.VC_META, KeyEvent.VK_META);
        map.put(NativeKeyEvent.VC_CONTEXT_MENU, KeyEvent.VK_CONTEXT_MENU);
        map.put(NativeKeyEvent.VC_DELETE, KeyEvent.VK_DELETE);
        map.put(NativeKeyEvent.VC_BACKSPACE, KeyEvent.VK_BACK_SPACE);
        map.put(NativeKeyEvent.VC_CAPS_LOCK, KeyEvent.VK_CAPS_LOCK);
        map.put(NativeKeyEvent.VC_NUM_LOCK, KeyEvent.VK_NUM_LOCK);
        map.put(NativeKeyEvent.VC_SCROLL_LOCK, KeyEvent.VK_SCROLL_LOCK);

        return Collections.unmodifiableMap(map);  /* 不可修改视图 */
    }

    public static int toVKCode(KeyCode vcCode) {
        return VCToVKKeyCodeMap.getOrDefault(vcCode.code, KeyEvent.VK_UNDEFINED);
    }

    public static int toVCMouse(MouseButton button) {
        return FXToVCMouseMap.get(button);
    }

    public static int toVKMouse(MouseButton button) {
        return FXToVKMouseMap.get(button);
    }

    public static int toVCScroll(MouseWheelScroll scroll) {
        return VFXToVCScrollMap.get(scroll.direction());
    }

    /* JavaFX -> VC  */
    public static final Map<MouseButton, Integer> FXToVCMouseMap = new HashMap<>() {{
        put(MouseButton.PRIMARY, NativeMouseEvent.BUTTON1);
        put(MouseButton.SECONDARY, NativeMouseEvent.BUTTON2);
        put(MouseButton.MIDDLE, NativeMouseEvent.BUTTON3);
        put(MouseButton.BACK, NativeMouseEvent.BUTTON4);
        put(MouseButton.FORWARD, NativeMouseEvent.BUTTON5);
    }};

    /* JavaFX -> VK */
    public static final Map<MouseButton, Integer> FXToVKMouseMap = new HashMap<>() {{
        put(MouseButton.PRIMARY, InputEvent.BUTTON1_DOWN_MASK);
        put(MouseButton.SECONDARY, InputEvent.BUTTON3_DOWN_MASK);
        put(MouseButton.MIDDLE, InputEvent.BUTTON2_DOWN_MASK);
    }};

    public static final Map<MouseWheelScroll.Direction, Integer> VFXToVCScrollMap = new HashMap<>() {{
        put(MouseWheelScroll.Direction.UP, -1);
        put(MouseWheelScroll.Direction.DOWN, 1);
    }};
}
