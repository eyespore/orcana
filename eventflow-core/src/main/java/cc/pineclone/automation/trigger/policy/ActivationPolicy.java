package cc.pineclone.automation.trigger.policy;

/* 触发模式，例如按住、切换 */

import cc.pineclone.automation.MacroLifecycleAware;
import cc.pineclone.automation.trigger.TriggerStatus;
import cc.pineclone.automation.trigger.source.InputSource;
import cc.pineclone.automation.trigger.source.InputSourceEvent;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @see HoldPolicy
 * @see TogglePolicy
 */
public interface ActivationPolicy extends MacroLifecycleAware {

    /**
     * 对{@link InputSource}传递的事件进行判定，给出是否可以执行下一步的判断
     * @param event 输入事件，通常来源于InputSource类，会被Trigger类实现如 SimpleTrigger 桥接到 Policy 当中
     * @param callback 回调函数，由于 Policy 的判断逻辑可能不会立即返回，例如双击这样的延迟判断，因此采用回调函数的
     *                 形式，回调会在 Policy 确保状态可以被返回的情况下触发
     */
    void decide(InputSourceEvent event, Consumer<Optional<TriggerStatus>> callback);

    static ActivationPolicy toggle() {
        return new TogglePolicy();
    }

    static ActivationPolicy hold() {
        return new HoldPolicy();
    }

    static ActivationPolicy click() {
        return new ClickPolicy();
    }

    static ActivationPolicy doubleClick(long interval) {
        return new DoubleClickPolicy(interval);
    }

    default Optional<TriggerStatus> fire(TriggerStatus status) {
        return Optional.of(status);
    }

}
