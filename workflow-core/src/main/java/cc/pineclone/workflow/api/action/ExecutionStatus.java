package cc.pineclone.workflow.api.action;

public enum ExecutionStatus {

    READY,  /* 准备就绪 */
    RUNNING,  /* 执行中 */
    HALTED,  /* 执行阻塞 */
    COMPLETED,  /* 执行完成 */
    CANCELED,  /* 执行取消 */

}
