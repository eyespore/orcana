package cc.pineclone.eventflow.runtime.impl.action.ctx;

import cc.pineclone.eventflow.core.api.context.FlowSession;
import cc.pineclone.eventflow.core.api.context.CallContext;
import cc.pineclone.eventflow.core.api.context.GlobalContext;
import cc.pineclone.eventflow.core.api.context.SessionContext;

public record DefaultFlowSession(
        GlobalContext global,
        SessionContext session,
        CallContext call
) implements FlowSession {
}
