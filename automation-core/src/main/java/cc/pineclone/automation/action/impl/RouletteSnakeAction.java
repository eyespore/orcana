package cc.pineclone.automation.action.impl;

import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.ScheduledAction;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;

public class RouletteSnakeAction extends ScheduledAction {

    private final Key snakeKey;
    private final VCRobotAdapter robot;
    private static final String ACTION_ID = "roulette-snake";

    /**
     * 轮盘吃零食宏，执行时会协助快速按下零食键，从而尽可能提升恢复血量的速度
     * @param interval 吃零食间隔
     * @param snakeKey 吃零食按键
     */
    public RouletteSnakeAction(long interval, Key snakeKey) {
        super(ACTION_ID, interval);
        this.snakeKey = snakeKey;
        this.robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public void schedule(MacroEvent event) {
        try {
            robot.simulate(this.snakeKey);
        } catch (InterruptedException ignored) {
        }
    }

}

