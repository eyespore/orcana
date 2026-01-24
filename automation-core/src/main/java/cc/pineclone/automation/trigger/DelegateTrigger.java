package cc.pineclone.automation.trigger;

import cc.pineclone.automation.MacroEvent;

import java.util.Set;

/**
 * 委托类型Trigger，它不自己判断触发条件，不依赖任何外部环境，而是通过聚合、包装、转发下层Trigger来变更
 * 下层Trigger产生的语义，向上层继续转发，通常以SimpleTrigger作为原子
 */
public abstract class DelegateTrigger extends Trigger implements TriggerListener {

    protected final Set<Trigger> triggers;

    public DelegateTrigger(Set<Trigger> triggers) {
        this.triggers = triggers;
        this.triggers.forEach(t -> t.addListener(this));
    }

    @Override
    public void onMacroLaunch(MacroEvent event) {
        if (!isLaunched) {
            triggers.forEach(trigger -> trigger.onMacroLaunch(event));
            isLaunched = true;
        }
    }

    @Override
    public void onMacroTerminate(MacroEvent event) {
        if (listeners.isEmpty()) {
            triggers.forEach(t -> t.removeListener(this));
            triggers.forEach(trigger -> trigger.onMacroTerminate(event));
        }
    }
}
