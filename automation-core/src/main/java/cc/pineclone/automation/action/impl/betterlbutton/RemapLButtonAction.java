package cc.pineclone.automation.action.impl.betterlbutton;

import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.MouseButton;

public class RemapLButtonAction extends Action {

    private static final String ACTION_ID = "better-l-button";
    private static final Key leftButton = new Key(MouseButton.PRIMARY);
    private final VCRobotAdapter robot;

    public RemapLButtonAction() {
        super(ACTION_ID);
        robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public void activate(MacroEvent event) {
        robot.mousePress(leftButton);
    }

    @Override
    public void deactivate(MacroEvent event) {
        robot.mouseRelease(leftButton);
    }
}
