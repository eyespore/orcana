package cc.pineclone.automation.trigger;

import cc.pineclone.automation.AutomationJobEvent;
import cc.pineclone.automation.AutomationJobEventListener;
import cc.pineclone.automation.trigger.policy.ActivationPolicy;
import cc.pineclone.automation.trigger.source.InputSource;
import cc.pineclone.automation.trigger.source.InputSourceListener;
import cc.pineclone.automation.trigger.source.JNativeHookInputSource;
import cc.pineclone.automation.trigger.source.JNativeHookInputSourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Trigger是"触发器"的抽象，它主要用于描述“如何触发”
 */
public abstract class Trigger implements AutomationJobEventListener, InputSourceListener {

    private final ActivationPolicy policy;
    private final InputSource source;

    protected final Set<TriggerListener> listeners = new HashSet<>();
    protected volatile boolean isLaunched = false;  /* 记录当前Trigger是否已经被启动，由于Trigger采用享元模式，应该避免Trigger被重复启动 */
    protected Logger log = LoggerFactory.getLogger(getClass());

    protected void fire(TriggerEvent event) {
        listeners.forEach(l -> l.onTriggerEvent(event));
    }

    /* 添加监听器 */
    public final void addListener(TriggerListener listener) {
        listeners.add(listener);
    }

    /* 移除监听器 */
    public final void removeListener(TriggerListener listener) {
        listeners.remove(listener);
    }

    public Trigger(InputSource source, ActivationPolicy policy) {
        this.policy = policy;
        this.source = source;
        source.setListener(this);
    }

    @Override
    public void onAutomationJobEvent(AutomationJobEvent event) {
        this.policy.onAutomationJobEvent(event);
        this.source.onAutomationJobEvent(event);
    }

    /* 由于 SimpleTrigger 基于享元模式（Flyweight Mode）优化，作为最小触发单元，一个 SimpleTrigger 最终有可能指向多个 Macro，
     * 这就意味着 SimpleTrigger 不能直接跟随着某一个单独的 Macro 被关闭，或是跟随每一次 Macro 的启动而重复启动
     *  */

    /**
     * 对于启动，引入状态位launched，SimpleTrigger仅会在第一次创建时启动所有的InputSource和Policy，后续如果由于享元模式，
     * SimpleTrigger继续指向新的Macro实例，那么onMacroLaunch时初始化应该不再被触发
     */
    @Override
    public void onAutomationLaunch(AutomationJobEvent event) {
        if (!isLaunched) {
            this.source.onAutomationLaunch(event);
            this.policy.onAutomationLaunch(event);
            isLaunched = true;
        }
    }

    /**
     * 对于卸载，由于Macro会在卸载之前优先调用removeListener将自己从Trigger的监听器列表中移除，因此应该判断仅在
     * 自己的监听器列表被彻底清空时，才会触发Policy和Source的卸载，否则会导致其中一个宏卸载引起另一个宏失效的情况
     */
    @Override
    public void onAutomationTerminate(AutomationJobEvent event) {
        if (listeners.isEmpty()) {
            /* 监听器（Macro）已经被全部移除，可以执行对ActivationPolicy和InputSource的终止 */
            this.policy.onAutomationTerminate(event);
            this.source.onAutomationTerminate(event);
            isLaunched = false;
        } else {
            log.debug("Trigger is not allow to terminate, unregistered listeners: {}", listeners);
        }
    }

    @Override
    public void onInputSourceEvent(JNativeHookInputSourceEvent event) {
        policy.decide(event, o -> o.ifPresent(
                s -> super.fire(TriggerEvent.of(this, s, event))));
    }

}
