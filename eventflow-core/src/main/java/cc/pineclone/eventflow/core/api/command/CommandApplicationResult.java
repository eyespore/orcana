package cc.pineclone.eventflow.core.api.command;

@Deprecated
public record CommandApplicationResult(Status status, String message) {
    public enum Status {
        SUCCEEDED,  /* 命令执行成功 */
        FAILED,  /* 命令执行失败 */
        CANCEL  /* 命令被中途取消 */
    }
}
