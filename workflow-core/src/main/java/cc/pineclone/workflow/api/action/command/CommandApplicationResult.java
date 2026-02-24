package cc.pineclone.workflow.api.action.command;

public record CommandApplicationResult(Status status, String message) {
    public enum Status {
        SUCCEEDED,  /* 命令执行成功 */
        FAILED  /* 命令执行失败 */
    }
}
