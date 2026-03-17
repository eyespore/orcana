package cc.pineclone.eventflow.runtime.impl.action.ctx;

import cc.pineclone.eventflow.core.api.context.FlowSession;

public record DefaultFlowSession(
        GlobalContext global,
        SessionContext session,
        CallContext call
) implements FlowSession {
}
