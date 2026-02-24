package cc.pineclone.workflow.api.action.ctx;

public interface SessionContext extends ExecutionContext {

    @Override
    default ContextScope scope() {
        return ContextScope.SESSION;
    }

}
