package cc.pineclone.automation.action.impl.betterpmenu;

import cc.pineclone.automation.common.SessionType;
import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.KeyCode;
import cc.pineclone.automation.input.MouseWheelScroll;

/* 快速点火 */
public class JoinANewSessionAction extends Action {

    private final long mouseScrollInterval;
    private final long enterKeyInterval;
    private final long timeUtilPMenuLoaded;

    private final SessionType sessionType;

    private final VCRobotAdapter robot;

    public static final String ACTION_ID = "better-p-menu";

    private final Key enterKey = new Key(KeyCode.ENTER);
    private final Key rightKey = new Key(KeyCode.RIGHT);

    private final Key mouseScrollUp = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.UP, 1));
    private final Key mouseScrollDown = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN, 1));

    private final Key menuKey = new Key(KeyCode.P);  /* P 菜单按键 */

    public JoinANewSessionAction(
            SessionType sessionType,
            long mouseScrollInterval,
            long enterKeyInterval,
            long timeUtilPMenuLoaded
    ) {
        super(ACTION_ID);

        this.sessionType = sessionType;  /* 加入战局类型 */
        this.mouseScrollInterval = mouseScrollInterval;
        this.enterKeyInterval = enterKeyInterval;
        this.timeUtilPMenuLoaded = timeUtilPMenuLoaded;
        this.robot = RobotFactory.getRobot();
    }

    @Override
    public void activate(MacroEvent event) {
        try {
            pressP();
            Thread.sleep(200);
            pressRight();
            awaitTimeUtilPMenuLoaded();  /* 等待列表加载 */

            pressEnter();
            mouseScrollUp();
            mouseScrollUp();
            Thread.sleep(700);
            mouseScrollUp();
            Thread.sleep(700);
            mouseScrollUp();
            mouseScrollUp();

            Thread.sleep(200);
            pressEnter();

            int times;
            switch (sessionType) {
                case INVITE_ONLY_SESSION -> times = 1;
                case CREW_SESSION -> times = 2;
                case INVITE_ONLY_CREW_SESSION -> times = 3;
                case INVITE_ONLY_FRIENDS_SESSION -> times = 4;
                default -> times = 0;
            }

            for (int i = 0; i < times; i++) mouseScrollDown();
//
            Thread.sleep(200);
            pressEnter();
            Thread.sleep(200);
            pressEnter();
        } catch (InterruptedException ignored) {
        }
    }

    private void mouseScrollDown() throws InterruptedException {
        robot.mouseWheel(mouseScrollDown);
        Thread.sleep(mouseScrollInterval);
    }

    private void mouseScrollUp() throws InterruptedException {
        robot.mouseWheel(mouseScrollUp);
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
