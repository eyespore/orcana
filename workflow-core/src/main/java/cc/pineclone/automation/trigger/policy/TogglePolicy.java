package cc.pineclone.automation.trigger.policy;

import cc.pineclone.automation.trigger.TriggerStatus;
import cc.pineclone.automation.trigger.source.InputSourceEvent;

import java.util.Optional;
import java.util.function.Consumer;

public class TogglePolicy implements ActivationPolicy {
    private boolean toggled = false;

    @Override
    public void decide(InputSourceEvent event, Consumer<Optional<TriggerStatus>> callback) {
        switch (event.getOperation()) {
            case KEY_PRESSED, MOUSE_PRESSED -> {
                toggled = !toggled;
                TriggerStatus status = toggled ? TriggerStatus.TOGGLE_ON : TriggerStatus.TOGGLE_OFF;
                callback.accept(fire(status));
            }
            default -> callback.accept(Optional.empty());
        };
    }
}
