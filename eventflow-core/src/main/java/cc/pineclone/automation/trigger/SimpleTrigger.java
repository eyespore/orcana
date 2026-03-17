package cc.pineclone.automation.trigger;

import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.trigger.policy.ActivationPolicy;
import cc.pineclone.automation.trigger.source.InputSource;
import cc.pineclone.automation.trigger.source.InputSourceEvent;
import cc.pineclone.automation.trigger.source.InputSourceListener;

/* 触发器 */
public class SimpleTrigger extends Trigger implements InputSourceListener {

    private final ActivationPolicy policy;
    private final InputSource source;

    public SimpleTrigger(InputSource source, ActivationPolicy policy) {
        this.policy = policy;
        this.source = source;
        source.setListener(this);
    }

    /* 由于 SimpleTrigger 基于享元模式（Flyweight Mode）优化，作为最小触发单元，一个 SimpleTrigger 最终有可能指向多个 Macro，
    * 这就意味着 SimpleTrigger 不能直接跟随着某一个单独的 Macro 被关闭，或是跟随每一次 Macro 的启动而重复启动
    *  */

    /**
     * 对于启动，引入状态位launched，SimpleTrigger仅会在第一次创建时启动所有的InputSource和Policy，后续如果由于享元模式，
     * SimpleTrigger继续指向新的Macro实例，那么onMacroLaunch时初始化应该不再被触发
     */
    @Override
    public void onMacroLaunch(MacroEvent event) {
        if (!isLaunched) {
            this.source.onMacroLaunch(event);
            this.policy.onMacroLaunch(event);
            isLaunched = true;
        }
    }

    /**
     * 对于卸载，由于Macro会在卸载之前优先调用removeListener将自己从Trigger的监听器列表中移除，因此应该判断仅在
     * 自己的监听器列表被彻底清空时，才会触发Policy和Source的卸载，否则会导致其中一个宏卸载引起另一个宏失效的情况
     */
    @Override
    public void onMacroTerminate(MacroEvent event) {
        if (listeners.isEmpty()) {
            /* 监听器（Macro）已经被全部移除，可以执行对ActivationPolicy和InputSource的终止 */
            this.policy.onMacroTerminate(event);
            this.source.onMacroTerminate(event);
            isLaunched = false;
        } else {
            log.debug("Trigger is not allow to terminate, unregistered listeners: {}", listeners);
        }
    }

    @Override
    public void onInputSourceEvent(InputSourceEvent event) {
        policy.decide(event, o -> o.ifPresent(
                s -> super.fire(TriggerEvent.of(this, s, event))));
    }

    @Override
    public String toString() {
        return "SimpleTrigger{" +
                "policy=" + policy +
                ", source=" + source +
                '}';
    }
}
