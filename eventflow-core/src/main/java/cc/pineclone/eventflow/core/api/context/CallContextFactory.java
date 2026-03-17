package cc.pineclone.eventflow.core.api.context;

import cc.pineclone.eventflow.core.api.action.ActionIdentity;
import cc.pineclone.eventflow.core.api.command.Command;

import java.time.Instant;
import java.util.UUID;

@Deprecated
public interface CallContextFactory {

    /**
     * 每次 submit 都会创建一个 CallContext（可以包含 command + args + tracing 等）。
     */
    CallContext createContext(
            ActionIdentity actionIdentity,
            UUID sessionId,
            Command command,
            Instant submittedAt
    );

}
