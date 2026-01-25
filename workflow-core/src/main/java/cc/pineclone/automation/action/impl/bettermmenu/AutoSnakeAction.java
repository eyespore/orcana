package cc.pineclone.automation.action.impl.bettermmenu;

import cc.pineclone.automation.Macro;
import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.ScheduledAction;
import cc.pineclone.automation.input.Key;

import java.util.concurrent.atomic.AtomicBoolean;

/* 自动 M 菜单回血 */
public class AutoSnakeAction extends BetterMMenuAction {

    private final AtomicBoolean isLoopRunning = new AtomicBoolean(false);  /* 阶段二是否正在运行 */
    private final Action snakeAction;  /* 子动作 */
    private final boolean keepMMenu;  /* 是否在退出时保留 M 菜单 */
    private final boolean refillVest;  /* 是否在激活时穿防弹衣 */

    /**
     * 子动作，用于实现 M 菜单阶段二
     */
    private class SnakeAction extends ScheduledAction {
        public SnakeAction(String actionId, long interval) {
            /* 此处的50ms延时是为了适配单点仅触发零食列表而不使用零食的场景 */
            /* 若将初始延迟设置为0，则会导致循环启动过快，即使点按激活热键，仍然会使用一份零食 */
            super(actionId, interval, 50);
        }

        @Override
        public void schedule(MacroEvent event) {
            try {
                pressEnter();  /* 点击回车键补零食 */
            } catch (InterruptedException ignored) {
                /* 利用中断打断循环状态 */
//                Logger.lowLevelDebug("interrupted exception occur, exit running phase2");
            }
        }

        @Override
        public void afterDeactivate(MacroEvent event) {
            /* 检查是否启用了退出时自动寻找掩体 */
            /* 主循环退出，将阶段二运行状态置为false */
//            Logger.lowLevelDebug("set is phase2running to false");
            AutoSnakeAction.this.isLoopRunning.set(false);
        }
    }

    /**
     * M菜单自动回血宏：
     * <li> 阶段1：启用M菜单并进入零食，该阶段被设计为不可中断，确保进入零食页面
     *
     * <li> 阶段2：循环点击回车
     */
    public AutoSnakeAction(Key menukey,
                           long mouseScrollInterval,
                           long enterKeyInterval,
                           long timeUtilMMenuLoaded,
                           boolean refillVest,
                           boolean keepMMenu) {

        super(menukey, mouseScrollInterval, enterKeyInterval, timeUtilMMenuLoaded);
        /* 子动作，通过传入相同的ACTION_ID来获取同一个robot对象 */
        this.snakeAction = new SnakeAction(ACTION_ID, enterKeyInterval);
        this.keepMMenu = keepMMenu;
        this.refillVest = refillVest;
    }

    /* 在进入循环之前的逻辑，该方法被用于执行宏的阶段1 */
    @Override
    public void activate(MacroEvent event) {
        /* 检查阶段二是否正在运行，若阶段二正在运行，则先停止阶段二，然后执行阶段一 */
//        Logger.lowLevelDebug("Auto snake activates");
        stopPhase2IfRunning(event);

        /* 当前循环未运行，且阶段一未执行，执行阶段一 */
        startPhase1(event);
    }

    @Override
    public void deactivate(MacroEvent event) {
        /* 由于延迟攀Action采用子动作，而不是子宏，子动作并不会被纳入MacroRegistry中得到挂起信号，因此
         *  需要由父动作管理，当挂起时doDeactivate会被调用，链式调用deactivate与afterDeactivate，完成后处理 */
        stopPhase2IfRunning(event);

        if (event.getMacroContext().getExecutionStatus().equals(Macro.MacroExecutionStatus.ACTIVE) && !keepMMenu) {  /* 在非进程挂起时，检查是否保留M菜单，决定是否将M菜单隐藏 */
            try {
                pressMenuKey();  /* 不保留M菜单，将菜单隐藏 */
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void stopPhase2IfRunning(MacroEvent event) {
        if (isLoopRunning.get()) {
            /* 当前循环已经运行，停止循环 */
            /* 此处应当调用 doDeactivate 而不是 deactivate，后者在生命周期不会调用afterDeactivate */
            this.snakeAction.doDeactivate(event);
        }
    }

    private void startPhase1(MacroEvent event) {
        /* 当前循环未运行，执行启动逻辑 */
        try {
            pressMenuKey();  /* 启动 M 菜单 */
            Thread.sleep(timeUtilMMenuLoaded);  /* 解决 M 键菜单出现过晚的问题 */

            for (int i = 0; i < 7; i++) mouseScrollUp();
            pressEnter();

            /* 似乎进入列表之后如果不等待会导致下拉列表触发失败，无法正确进入零食列表 */
            /* 因此此处给出100ms延时，确保能够正确进入零食列表 */
            Thread.sleep(100);
            mouseScrollDown();

            if (refillVest) {
//                Logger.lowLevelDebug("refill vest activate");

                /* 使用防弹衣 */
                pressEnter();
                for (int i = 0; i < 3; i++) mouseScrollUp();
                pressEnter();
                pressEscape();  /* 撤出防弹衣列表 */
            }

            mouseScrollDown();

            pressEnter();  /* 进入零食列表，步入循环 */

            snakeAction.activate(event);
            isLoopRunning.set(true);

//            Logger.lowLevelDebug("done");
        } catch (InterruptedException ignored) {
            /* 忽略中断而不执行任何操作，确保阶段一被完整执行 */
        }
    }
}
