package cc.pineclone.automation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class MacroRegistry implements WindowTitleListener
{

    private static final String GTAV_WINDOW_TITLE = "Grand Theft Auto V";  /* 增强 & 传承标题相同 */

    private volatile boolean globalSuspended = true;
    private final Map<UUID, Macro> registry = new LinkedHashMap<>();
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final PlatformFocusMonitor platformFocusMonitor;

    public MacroRegistry(PlatformFocusMonitor platformFocusMonitor) {
        this.platformFocusMonitor = platformFocusMonitor;
        this.platformFocusMonitor.addListener(this);
    }

    /* 宏注册入口，将宏注册到上下文 */
    public UUID register(Macro macro) {
        UUID uuid = UUID.randomUUID();
        registry.put(uuid, macro);
        if (globalSuspended) macro.suspend();  /* 创建宏时，如果全局处于挂起状态，那么需要将新生的宏挂起 */
        return uuid;
    }

    /* 启用某个宏，通常由GUI中的开关直接控制 */
    public boolean launchMacro(UUID uuid) {
        Macro macro = registry.get(uuid);
        if (macro == null) return false;
        macro.launch();
        return true;
    }

    /* 停止某个宏 */
    public boolean terminateMacro(UUID uuid) {
        Macro macro = registry.get(uuid);
        if (macro == null) return false;
        macro.terminate();
        registry.remove(uuid);
        return true;
    }

    /* GTA OPS通过监听当前用户焦点窗口判断用户是否在游戏内，当用户切出游戏时会将所有的宏挂起 */
    @Override
    public void accept(String s) {
        log.debug(s);

        if (s.equals(GTAV_WINDOW_TITLE)) {  /* 用户切回游戏，恢复所有的宏 */
            globalSuspended = false;
            registry.values().forEach(Macro::resume);
        } else {  /* 用户切出游戏，挂起所有的宏 */
//            log.debug("macro to be suspended count: {}", registry);
            globalSuspended = true;
            registry.values().forEach(Macro::suspend);
        }
    }
}