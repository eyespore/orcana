package cc.pineclone.eventflow.runtime.impl.context;

import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.context.ContextReader;
import cc.pineclone.eventflow.common.api.value.ValueBinding;
import cc.pineclone.eventflow.runtime.api.session.GlobalSession;
import cc.pineclone.eventflow.runtime.api.session.PathValueAccessor;
import cc.pineclone.eventflow.runtime.api.session.RuntimeSession;

import java.util.Objects;
import java.util.Optional;

@Deprecated
public final class ActionContextReader implements ContextReader {

    private final Command command;
    private final RuntimeSession runtimeSession;
    private final GlobalSession globalSession;
    private final PathValueAccessor accessor;

    public ActionContextReader(Command command, RuntimeSession runtimeSession, GlobalSession globalSession, PathValueAccessor accessor) {
        this.command = Objects.requireNonNull(command, "command");
        this.runtimeSession = Objects.requireNonNull(runtimeSession, "runtimeSession");
        this.globalSession = Objects.requireNonNull(globalSession, "globalSession");
        this.accessor = Objects.requireNonNull(accessor, "accessor");
    }

    @Override
    public Object read(ValueBinding ref) {
        return switch (ref.scope()) {
            case COMMAND_PARAMS -> accessor.get(command.params(), ref.path());
            case RUNTIME -> accessor.get(runtimeSession.vars(), ref.path());
            case GLOBAL -> accessor.get(globalSession.vars(), ref.path());
            case EVENT_PAYLOAD -> Optional.empty();
        };
    }
}
