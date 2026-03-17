package cc.pineclone.automation.action.impl;

import cc.pineclone.automation.Macro;
import cc.pineclone.automation.AutomationContext;
import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.ScheduledAction;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.KeyCode;
import cc.pineclone.automation.input.MouseWheelScroll;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DelayClimbAction extends Action {

    private final VCRobotAdapter robot;
    private final Key usePhoneKey;
    private final Key hideInCoverKey;

    private final Key mouseWheelScrollDown = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN, 1));

    private final long timeUtilCameraExited;
    private final long timeUtilCameraLoaded1;
    private final long timeUtilCameraLoaded2;

    protected static final String ACTION_ID = "delay-climb";

    private volatile boolean shouldStopPhase1 = false;  /* 是否应该停止Phase1 */
    private final AtomicBoolean isPhase1Running = new AtomicBoolean(false);  /* 阶段一是否正在运行 */
    private final AtomicBoolean isPhase2Running = new AtomicBoolean(false);  /* 阶段二是否正在运行 */

    private volatile ScheduledFuture<?> future = null;

    private final Action cameraAction;

    private final boolean hideInCoverOnExit;  /* 是否在结束时自动躲入掩体 */

    /**
     * 子动作，用于实现延迟攀阶段二
     */
    private class CameraAction extends ScheduledAction {
        public CameraAction(String macroId, long interval) {
            super(macroId, interval, 200);
        }

        @Override
        public void schedule(MacroEvent event) {
            try {
                /* 进入CameraAction时相机应当处关闭状态，因此首先启用相机 */
                enterCamera();
                awaitTimeUtilCameraLoaded2();
                /* 等待相机加载完毕后关闭相机 */

                exitCamera();  /* 退出相机 */
                awaitTimeUtilCameraExited();
            } catch (InterruptedException ignored) {
                /* 利用中断打断循环状态 */
//                Logger.lowLevelDebug("interrupted exception occur, exit running phase2");
            }
        }

        @Override
        public void afterDeactivate(MacroEvent event) {
            /* 检查是否启用了退出时自动寻找掩体 */
            if (event.getMacroContext().getExecutionStatus().equals(Macro.MacroExecutionStatus.SUSPENDED)) return;

            if (DelayClimbAction.this.hideInCoverOnExit) {
                try {
                    DelayClimbAction.this.pressHideInCoverKey();
                } catch (InterruptedException ignored) {
                }
            }

            /* 主循环退出，将阶段二运行状态置为false */
//            Logger.lowLevelDebug("set is phase2running to false");
            DelayClimbAction.this.isPhase2Running.set(false);
        }
    }

    /**
     * 延迟攀爬宏，宏的执行逻辑分为两个阶段：
     * <li> 阶段1：通过躲进掩体键位让小哑巴进入掩体，同时连续快速点击两次使用手机按键掏出相机；等待一段延时(stage1)，确保相机被自动收起
     * 此时点击前进方向键和空格触发攀爬动作，等待微小延迟确定攀爬生效后，二次掏出相机，此时小哑巴处于站立状态；随后取消相机，点击
     * 两次右方向键，确保手机应用处于相机处
     *
     * <li> 阶段2：进入循环，点击鼠标左键进入相机，点击鼠标右键退出相机，退出相机后等待一段时间（约1.5s供小哑巴移动），然后再次点击
     * 左键进入相机，保持该循环，直到用户点击“停止延迟攀爬宏”，此处使用子宏来实现阶段2
     *
     * @param usePhoneKey 使用手机按键
     * @param hideInCoverKey 躲入掩体键位
     * @param triggerInterval 每次关闭相机-重新打开相机循环间隔，决定了延迟攀是否失败和小哑巴能够自由移动的时间
     *
     * @param timeUtilCameraExited 在阶段1当中等待相机退出的时间
     * @param timeUtilCameraLoaded1 在阶段2当中等待相机退出的时间
     */
    public DelayClimbAction(Key usePhoneKey,
                            Key hideInCoverKey,
                            long triggerInterval,
                            long timeUtilCameraExited,
                            long timeUtilCameraLoaded1,
                            long timeUtilCameraLoaded2,
                            boolean hideInCoverOnExit) {
        super(ACTION_ID);
        this.usePhoneKey = usePhoneKey;
        this.hideInCoverKey = hideInCoverKey;
        this.timeUtilCameraExited = timeUtilCameraExited;
        this.timeUtilCameraLoaded1 = timeUtilCameraLoaded1;
        this.timeUtilCameraLoaded2 = timeUtilCameraLoaded2;

        this.hideInCoverOnExit = hideInCoverOnExit;

        /* 子动作，通过传入相同的ACTION_ID来获取同一个robot对象 */
        this.cameraAction = new CameraAction(ACTION_ID, triggerInterval);

        robot = RobotFactory.getRobot(ACTION_ID);
    }

    /* 在进入循环之前的逻辑，该方法被用于执行宏的阶段1 */
    @Override
    public void activate(MacroEvent event) {
        /* 检查阶段二是否正在运行 */
        if (stopPhase2IfRunning(event)) return;

        /* 当前循环未运行，检查阶段一是否正在运行 */
        if (stopPhase1IfRunning()) return;

        /* 当前循环未运行，且阶段一未执行，执行阶段一 */
        startPhase1(event);
    }

    private boolean stopPhase1IfRunning() {
        /* 当前循环未运行，检查阶段一是否正在运行 */
        if (isPhase1Running.compareAndSet(true, false)) {
            /* 阶段一正在运行，停止阶段一 */
            shouldStopPhase1 = true;

            /* 中断运行中的任务 */
            if (future != null) {
                future.cancel(true);
                future = null;
            }
            return true;
        }
        return false;
    }

    private boolean stopPhase2IfRunning(MacroEvent event) {
        /* 检查阶段二是否正在运行 */
        if (isPhase2Running.get()) {
            /* 当前循环已经运行，停止循环 */
            this.cameraAction.doDeactivate(event);  /* 此处应当调用 doDeactivate 而不是 deactivate，后者在生命周期不会调用afterDeactivate */
            return true;
        }
        return false;
    }

    private void startPhase1(MacroEvent event) {
        /* 阶段一未运行，提交运行任务 */
        isPhase1Running.set(true);
        future = AutomationContext.getInstance().getScheduler().schedule(() -> {
            try {
                /* 当前循环未运行，执行启动逻辑 */
                /* 进入掩体并打开相机 */
                pressHideInCoverKey();
                setupCamera();  /* 相机会自动消失 */
                awaitTimeUtilCameraLoaded1();  /* 此处为第一次打开相机，因此等待时间应当较长一些 */

                /* 按住 W 键位并点击空格 */
                holdWAndPressSpace();
                setupCamera();  /* 掏出手机并打开相机 */
                awaitTimeUtilCameraLoaded2();  /* 此后打开相机使用延迟2 */

                exitCamera();  /* 点击右键关闭相机并进入循环 */
                awaitTimeUtilCameraExited();
                selectCamera();  /* 选择相机 */

                if (shouldStopPhase1) return;
                cameraAction.activate(event);
                isPhase2Running.set(true);

            } catch (InterruptedException ignored) {
                /* 利用中断打断阶段一执行 */
            } finally {
                shouldStopPhase1 = false;
                isPhase1Running.set(false);
            }
        }, 0, TimeUnit.SECONDS);
    }

    @Override
    public void deactivate(MacroEvent event) {
        /* 由于延迟攀Action采用子动作，而不是子宏，子动作并不会被纳入MacroRegistry中得到挂起信号，因此
        *  需要由父动作管理，当挂起时deactivate会被调用，此时由父动作停止子动作 */
        if (stopPhase2IfRunning(event)) return;
        stopPhase1IfRunning();
    }

    private void holdWAndPressSpace() throws InterruptedException {
        try {
            robot.keyPress(new Key(KeyCode.W));
            Thread.sleep(30);
            robot.simulate(new Key(KeyCode.SPACE));
            Thread.sleep(30);
        } finally {
            robot.keyRelease(new Key(KeyCode.W));
            Thread.sleep(30);
        }
    }

    private void selectCamera() throws InterruptedException {
//        robot.simulate(new Key(KeyCode.LEFT));
        robot.simulate(mouseWheelScrollDown);
        Thread.sleep(50);
//        robot.simulate(new Key(KeyCode.DOWN));
        robot.simulate(mouseWheelScrollDown);
        Thread.sleep(50);
    }

    private void pressHideInCoverKey() throws InterruptedException {
        robot.simulate(this.hideInCoverKey);
        Thread.sleep(30);
    }

    /* 连续点击两次使用手机键来打开相机 */
    private void setupCamera() throws InterruptedException {
        robot.simulate(this.usePhoneKey);
        Thread.sleep(30);
        robot.simulate(this.usePhoneKey);
        Thread.sleep(30);
    }

    private void exitCamera() throws InterruptedException {
        robot.simulate(new Key(KeyCode.ESCAPE));
        Thread.sleep(30);
    }

    private void enterCamera() throws InterruptedException {
        robot.simulate(new Key(KeyCode.ENTER));
        Thread.sleep(30);
    }

    private void awaitTimeUtilCameraExited() throws InterruptedException {
        Thread.sleep(timeUtilCameraExited);
    }

    private void awaitTimeUtilCameraLoaded1() throws InterruptedException {
        Thread.sleep(timeUtilCameraLoaded1);
    }

    private void awaitTimeUtilCameraLoaded2() throws InterruptedException {
        Thread.sleep(timeUtilCameraLoaded2);
    }
}
