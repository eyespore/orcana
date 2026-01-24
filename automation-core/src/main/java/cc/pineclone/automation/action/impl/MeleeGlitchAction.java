package cc.pineclone.automation.action.impl;

import cc.pineclone.automation.MacroPriorityContext;
import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.ScheduledAction;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;

public class MeleeGlitchAction extends ScheduledAction {

    private final Key meleeSnakeScrollKey;  /* 近战武器零食滚轮键 */
    private final VCRobotAdapter robot;  /* 执行机器人 */

    MacroPriorityContext context = MacroPriorityContext.getInstance();
    public static final String ACTION_ID = "melee-glitch";

    @Override
    public boolean beforeActivate(MacroEvent event) {
        context.blockSwapGlitch.set(true);  /* 动作开始之前，阻塞切枪偷速 */
        return true;
    }

    @Override
    public void afterDeactivate(MacroEvent event) {
        context.blockSwapGlitch.set(false);  /* 动作完全撤销之后，允许切枪偷速 */
    }

    /* 近战偷速宏 */
    public MeleeGlitchAction(long interval, Key meleeSnakeScrollKey) {
        super(ACTION_ID, interval);
        this.meleeSnakeScrollKey = meleeSnakeScrollKey;
        this.robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public void schedule(MacroEvent event) {
        try {
            robot.simulate(meleeSnakeScrollKey);
        } catch (InterruptedException ignored) {

        }
    }
}
