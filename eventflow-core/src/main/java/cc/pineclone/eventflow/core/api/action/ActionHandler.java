package cc.pineclone.eventflow.core.api.action;

import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.command.CommandAcceptanceResult;
import cc.pineclone.eventflow.core.api.command.CommandApplicationResult;
import cc.pineclone.eventflow.core.api.context.FlowSession;

import java.util.concurrent.CompletionStage;

@Deprecated
public interface ActionHandler {

    /* 处理命令及时响应结果以及命令执行完成的结果 */
    CommandHandlingReceipt handle(FlowSession ctx, Command command);

    /* Action 执行结果 */
    CompletionStage<ActionExecutionOutcome> outcome();

    record CommandHandlingReceipt(
            CommandAcceptanceResult acceptance,
            CompletionStage<CommandApplicationResult> application) {
    }

    record ActionExecutionOutcome(ActionExecutionStatus status, String message) { }

    enum ActionExecutionStatus {
        COMPLETED,  /* Action 完整地被执行了 */
        CANCELED,   /* Action 被中途取消 */
        FAILED  /* Action 执行期间遇到异常 */
    }
}
