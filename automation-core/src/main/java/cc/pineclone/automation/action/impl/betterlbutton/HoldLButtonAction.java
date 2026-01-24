package cc.pineclone.automation.action.impl.betterlbutton;

import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.MouseButton;

public class HoldLButtonAction extends Action {

    private static final String ACTION_ID = "better-l-button";
    private boolean running = false;

    private final VCRobotAdapter robot;
    private final Key leftButton = new Key(MouseButton.PRIMARY);

    public HoldLButtonAction() {
        super(ACTION_ID);
        robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public void activate(MacroEvent event) {
        if (running) return;
        running = true;
        robot.mousePress(leftButton);
    }

    @Override
    public void deactivate(MacroEvent event) {
        if (!running) return;
        robot.mouseRelease(leftButton);
        running = false;
    }
}
