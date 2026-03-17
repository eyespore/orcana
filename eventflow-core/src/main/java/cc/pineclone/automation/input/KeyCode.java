package cc.pineclone.automation.input;

import java.util.HashMap;
import java.util.Map;

public enum KeyCode {

    ESCAPE(0x0001, "Escape"),
    F1(0x003B, "F1"),
    F2(0x003C, "F2"),
    F3(0x003D, "F3"),
    F4(0x003E, "F4"),
    F5(0x003F, "F5"),
    F6(0x0040, "F6"),
    F7(0x0041, "F7"),
    F8(0x0042, "F8"),
    F9(0x0043, "F9"),
    F10(0x0044, "F10"),
    F11(0x0057, "F11"),
    F12(0x0058, "F12"),
    BACKQUOTE(0x0029, "Tilde"),
    KEY_1(0x0002, "One"),
    KEY_2(0x0003, "Two"),
    KEY_3(0x0004, "Three"),
    KEY_4(0x0005, "Four"),
    KEY_5(0x0006, "Five"),
    KEY_6(0x0007, "Six"),
    KEY_7(0x0008, "Seven"),
    KEY_8(0x0009, "Eight"),
    KEY_9(0x000A, "Nine"),
    KEY_0(0x000B, "Zero"),
    MINUS(0x000C, "Hyphen"),
    EQUALS(0x000D, "Equals"),
    BACKSPACE(0x000E, "BackSpace"),
    TAB(0x000F, "Tab"),
    A(0x001E, "A"),
    B(0x0030, "B"),
    C(0x002E, "C"),
    D(0x0020, "D"),
    E(0x0012, "E"),
    F(0x0021, "F"),
    G(0x0022, "G"),
    H(0x0023, "H"),
    I(0x0017, "I"),
    J(0x0024, "J"),
    K(0x0025, "K"),
    L(0x0026, "L"),
    M(0x0032, "M"),
    N(0x0031, "N"),
    O(0x0018, "O"),
    P(0x0019, "P"),
    Q(0x0010, "Q"),
    R(0x0013, "R"),
    S(0x001F, "S"),
    T(0x0014, "T"),
    U(0x0016, "U"),
    V(0x002F, "V"),
    W(0x0011, "W"),
    X(0x002D, "X"),
    Y(0x0015, "Y"),
    Z(0x002C, "Z"),
    OPEN_BRACKET(0x001A, "LeftBracket"),
    CLOSE_BRACKET(0x001B, "RightBracket"),
    BACK_SLASH(0x002B, "Backslash"),
    SEMICOLON(0x0027, "Semicolon"),
    QUOTE(0x0028, "Quote"),
    ENTER(0x001C, "Enter"),
    COMMA(0x0033, "Comma"),
    PERIOD(0x0034, "Period"),
    SLASH(0x0035, "Slash"),
    SPACE(0x0039, "SpaceBar"),
    INSERT(0x0E52, "Insert"),
    DELETE(0x0E53, "Delete"),
    HOME(0x0E47, "Home"),
    END(0x0E4F, "End"),
    PAGE_UP(0x0E49, "PageUp"),
    PAGE_DOWN(0x0E51, "PageDown"),
    UP(0xE048, "Up"),
    LEFT(0xE04B, "Left"),
    RIGHT(0xE04D, "Right"),
    DOWN(0xE050, "Down"),
    SHIFT(0x002A, "Shift", true),
    CONTROL(0x001D, "Control", true),
    ALT(0x0038, "Alt", true),
    ;
    public final int code;
    public final String ueText;
    public final boolean requireLeftRight;

    KeyCode(int code, String ueText) {
        this(code, ueText, false);
    }

    KeyCode(int code, String ueText, boolean requireLeftRight) {
        this.code = code;
        this.ueText = ueText;
        this.requireLeftRight = requireLeftRight;
    }

    private static final KeyCode[] codeMap;
    private static final Map<String, KeyCode> ueTextMap = new HashMap<>();

    static {
        int max = 0;
        for (var c : values()) {
            if (c.code > max) {
                max = c.code;
            }
        }
        codeMap = new KeyCode[max + 1];
        for (var c : values()) {
            codeMap[c.code] = c;
            ueTextMap.put(c.ueText, c);
        }
    }

    public static KeyCode valueOf(int code) {
        if (code == 0x0e36) return SHIFT;
        if (code >= codeMap.length) return null;
        if (code < 0) return null;
        return codeMap[code];
    }

    public static KeyCode valueOfUeText(String ueText) {
        return ueTextMap.get(ueText);
    }

}
