package cc.pineclone.eventflow.runtime.api.selector;

import cc.pineclone.eventflow.core.api.command.Command;

public interface CommandSelector {

    boolean matches(Command command);

}
