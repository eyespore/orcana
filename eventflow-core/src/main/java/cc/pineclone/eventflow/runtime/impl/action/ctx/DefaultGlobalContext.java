package cc.pineclone.eventflow.runtime.impl.action.ctx;

import cc.pineclone.eventflow.core.api.context.GlobalContext;

final class DefaultGlobalContext extends MapBackedExecutionContext implements GlobalContext {
    // relies on GlobalContext.scope() default
}
