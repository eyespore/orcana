package cc.pineclone.eventflow.runtime.api.bundle;

import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.runtime.api.session.RuntimeSession;

public record CommandBundle(
        RuntimeSession runtimeSession,
        Command command
) implements RuntimeBundle {
}
