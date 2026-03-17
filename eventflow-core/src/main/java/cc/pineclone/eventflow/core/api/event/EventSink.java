package cc.pineclone.eventflow.core.api.event;

@FunctionalInterface
public interface EventSink {

    void emit(Event event);

}
