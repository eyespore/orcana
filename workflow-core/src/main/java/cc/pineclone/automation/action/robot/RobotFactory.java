package cc.pineclone.automation.action.robot;

import cc.pineclone.automation.input.Key;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RobotFactory {

    public static final String COMMON_ROBOT = "common-robot";

    private static final Map<String, VCRobotAdapter> compositeRobotAdapters = new HashMap<>();

    public static VCRobotAdapter getRobot() {
        return getRobot(COMMON_ROBOT);
    }

    // 推荐使用这个方法获得robot，以提高执行的上线
    // 通过传入的key与robot建立映射关系，如果key已经存在，那么就会返回先前已经创建的robot
    // 这是为了一些宏当中可能包含“子宏”考虑的
    public static VCRobotAdapter getRobot(String key) {
        return compositeRobotAdapters.computeIfAbsent(key, k -> {
            try {
                return new CompositeRobotAdapter(new Robot());
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        });
    }



    /* 基于VCRobotAdapter实现的机器人适配器，主要是提供了simulate的实现 */
    /* Composite意为“组合”，CompositeRobotAdapter可以直接传入滚轮、键盘按键、鼠标案件，减少编码难度 */
    private static class CompositeRobotAdapter extends VCRobotAdapter {
        public CompositeRobotAdapter(Robot robot) {
            super(robot);
        }

        @Override
        public void simulate(Key key) throws InterruptedException {
            this.simulate(key, 20);
        }

        @Override
        public void simulate(Key key, long delay) throws InterruptedException {
            if (key.key != null) {
                /* 执行按键 */
                try {
                    keyPress(key);
                    Thread.sleep(delay);
                } finally {
                    keyRelease(key);
                }
            } else if (key.button != null) {
                /* 执行鼠标 */
                try {
                    mousePress(key);
                    Thread.sleep(delay);
                } finally {
                    mouseRelease(key);
                }
            } else if (key.scroll != null) {
                /* 执行滚轮 */
                mouseWheel(key);
            }
        }
    }
}
