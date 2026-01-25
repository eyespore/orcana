package cc.pineclone.automation;

import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.trigger.Trigger;
import cc.pineclone.automation.trigger.TriggerEvent;
import cc.pineclone.automation.trigger.TriggerListener;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 抽象宏 */
public abstract class Macro implements TriggerListener {

    protected final Trigger trigger;
    protected final Action action;

    private MacroState state;
    @Getter private volatile MacroExecutionStatus executionStatus;  /* 执行状态 */

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 当全局挂起，宏被从Trigger监听器中移除，此时一旦用户通过UI关闭任何一个宏功能，那么onMacroTerminate被调用，
     * Trigger发现监听器为空，直接被关闭，随后唤醒宏的时候，Trigger就已经全部失效了，因此除非宏被显式关闭，否则不要将其从监听器中移除
     * 为了避免这种情况，应该仅仅在Macro的terminate方法被调用时，即宏的生命周期结束时，将宏从Trigger的监听器列表移除，避免监听器的错误判断
     */
    @Override
    public final void onTriggerEvent(TriggerEvent event) {
        if (executionStatus.equals(MacroExecutionStatus.SUSPENDED)) return;  /* 当前宏处于挂起状态，不执行任何业务逻辑 */
        handleTriggerEvent(event);
    }

    protected abstract void handleTriggerEvent(TriggerEvent event);

    public enum MacroStatus {
        CREATED,  /* 宏在被创建之后，会处于CREATED状态，通过调用launch()使其切入RUNNING状态，CREATED状态支持挂起 */
        RUNNING,   /* 调用terminate()会切入TERMINATED状态*/
        TERMINATED  /* 宏生命周期结束，使用需要重新创建宏 */
    }

    public enum MacroExecutionStatus {
        ACTIVE, SUSPENDED
    }

    public interface MacroState {
        default void launch(Macro context) {}  /* 启动 */
        default void terminate(Macro context) {}  /* 停止 */
        MacroStatus getStatus();
    }
    
    /* 初始创建状态，该状态支持被挂起 */
    private static class CreatedState implements MacroState {
        @Override
        public void launch(Macro context) {
            context.state = new RunningState();
            context.trigger.addListener(context);  /* 将宏加入Trigger的监听列表，桥接开始 */
            context.trigger.onMacroLaunch(MacroEvent.of(null, context));
            context.action.onMacroLaunch(MacroEvent.of(null, context));  /* 告知生命周期 */
        }

        @Override
        public MacroStatus getStatus() {
            return MacroStatus.CREATED;
        }
    }
    
    /* 正在执行 */
    private static class RunningState implements MacroState {
        @Override
        public void terminate(Macro context) {
            context.state = new TerminatedState();
            context.trigger.removeListener(context);  /* 将宏从 Trigger 监听器列表注销，桥接结束 */
            context.action.onMacroTerminate(MacroEvent.of(null, context));
            context.trigger.onMacroTerminate(MacroEvent.of(null, context));  /* 注销执行器 */
        }

        @Override
        public MacroStatus getStatus() {
            return MacroStatus.RUNNING;
        }
    }
    
    /* 被关闭状态 */
    private static class TerminatedState implements MacroState {
        @Override
        public MacroStatus getStatus() {
            return MacroStatus.TERMINATED;
        }
    }

    public Macro(final Trigger trigger, final Action action) {
        this.trigger = trigger;
        this.action = action;
        this.state = new CreatedState();
        this.executionStatus = MacroExecutionStatus.ACTIVE;
    }

    /* 可以通过重复调用launch 和 terminate来灵活控制宏的加载和卸载，而不是调用GlobalScreen.registerNativeHook以及unregisterNativeHook */

    public void launch() {  /* 启用宏，例如注册监听器等 */
        this.state.launch(this);
    }

    /**
     * 停止宏，例如注销监听器等，需要注意的是，一旦某个宏被终止，那么它将不可以再被恢复
     */
    public void terminate() {  /*  */
        this.state.terminate(this);
    }

    /**
     * 挂起宏，此时会中断Trigger的事件输入，实现挂起的效果
     */
    public void suspend() {
        if (getStatus().equals(MacroStatus.TERMINATED)) return;  /* 宏生命周期结束，不支持挂起 */
        if (executionStatus.equals(MacroExecutionStatus.SUSPENDED)) return;  /* 宏当前已经处于挂起状态 */
        this.executionStatus = MacroExecutionStatus.SUSPENDED;
        this.action.onMacroSuspend(MacroEvent.of(null, this));
        this.trigger.onMacroSuspend(MacroEvent.of(null, this));
    }

    /**
     * 恢复宏，会恢复Trigger的事件输入，从而恢复Action的执行
     */
    public void resume() {
        if (getStatus().equals(MacroStatus.TERMINATED)) return;  /* 宏生命周期已经结束，不支持恢复 */
        if (executionStatus == MacroExecutionStatus.ACTIVE) return;  /* 宏当前已经处于唤醒状态 */
        this.executionStatus = MacroExecutionStatus.ACTIVE;
        this.action.onMacroResume(MacroEvent.of(null, this));
        this.trigger.onMacroResume(MacroEvent.of(null, this));
    }

    public MacroStatus getStatus() {
        return state.getStatus();
    }

    @Override
    public String toString() {
        return "Macro{" +
                "action=" + action +
                ", trigger=" + trigger +
                '}';
    }
}
