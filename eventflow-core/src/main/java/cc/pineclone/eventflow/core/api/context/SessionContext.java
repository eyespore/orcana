package cc.pineclone.eventflow.core.api.context;

@Deprecated
public interface SessionContext extends ExecutionContext {

    @Override
    default ContextScope scope() {
        return ContextScope.SESSION;
    }

}
