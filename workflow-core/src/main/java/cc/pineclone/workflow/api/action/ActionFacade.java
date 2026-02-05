package cc.pineclone.workflow.api.action;

/* 执行器句柄 */
public interface ActionFacade {

    /* 执行状态 */
    ExecutionStatus status();

    /* 执行器属性，描述当前执行器的调度属性，例如互斥、优先级等 */
    ExecutionProfile profile();

    void halt();  /* 阻塞当前执行 */

    void cont();  /* 继续执行 */

    void cancel();  /* 取消执行 */

    void setCompleted();  /* 将当前句柄标记为已完成 */

    void addCompletedCallback(Runnable callback);  /* 完成时回调 */

}
