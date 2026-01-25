package cc.pineclone.automation.action.impl.bettermmenu;

import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.KeyCode;
import cc.pineclone.automation.input.MouseWheelScroll;

/* 更好 M 菜单 */
public class BetterMMenuAction extends Action {

    public static final String ACTION_ID = "better-m-menu";

    protected final long mouseScrollInterval;  /* 鼠标滚动间隔 */
    protected final long keyPressInterval;  /* 回车间隔 */
    protected final long timeUtilMMenuLoaded;  /* 等待 M 菜单加载时间 */

    private final VCRobotAdapter robot;

    private final Key mouseScrollDown = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN, 1));
    private final Key mouseScrollUp = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.UP, 1));
    private final Key enterKey = new Key(KeyCode.ENTER);
    private final Key escapeKey = new Key(KeyCode.ESCAPE);
    private final Key menuKey;  /* 菜单键 */

    public BetterMMenuAction(
            Key menuKey,
            long mouseScrollInterval,
            long keyPressInterval,
            long timeUtilMMenuLoaded) {

        super(ACTION_ID);
        this.menuKey = menuKey;
        this.mouseScrollInterval = mouseScrollInterval;
        this.keyPressInterval = keyPressInterval;
        this.timeUtilMMenuLoaded = timeUtilMMenuLoaded;

        this.robot = RobotFactory.getRobot();
    }

    protected void pressMenuKey() throws InterruptedException {
        robot.simulate(menuKey);
        Thread.sleep(keyPressInterval);
    }

    protected void mouseScrollDown() throws InterruptedException {
        robot.mouseWheel(mouseScrollDown);
        Thread.sleep(mouseScrollInterval);
    }

    protected void mouseScrollUp() throws InterruptedException {
        robot.mouseWheel(mouseScrollUp);
        Thread.sleep(mouseScrollInterval);
    }

    protected void pressEnter() throws InterruptedException {
        robot.simulate(enterKey);
        Thread.sleep(keyPressInterval);
    }

    protected void pressEscape() throws InterruptedException {
        robot.simulate(escapeKey);
        Thread.sleep(keyPressInterval);
    }
 }
