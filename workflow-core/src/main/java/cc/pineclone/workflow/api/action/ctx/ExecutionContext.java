package cc.pineclone.workflow.api.action.ctx;

public interface ExecutionContext {

    enum ContextScope {
        GLOBAL,
        SESSION,
        CALL
    }

    Object get(String key);
    void put(String key, Object value);

    ContextScope scope();

}
