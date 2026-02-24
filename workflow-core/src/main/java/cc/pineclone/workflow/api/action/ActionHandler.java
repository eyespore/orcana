package cc.pineclone.workflow.api.action;

import cc.pineclone.workflow.api.action.command.ActionCommand;
import cc.pineclone.workflow.api.action.command.CommandAcceptanceResult;
import cc.pineclone.workflow.api.action.command.CommandApplicationResult;
import cc.pineclone.workflow.api.action.ctx.ActionContext;

import java.util.concurrent.CompletionStage;

public interface ActionHandler {

    /* 处理命令 */
    CommandHandlingReceipt handleCoordinateCommand(ActionContext ctx, ActionCommand.CoordinateCommand command);

    /* Action 执行结果 */
    CompletionStage<ActionExecutionOutcome> actionExecutionOutcome();

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
