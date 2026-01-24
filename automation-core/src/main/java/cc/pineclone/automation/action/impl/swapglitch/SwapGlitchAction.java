package cc.pineclone.automation.action.impl.swapglitch;

import cc.pineclone.automation.MacroPriorityContext;
import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.ScheduledAction;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;

public class SwapGlitchAction extends ScheduledAction {

    private final VCRobotAdapter robot;
    private final Key hotkey;

    protected static final String ACTION_ID = "swap-glitch";
    MacroPriorityContext context = MacroPriorityContext.getInstance();

    /**
     * 切枪偷速宏，辅助重复点按武器轮盘键，实现切枪保持小哑巴的战斗状态，偷取额外的移动速度
     * @param hotkey 武器轮盘键
     * @param interval 切枪间隔，经过实测50ms最佳
     */
    public SwapGlitchAction(Key hotkey, long interval) {
        super(ACTION_ID, interval);
        this.hotkey = hotkey;
        robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public void schedule(MacroEvent event) {
        if (context.blockSwapGlitch.get()) return;
        try {
            robot.simulate(this.hotkey);
        } catch (InterruptedException ignored) {
        }
    }
}
