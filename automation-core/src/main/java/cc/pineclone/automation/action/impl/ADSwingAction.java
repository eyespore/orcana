package cc.pineclone.automation.action.impl;

import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.ScheduledAction;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;

public class ADSwingAction extends ScheduledAction {

    private final VCRobotAdapter robot;
    private final Key moveLeftKey;
    private final Key moveRightKey;

    private boolean pressLeft = false;
    public static final String ACTION_ID = "ad-swing";

    /**
     * AD摇宏，执行宏的时候会依据设定的时间interval交替按下AD(可自定义)，从而实现在dc、近战偷速、
     * 轮盘回血时不丢失地速的效果
     * @param interval 触发AD的间隔
     * @param moveLeftKey 左移动键
     * @param moveRightKey 右移动键
     */
    public ADSwingAction(long interval, Key moveLeftKey, Key moveRightKey) {
        super(ACTION_ID, interval);
        this.moveLeftKey = moveLeftKey;
        this.moveRightKey = moveRightKey;
        this.robot = RobotFactory.getRobot("ad-swing");
    }

    @Override
    public void schedule(MacroEvent event) {
        try {
            if (pressLeft) {  /* 按下左 */
                pressLeft = false;
                robot.simulate(moveLeftKey);
            } else {  /* 按下右 */
                pressLeft = true;
                robot.simulate(moveRightKey);
            }
        } catch (InterruptedException ignored) {
        }
    }
}
