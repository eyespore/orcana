package cc.pineclone.automation.action.impl.betterpmenu;

import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.KeyCode;
import cc.pineclone.automation.input.MouseWheelScroll;

/* 快速点火 */
public class JoinABookmarkedJobAction extends Action {

    private final long mouseScrollInterval;
    private final long enterKeyInterval;
    private final long timeUtilPMenuLoaded;
    private final long timeUtilJobsLoaded;

    private final VCRobotAdapter robot;

    public static final String ACTION_ID = "better-p-menu";

    private final Key enterKey = new Key(KeyCode.ENTER);
    private final Key rightKey = new Key(KeyCode.RIGHT);

    private final Key mouseScrollUpKey = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.UP, 1));
    private final Key mouseScrollDownKey = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN, 1));

    private final Key menuKey = new Key(KeyCode.P);  /* P 菜单按键 */

    public JoinABookmarkedJobAction(
            long mouseScrollInterval,
            long enterKeyInterval,
            long timeUtilPMenuLoaded,
            long timeUtilJobsLoaded
    ) {
        super(ACTION_ID);

        this.mouseScrollInterval = mouseScrollInterval;
        this.enterKeyInterval = enterKeyInterval;
        this.timeUtilPMenuLoaded = timeUtilPMenuLoaded;
        this.timeUtilJobsLoaded = timeUtilJobsLoaded;
        this.robot = RobotFactory.getRobot();
    }

    @Override
    public void activate(MacroEvent event) {
        try {
            pressP();
            Thread.sleep(200);
            pressRight();
            awaitTimeUtilPMenuLoaded();  /* 等待列表加载 */
            pressEnter();  /* 选择列表 */
            Thread.sleep(200);
            pressEnter();  /* 差事 */
            mouseScrollDown();  /* 进行差事 */
            Thread.sleep(200);
            pressEnter();
            Thread.sleep(200);
            mouseScrollDown();  /* 已收藏的 */
            Thread.sleep(200);
            pressEnter();

            Thread.sleep(timeUtilJobsLoaded);  /* 等待差事加载 */

            for (int i = 0; i < 5; i++) mouseScrollDown();  /* 夺取 */
            Thread.sleep(200);
            pressEnter();
            Thread.sleep(200);
            pressEnter();
            Thread.sleep(200);
            pressEnter();  /* 确认进行该差事 */
        } catch (InterruptedException ignored) {
        }
    }

    private void mouseScrollDown() throws InterruptedException {
        robot.mouseWheel(mouseScrollDownKey);
        Thread.sleep(mouseScrollInterval);
    }

    private void mouseScrollUp() throws InterruptedException {
        robot.mouseWheel(mouseScrollUpKey);
        Thread.sleep(mouseScrollInterval);
    }

    private void pressP() throws InterruptedException {
        robot.simulate(menuKey);
        awaitArrow();
    }

    private void pressRight() throws InterruptedException {
        robot.simulate(rightKey);
        awaitArrow();
    }

    private void pressEnter() throws InterruptedException {
        robot.simulate(enterKey);
        awaitEnter();
    }

    private void awaitArrow() throws InterruptedException {
        Thread.sleep(mouseScrollInterval);
    }

    private void awaitEnter() throws InterruptedException {
        Thread.sleep(enterKeyInterval);
    }

    private void awaitTimeUtilPMenuLoaded() throws InterruptedException {
        Thread.sleep(timeUtilPMenuLoaded);
    }
}
