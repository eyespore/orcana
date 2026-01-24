package cc.pineclone.automation.trigger;

import cc.pineclone.automation.MacroLifecycleAware;
import cc.pineclone.automation.trigger.policy.ActivationPolicy;
import cc.pineclone.automation.trigger.source.InputSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Trigger是"触发器"的抽象，它主要用于描述“如何触发”
 *
 * @see SimpleTrigger 简单触发器，这是触发器的最小单元，它通过桥接模式，组合触发源{@link InputSource}以及触发模式
 * {@link ActivationPolicy}描述了如何触发
 *
 * @see CompositeTrigger 复合触发器，通过汇集{@link SimpleTrigger}来达到“与”的效果，当所有合集中的简单触发器
 * 被触发，复合触发器才会触发，通过组合触发器可以实现类似[复合快捷键]的效果
 *
 * 也许未来会拓展更多触发器? 这一套设计很不错
 */
public abstract class Trigger implements MacroLifecycleAware {

    protected final Set<TriggerListener> listeners = new HashSet<>();
    protected volatile boolean isLaunched = false;  /* 记录当前Trigger是否已经被启动，由于Trigger采用享元模式，应该避免Trigger被重复启动 */
    protected Logger log = LoggerFactory.getLogger(getClass());

    protected void fire(TriggerEvent event) {
        listeners.forEach(l -> l.onTriggerEvent(event));
    }

    /* 覆写这两个方法是危险的，应该避免覆写它们 */

    /* 添加监听器 */
    public final void addListener(TriggerListener listener) {
        listeners.add(listener);
    }

    /* 移除监听器 */
    public final void removeListener(TriggerListener listener) {
        listeners.remove(listener);
    }
}
