package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.automation.action.Action;
import cc.pineclone.eventflow.core.api.command.Command;

import java.util.List;

@Deprecated
public interface CommandResolver {

    List<Action> resolve(Command command);

}
