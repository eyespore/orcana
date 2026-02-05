package cc.pineclone.workflow.api.trigger;

public interface Trigger {

    void attach(TriggerEventSink sink);

    void detach();

    TriggerIdentity getIdentity();

}
