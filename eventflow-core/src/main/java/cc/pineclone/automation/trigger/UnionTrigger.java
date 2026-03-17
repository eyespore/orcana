package cc.pineclone.automation.trigger;

import java.util.Set;

/**
 * “或”Trigger触发逻辑，组合多个Trigger，并在Trigger触发时原样传递Trigger的事件，不改变任何语义
 */
public class UnionTrigger extends DelegateTrigger {

    public UnionTrigger(Set<Trigger> triggers) {
        super(triggers);
    }

    @Override
    public void onTriggerEvent(TriggerEvent event) {
        /* 原样转发下层 Trigger 事件 */
        fire(TriggerEvent.of(this, event.getTriggerStatus(), event.getInputSourceEvent()));
    }
}
