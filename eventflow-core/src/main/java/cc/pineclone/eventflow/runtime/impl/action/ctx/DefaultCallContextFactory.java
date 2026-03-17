package cc.pineclone.eventflow.runtime.impl.action.ctx;

import cc.pineclone.eventflow.core.api.action.ActionIdentity;
import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.context.CallContext;
import cc.pineclone.eventflow.core.api.context.CallContextFactory;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DefaultCallContextFactory implements CallContextFactory {

    @Override
    public CallContext createContext(ActionIdentity actionIdentity, UUID sessionId, Command command, Instant submittedAt) {
        Objects.requireNonNull(actionIdentity, "actionIdentity");
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(submittedAt, "submittedAt");
        Objects.requireNonNull(command, "command");

        DefaultCallContext ctx = new DefaultCallContext(command.command(), safeMeta(command.args()));
        ctx.put("action.domain", actionIdentity.domain());
        ctx.put("action.name", actionIdentity.name());
        ctx.put("session.id", sessionId);
        ctx.put("call.submittedAt", submittedAt);
        return ctx;
    }

    private Map<String, Object> safeMeta(Map<String, Object> args) {
        return args == null ? Map.of() : Map.copyOf(args);
    }
}
