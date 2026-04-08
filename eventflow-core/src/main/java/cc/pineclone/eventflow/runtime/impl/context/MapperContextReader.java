package cc.pineclone.eventflow.runtime.impl.context;

import cc.pineclone.eventflow.core.api.context.ContextReader;
import cc.pineclone.eventflow.common.api.value.ValueBinding;
import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.runtime.api.session.GlobalSession;
import cc.pineclone.eventflow.runtime.api.session.PathValueAccessor;
import cc.pineclone.eventflow.runtime.api.session.RuntimeSession;

import java.util.Objects;
import java.util.Optional;

@Deprecated
public class MapperContextReader implements ContextReader {

    private final Event event;
    private final RuntimeSession runtimeSession;
    private final GlobalSession globalSession;
    private final PathValueAccessor accessor;

    public MapperContextReader(Event event, RuntimeSession runtimeSession, GlobalSession globalSession, PathValueAccessor accessor) {
        this.event = Objects.requireNonNull(event, "event");
        this.runtimeSession = Objects.requireNonNull(runtimeSession, "runtimeSession");
        this.globalSession = Objects.requireNonNull(globalSession, "globalSession");
        this.accessor = Objects.requireNonNull(accessor, "accessor");
    }

    @Override
    public Object read(ValueBinding ref) {
        return switch (ref.scope()) {
            case EVENT_PAYLOAD -> accessor.get(event.payload(), ref.path());
            case RUNTIME -> accessor.get(runtimeSession.vars(), ref.path());
            case GLOBAL -> accessor.get(globalSession.vars(), ref.path());
            case COMMAND_PARAMS -> Optional.empty();
        };
    }
}
