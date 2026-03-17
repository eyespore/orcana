package cc.pineclone.eventflow.runtime.impl.action.ctx;

import cc.pineclone.eventflow.core.api.context.CallContext;

import java.util.Map;
import java.util.Objects;

final class DefaultCallContext extends MapBackedExecutionContext implements CallContext {

    private final String command;
    private final Map<String, Object> meta;

    DefaultCallContext(String command, Map<String, Object> meta) {
        this.command = Objects.requireNonNull(command, "command");
        this.meta = Objects.requireNonNull(meta, "meta");
    }

    @Override
    public String command() {
        return command;
    }

    @Override
    public Map<String, Object> meta() {
        return meta;
    }
}
