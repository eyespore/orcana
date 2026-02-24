package cc.pineclone.workflow.api.trigger.event;

@FunctionalInterface
public interface TriggerEventSink {

    void emit(TriggerEvent event);

}
