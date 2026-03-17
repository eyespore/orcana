package cc.pineclone.automation.action.robot;

import cc.pineclone.automation.input.Key;

/**
 * Robot适配器，兼容原RobotAPI，提供对{@link Key}的适配能力
 */
public interface RobotAdapter {

    void simulate(Key key) throws InterruptedException;

    void keyPress(int keyCode);

    void keyRelease(int keyCode);

    void mousePress(int button);

    void mouseRelease(int button);

    void mouseWheel(int wheelAmt);

}
