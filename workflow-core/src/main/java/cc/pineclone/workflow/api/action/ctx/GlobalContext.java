package cc.pineclone.workflow.api.action.ctx;

public interface GlobalContext extends ExecutionContext {

    @Override
    default ContextScope scope() {
        return ContextScope.GLOBAL;
    }

}
