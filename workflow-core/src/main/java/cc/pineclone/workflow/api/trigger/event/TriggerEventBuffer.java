package cc.pineclone.workflow.api.trigger.event;

public interface TriggerEventBuffer extends
        TriggerEventSink,
        TriggerEventSource,
        AutoCloseable {

    @Override
    void close();

}
