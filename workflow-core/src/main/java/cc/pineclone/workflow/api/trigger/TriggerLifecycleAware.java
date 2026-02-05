package cc.pineclone.workflow.api.trigger;

public interface TriggerLifecycleAware {

    void init();

    void destroy();

}
