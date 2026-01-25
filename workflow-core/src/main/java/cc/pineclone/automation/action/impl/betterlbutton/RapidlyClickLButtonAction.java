package cc.pineclone.automation.action.impl.betterlbutton;

import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.ScheduledAction;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.MouseButton;

public class RapidlyClickLButtonAction extends ScheduledAction {

    private static final String ACTION_ID = "better-l-button";
    private static final Key leftButton = new Key(MouseButton.PRIMARY);
    private final VCRobotAdapter robot;

    public RapidlyClickLButtonAction(long interval) {
        super(ACTION_ID, interval);
        robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public void schedule(MacroEvent event) {
        try {
            robot.simulate(leftButton);
        } catch (InterruptedException ignored) {
        }
    }
}
