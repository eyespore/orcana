package cc.pineclone.workflow.api.trigger;

@FunctionalInterface
public interface TriggerEventSink {

    void emit(TriggerEvent event);

}
