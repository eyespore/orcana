package cc.pineclone.eventflow.runtime.impl.action.ctx;

import cc.pineclone.eventflow.core.api.context.SessionContext;

final class DefaultSessionContext extends MapBackedExecutionContext implements SessionContext {
    // relies on SessionContext.scope() default
}
