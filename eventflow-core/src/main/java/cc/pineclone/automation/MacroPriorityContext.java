package cc.pineclone.automation;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

public class MacroPriorityContext {

    @Getter
    private static final MacroPriorityContext instance = new MacroPriorityContext();

    /* 宏运行优先级 */
    @Deprecated
    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
    }

    @Deprecated
    public static class MacroToken {
        final String macroId;  /* 宏id */
        final Priority priority;  /* 宏优先级 */
        final AtomicBoolean blocked;  /* 宏是否被阻塞 */

        MacroToken(String macroId, Priority priority) {
            this.macroId = macroId;
            this.priority = priority;
            this.blocked = new AtomicBoolean(false);
        }
    }

    /* 是否允许切枪偷速，用于兼容近战偷速与切枪偷速 */
    public final AtomicBoolean blockSwapGlitch = new AtomicBoolean(false);
    /* 是否允许快速切枪，用于兼容快速切枪 */
    public final AtomicBoolean blockQuickSwap = new AtomicBoolean(false);

}
