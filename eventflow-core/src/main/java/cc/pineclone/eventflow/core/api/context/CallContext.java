package cc.pineclone.eventflow.core.api.context;

import java.util.Map;

@Deprecated
public interface CallContext extends ExecutionContext {

    String command();
    Map<String, Object> meta();

    @Override
    default ContextScope scope() {
        return ContextScope.CALL;
    }
}
