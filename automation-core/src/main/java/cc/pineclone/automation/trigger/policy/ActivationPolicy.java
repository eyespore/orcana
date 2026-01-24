package cc.pineclone.automation.trigger.policy;

/* 触发模式，例如按住、切换 */

import cc.pineclone.automation.AutomationJobEventListener;
import cc.pineclone.automation.trigger.TriggerStatus;
import cc.pineclone.automation.trigger.source.JNativeHookInputSource;
import cc.pineclone.automation.trigger.source.JNativeHookInputSourceEvent;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @see HoldPolicy
 * @see TogglePolicy
 */
public interface ActivationPolicy extends AutomationJobEventListener {

    void decide(JNativeHookInputSourceEvent event, Consumer<Optional<TriggerStatus>> callback);

    default Optional<TriggerStatus> fire(TriggerStatus status) {
        return Optional.of(status);
    }

}
