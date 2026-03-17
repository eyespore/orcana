package cc.pineclone.eventflow.core.api.context;

@Deprecated
public interface GlobalContext extends ExecutionContext {

    @Override
    default ContextScope scope() {
        return ContextScope.GLOBAL;
    }

}
