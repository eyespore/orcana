package cc.pineclone.automation.action.robot;

import cc.pineclone.automation.utils.KeyUtils;
import cc.pineclone.automation.input.Key;
import lombok.Getter;

import java.awt.*;

/* 基于VC键符工作的机器人实现，支持传入VK键符进行转换，会将VK键符映射到VC后执行 */
/* 考虑到java.awt.Robot是基于VC键符工作的，该类给java.awt.Robot和VFX之间提供了很好的桥接 */
@Getter
public class VCRobotAdapter implements RobotAdapter {

    protected final Robot robot;

    public VCRobotAdapter(Robot robot) {
        this.robot = robot;
    }

    @Override
    public void simulate(Key key) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void simulate(Key key, long delay) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void keyPress(Key key) {
        int vkCode = KeyUtils.toVKCode(key.key);
        robot.keyPress(vkCode);
    }

    public final void keyRelease(Key key) {
        int vkCode = KeyUtils.toVKCode(key.key);
        robot.keyRelease(vkCode);
    }

    public final void mousePress(Key key) {
        int mask = KeyUtils.toVKMouse(key.button);
        robot.mousePress(mask);
    }

    public final void mouseRelease(Key key) {
        int mask = KeyUtils.toVKMouse(key.button);
        robot.mouseRelease(mask);
    }

    public final void mouseWheel(Key key) {
        int mask = KeyUtils.toVCScroll(key.scroll);
        robot.mouseWheel(mask);
    }

    @Override
    public final void mousePress(int button) {
        robot.mousePress(button);
    }

    @Override
    public final void mouseRelease(int button) {
        robot.mouseRelease(button);
    }

    @Override
    public final void mouseWheel(int wheelAmt) {
        robot.mouseWheel(wheelAmt);
    }

    @Override
    public final void keyPress(int keyCode) {
        robot.keyPress(keyCode);
    }

    @Override
    public final void keyRelease(int keyCode) {
        robot.keyRelease(keyCode);
    }
}
