package cc.pineclone.workflow.api.action;

import cc.pineclone.workflow.api.action.command.ActionCommand;
import cc.pineclone.workflow.api.action.ctx.ActionContext;

public interface CustomCommandActionHandler extends ActionHandler {

    CommandHandlingReceipt handleCustomCommand(ActionContext ctx, ActionCommand.CustomCommand command);

}
