package cc.pineclone.workflow.api.action.ctx;

import java.util.Map;

public interface CallContext extends ExecutionContext {

    String command();
    Map<String, Object> meta();

    @Override
    default ContextScope scope() {
        return ContextScope.CALL;
    }
}
