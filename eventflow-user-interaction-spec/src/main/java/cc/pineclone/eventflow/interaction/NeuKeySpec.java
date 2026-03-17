package cc.pineclone.eventflow.interaction;

public record NeuKeySpec(
        NeuKey key,
        NeuKeyLocation keyLocation
) implements NeuSpec {

    public enum NeuKey {
        ESCAPE,  /* ESC */
        F1,  /* F1 */
        F2,  /* F2 */
        F3,  /* F3 */
        F4,  /* F4 */
        F5,  /* F5 */
        F6,  /* F6 */
        F7,  /* F7 */
        F8,  /* F8 */
        F9,  /* F9 */
        F10,  /* F10 */
        F11,  /* F11 */
        F12,  /* F12 */
        F13,  /* F13 */
        F14,  /* F14 */
        F15,  /* F15 */
        F16,  /* F16 */
        F17,  /* F17 */
        F18,  /* F18 */
        F19,  /* F19 */
        F20,  /* F20 */
        F21,  /* F21 */
        F22,  /* F22 */
        F23,  /* F23 */
        F24,  /* F24 */
        BACKQUOTE,  /* ~ ` */
        NUM_1,  /* 1 */
        NUM_2,  /* 2 */
        NUM_3,  /* 3 */
        NUM_4,  /* 4 */
        NUM_5,  /* 5 */
        NUM_6,  /* 6 */
        NUM_7,  /* 7 */
        NUM_8,  /* 8 */
        NUM_9,  /* 9 */
        NUM_0,  /* 10 */
        MINUS,  /* - _ */
        EQUALS,  /* + = */
        BACKSPACE,  /* BACKSPACE */
        TAB,  /* TAB */
        CAPS_LOCK,  /* CAPS LOCK */
        A,  /* A */
        B,  /* B */
        C,  /* C */
        D,  /* D */
        E,  /* E */
        F,  /* F */
        G,  /* G */
        H,  /* H */
        I,  /* I */
        J,  /* J */
        K,  /* K */
        L,  /* L */
        M,  /* M */
        N,  /* N */
        O,  /* O */
        P,  /* P */
        Q,  /* Q */
        R,  /* R */
        S,  /* S */
        T,  /* T */
        U,  /* U */
        V,  /* V */
        W,  /* W */
        X,  /* X */
        Y,  /* Y */
        Z,  /* Z */
        OPEN_BRACKET,  /* { [ */
        CLOSE_BRACKET,  /* } ] */
        BACK_SLASH,  /* \ | */
        SEMICOLON,  /* : ; */
        QUOTE,  /* " ' */
        ENTER,  /* ENTER */
        COMMA,  /* , < */
        PERIOD,  /* . > */
        SLASH,  /* / ? */
        SPACE,  /* SPACE */
        PRINT_SCREEN,  /* PRINT SCREEN */
        SCROLL_LOCK,  /* SCROLL_LOCK */
        PAUSE,  /* PAUSE */
        INSERT,  /* INSERT */
        DELETE,  /* DEL */
        HOME,  /* HOME */
        END,  /*END */
        PAGE_UP,  /* PGUP */
        PAGE_DOWN,  /* PGDN */
        UP,  /* ↑ */
        LEFT,  /* ← */
        CLEAR,  /* CLEAR */
        RIGHT,  /* → */
        DOWN,  /* ↓ */
        NUM_LOCK,  /* NUM LOCK */
        SEPARATOR,  /*  */
        SHIFT,  /* SHIFT */
        CONTROL,  /* CTRL */
        ALT,  /* ALT */
        META,  /* META */
        CONTEXT_MENU,
        POWER,
        SLEEP,
        WAKE,
        MEDIA_PLAY,
        MEDIA_STOP,
        MEDIA_PREVIOUS,
        MEDIA_NEXT,
        MEDIA_SELECT,
        MEDIA_EJECT,
        VOLUME_MUTE,
        VOLUME_UP,
        VOLUME_DOWN,
        APP_MAIL,  /* 邮箱 */
        APP_CALCULATOR,  /* 计算器 */
        APP_MUSIC,  /* 音乐 */
        APP_PICTURES,  /* 图片 */
        BROWSER_SEARCH,  /* 浏览器检索 */
        BROWSER_HOME,  /* 浏览器主页 */
        BROWSER_BACK,  /* 浏览器后退 */
        BROWSER_FORWARD,  /* 浏览器前进 */
        BROWSER_STOP,  /* 浏览器停止加载 */
        BROWSER_REFRESH,  /* 浏览器刷新 */
        BROWSER_FAVORITES,  /* 浏览器收藏 */
        KATAKANA,
    //    UNDERSCORE,  /* _ */  /* SHIFT + MINUS */
        FURIGANA,  /* FURIGANA */
        KANJI,  /* KANJI */
        HIRAGANA,  /* HIRAGANA */
        YEN,  /* YEN */
        SUN_HELP,  /* SUN HELP */
        SUN_STOP,  /* SUN STOP */
        SUN_PROPS,  /* SUN PROPS */
        SUN_FRONT,  /* SUN FRONT */
        SUN_OPEN,  /* SUN OPEN */
        SUN_FIND,  /* SUN FIND */
        SUN_AGAIN,  /* SUN AGAIN */
        SUN_UNDO,  /* SUN UNDO */
        SUN_COPY,  /* SUN COPY */
        SUN_INSERT,  /* SUN INSERT */
        SUN_CUT,  /* SUN CUT */
        UNDEFINED  /* UNKNOWN */
    }

    public enum NeuKeyLocation {
        UNKNOWN,   /* 未知区域键盘 */
        STANDARD,  /* 主区域键盘 */
        LEFT,   /* 右侧键 */
        RIGHT,  /* 左侧键 */
        NUMPAD;  /* 数字键盘 */
    }
}
