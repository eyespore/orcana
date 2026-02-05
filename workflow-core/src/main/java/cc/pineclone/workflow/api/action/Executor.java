package cc.pineclone.workflow.api.action;

public interface Executor {

    /* 控制反转 */
    ActionFacade execute(ExecutionContext context);

}
