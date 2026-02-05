package cc.pineclone.workflow.api.action;

public interface ExecutionPolicy {

    void apply(Operation action, ExecutionContext context);

}
