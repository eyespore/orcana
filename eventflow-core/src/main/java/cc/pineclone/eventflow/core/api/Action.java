package cc.pineclone.eventflow.core.api;

import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.context.ActionContext;

public interface Action extends CoreComponent {

    void execute(Command command, ActionContext context);

}

