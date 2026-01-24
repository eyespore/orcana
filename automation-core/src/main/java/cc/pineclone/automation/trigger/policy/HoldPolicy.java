package cc.pineclone.automation.trigger.policy;

import cc.pineclone.automation.trigger.TriggerStatus;
import cc.pineclone.automation.trigger.source.InputSourceEvent;

import java.util.Optional;
import java.util.function.Consumer;

public class HoldPolicy implements ActivationPolicy {
    @Override
    public void decide(InputSourceEvent event, Consumer<Optional<TriggerStatus>> callback) {
        switch (event.getOperation()) {
            case MOUSE_PRESSED, KEY_PRESSED -> callback.accept(fire(TriggerStatus.HOLD_START));  /* 按键按下 */
            case MOUSE_RELEASED, KEY_RELEASED -> callback.accept(fire(TriggerStatus.HOLD_STOP));  /* 按键抬起 */
            default -> callback.accept(Optional.empty());  /* 默认忽略 */
        };
    }
}
