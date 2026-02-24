package cc.pineclone.workflow.api.action.command;

public record CommandAcceptanceResult(Status status, String message) {
    public enum Status {
        ACCEPTED,  /* 命令得到受理 */
        REJECTED,  /* 拒绝受理该命令 */
        UNSUPPORTED,  /* 命令不被支持 */
        FAILED,  /* 处理命令时遇到异常 */
        IGNORED,  /* 此次命令提交将会被忽略 */
        AWAITING  /* 等待 */
    }
}
