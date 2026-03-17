package cc.pineclone.automation.action.impl.swapglitch;

import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.action.ActionDecorator;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;

public class SwapMeleeAction extends ActionDecorator {

    private final long delay;
    private final Key hotkey;  /* 切换近战武器热键 */
    private final VCRobotAdapter robot;  /* 机器人 */

    public SwapMeleeAction(Action delegate, Key hotkey, long delay) {
        super(delegate);
        this.delay = delay;
        this.hotkey = hotkey;
        this.robot = RobotFactory.getRobot(delegate.getActionId());
    }

    /* 在动作(比如切枪偷速)开始之前，切换到近战武器 */
    @Override
    public boolean beforeActivate(MacroEvent event) {
        try {
            if (!delegate.beforeActivate(event)) return false;

            robot.simulate(hotkey);
            Thread.sleep(10);
            robot.simulate(hotkey);
            Thread.sleep(delay);
            return true;
        } catch (InterruptedException e) {
            /* 被中断时返回 false */
            return false;
        }
    }
}
