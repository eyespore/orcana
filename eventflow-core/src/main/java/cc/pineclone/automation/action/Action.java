package cc.pineclone.automation.action;

import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.MacroLifecycleAware;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Action implements ActionLifecycle, MacroLifecycleAware {

    /* 主要用于装饰器辨别当前父动作，以获取缓存的机器人实例 */
    @Getter protected final String actionId;
//    @Setter private boolean enable = true;
//    /* 挂起状态位，当宏挂起时动作应该被拒绝执行（例如不在指定程序当中） */
//    @Setter private boolean suspended = false;
    /* 激活状态位，如果需要子ACTION，那么可以通过这个状态位来控制ACTION是否被响应 */

    protected Logger log = LoggerFactory.getLogger(getClass());

    public Action(final String actionId) {
        this.actionId = actionId;
    }

    public final void doActivate(MacroEvent event) {
        boolean flag = beforeActivate(event);
        if (flag) {
            activate(event);
            afterActivate(event);
        }
    }

    public final void doDeactivate(MacroEvent event) {
        boolean flag = beforeDeactivate(event);
        if (flag) {
            deactivate(event);
            afterDeactivate(event);
        }
    }

    @Override
    public final void onMacroSuspend(MacroEvent event) {
        this.doDeactivate(event);
    }
}
