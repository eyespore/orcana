package cc.pineclone.automation.trigger;


import cc.pineclone.automation.trigger.source.InputSourceEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class TriggerEvent {

    private Trigger source;
    private TriggerStatus triggerStatus;
    private InputSourceEvent inputSourceEvent;

    private TriggerEvent(final Trigger source, TriggerStatus triggerStatus, InputSourceEvent inputSourceEvent) {
        this.source = source;
        this.triggerStatus = triggerStatus;
        this.inputSourceEvent = inputSourceEvent;
    }

    public static TriggerEvent of(Trigger source, TriggerStatus status, InputSourceEvent inputSourceEvent) {
        return new TriggerEvent(source, status, inputSourceEvent);
    }
}
