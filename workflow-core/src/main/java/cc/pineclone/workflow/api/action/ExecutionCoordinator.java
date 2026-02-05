package cc.pineclone.workflow.api.action;

/* 执行器协调者 */
public interface ExecutionCoordinator {

    /* 决定是否可以运行当前的执行器 */
    void decide(ActionFacade handler);

    void onCompleted(ActionFacade handler);

}
