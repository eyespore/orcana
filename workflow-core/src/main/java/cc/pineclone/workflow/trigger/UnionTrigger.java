package cc.pineclone.workflow.trigger;

import cc.pineclone.workflow.api.trigger.TriggerEvent;
import cc.pineclone.workflow.api.trigger.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;

import java.util.*;

public class UnionTrigger extends DefaultCompositeTrigger {

    private final TriggerIdentity identity;
    private final Map<TriggerEventIdentity, TriggerEventIdentity> eventMapping;

    public UnionTrigger(
            TriggerIdentity identity,
            Map<TriggerEventIdentity, TriggerEventIdentity> eventMapping
    ) {
        this.identity = identity;
        this.eventMapping = Map.copyOf(eventMapping);
    }

    /* 通过对 handleChildTriggerEvent 和 reset 同时加锁避免由于 Trigger 本身线程和 scheduler线程 */
    /* 触发的对 receivedEvents 的竞态修改问题 */
    @Override
    public synchronized void handleChildTriggerEvent(TriggerEvent event) {
        TriggerEventIdentity input = event.getIdentity();
        if (!eventMapping.containsKey(input)) return;

        TriggerEventIdentity output = eventMapping.get(input);  // 映射成输出事件
        emit(output, event.getMeta());
    }

    @Override
    public TriggerIdentity getIdentity() {
        return this.identity;
    }
}
