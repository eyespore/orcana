package cc.pineclone.automation;

import cc.pineclone.automation.trigger.Trigger;
import cc.pineclone.automation.trigger.TriggerEvent;
import cc.pineclone.automation.trigger.TriggerListener;
import cc.pineclone.automation.workflow.Workflow;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public abstract class AutomationJob implements TriggerListener {

    private final UUID jobId = UUID.randomUUID();  /* 自动化 ID */

    protected final Trigger trigger;
//    protected final Action action;
    protected final Workflow workflow;

    private JobState state;
    @Getter private volatile JobExecutionStatus executionStatus;  /* 执行状态 */

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 当全局挂起，宏被从Trigger监听器中移除，此时一旦用户通过UI关闭任何一个宏功能，那么onMacroTerminate被调用，
     * Trigger发现监听器为空，直接被关闭，随后唤醒宏的时候，Trigger就已经全部失效了，因此除非宏被显式关闭，否则不要将其从监听器中移除
     * 为了避免这种情况，应该仅仅在Automation的terminate方法被调用时，即宏的生命周期结束时，将宏从Trigger的监听器列表移除，避免监听器的错误判断
     */
    @Override
    public final void onTriggerEvent(TriggerEvent event) {
        if (executionStatus.equals(JobExecutionStatus.SUSPENDED)) return;  /* 当前宏处于挂起状态，不执行任何业务逻辑 */
        if (!getStatus().equals(AutomationStatus.RUNNING)) return;  /* 仅在 RUNNING 状态转发事件 */
    }

    protected abstract void handleTriggerEvent(TriggerEvent event);

    public enum AutomationStatus {
        CREATED,  /* 宏在被创建之后，会处于CREATED状态，通过调用launch()使其切入RUNNING状态，CREATED状态支持挂起 */
        RUNNING,   /* 调用terminate()会切入TERMINATED状态*/
        TERMINATED  /* 宏生命周期结束，使用需要重新创建宏 */
    }

    public enum JobExecutionStatus {
        ACTIVE, SUSPENDED
    }

    private interface JobState {
        default void launch(AutomationJob context) {  /* 启动 */
            throw new UnsupportedOperationException("Automation with status " + getStatus() + " is not allowed to be launched");
        }

        default void terminate(AutomationJob context) {  /* 停止 */
            throw new UnsupportedOperationException("Automation with status " + getStatus() + " is not allowed to be terminated");
        }

        AutomationStatus getStatus();
    }
    
    /* 初始创建状态，该状态支持被挂起 */
    private static class CreatedState implements JobState {
        @Override
        public void launch(AutomationJob context) {
            context.state = new RunningState();
            context.trigger.addListener(context);  /* 将宏加入Trigger的监听列表，桥接开始 */
            AutomationJobEvent event = new AutomationJobEvent(context.jobId, context.getStatus(), context.executionStatus);
            context.trigger.onAutomationJobEvent(event);
            context.workflow.onAutomationJobEvent(event);
//            context.action.onAutomationLaunch(AutomationEvent.of(null, context));  /* 告知生命周期 */
        }

        @Override
        public AutomationStatus getStatus() {
            return AutomationStatus.CREATED;
        }
    }
    
    /* 正在执行 */
    private static class RunningState implements JobState {
        @Override
        public void terminate(AutomationJob context) {
            context.state = new TerminatedState();
            context.trigger.removeListener(context);  /* 将宏从 Trigger 监听器列表注销，桥接结束 */
//            context.action.onAutomationTerminate(AutomationEvent.of(null, context));
            AutomationJobEvent event = new AutomationJobEvent(context.jobId, context.getStatus(), context.executionStatus);

            context.workflow.onAutomationJobEvent(event);
            context.trigger.onAutomationJobEvent(event);  /* 注销执行器 */

        }

        @Override
        public AutomationStatus getStatus() {
            return AutomationStatus.RUNNING;
        }
    }
    
    /* 被关闭状态 */
    private static class TerminatedState implements JobState {
        @Override
        public AutomationStatus getStatus() {
            return AutomationStatus.TERMINATED;
        }
    }

    public AutomationJob(final Trigger trigger, final Workflow workflow) {
        this.trigger = trigger;
        this.workflow = workflow;
        this.state = new CreatedState();
        this.executionStatus = JobExecutionStatus.ACTIVE;
    }

    public void launch() {  /* 启用宏，例如注册监听器等 */
        this.state.launch(this);
    }

    /**
     * 停止宏，例如注销监听器等，需要注意的是，一旦某个宏被终止，那么它将不可以再被恢复
     */
    public void terminate() {
        this.state.terminate(this);
    }

    /**
     * 挂起Automation，此时会中断Trigger的事件输入，实现挂起的效果
     */
    public void suspend() {
        if (getStatus().equals(AutomationStatus.TERMINATED)) return;  /* 宏生命周期结束，不支持挂起 */
        if (executionStatus.equals(JobExecutionStatus.SUSPENDED)) return;  /* 宏当前已经处于挂起状态 */
        this.executionStatus = JobExecutionStatus.SUSPENDED;
        this.workflow.onAutomationJobEvent(createEvent());
        this.trigger.onAutomationJobEvent(createEvent());
    }

    /**
     * 恢复Automation，会恢复Trigger的事件输入，从而恢复Workflow的执行
     */
    public void resume() {
        if (getStatus().equals(AutomationStatus.TERMINATED)) return;  /* 宏生命周期已经结束，不支持恢复 */
        if (executionStatus == JobExecutionStatus.ACTIVE) return;  /* 宏当前已经处于唤醒状态 */
        this.executionStatus = JobExecutionStatus.ACTIVE;
        this.workflow.onAutomationJobEvent(createEvent());
        this.trigger.onAutomationJobEvent(createEvent());
    }

    public AutomationStatus getStatus() {
        return state.getStatus();
    }

    private AutomationJobEvent createEvent() {
        return new AutomationJobEvent(jobId, getStatus(), executionStatus);
    }
}
