package cc.pineclone.automation.trigger;


import cc.pineclone.automation.trigger.source.JNativeHookInputSourceEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class TriggerEvent {

    private Trigger source;
    private TriggerStatus triggerStatus;
    private JNativeHookInputSourceEvent inputSourceEvent;

    private TriggerEvent(final Trigger source, TriggerStatus triggerStatus, JNativeHookInputSourceEvent inputSourceEvent) {
        this.source = source;
        this.triggerStatus = triggerStatus;
        this.inputSourceEvent = inputSourceEvent;
    }

    public static TriggerEvent of(Trigger source, TriggerStatus status, JNativeHookInputSourceEvent inputSourceEvent) {
        return new TriggerEvent(source, status, inputSourceEvent);
    }
}
