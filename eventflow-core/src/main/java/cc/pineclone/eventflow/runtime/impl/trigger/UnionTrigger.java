package cc.pineclone.eventflow.runtime.impl.trigger;

import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.core.api.ComponentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UnionTrigger extends DefaultCompositeTrigger {

    private final Map<EventSelector, EventSelector> eventMapping;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public UnionTrigger(
            ComponentId identity,
            Map<EventSelector, EventSelector> eventMapping
    ) {
        super(identity);
        this.eventMapping = Map.copyOf(eventMapping);
    }

    /* 通过对 handleChildTriggerEvent 和 reset 同时加锁避免由于 Trigger 本身线程和 scheduler线程 */
    /* 触发的对 receivedEvents 的竞态修改问题 */
    @Override
    public synchronized void handleChildTriggerEvent(Event event) {
//        log.debug("On child event: {}", event.getIdentity());
        EventSelector inputEventKey = event.getEventKey();
        if (!eventMapping.containsKey(inputEventKey)) return;

        EventSelector mappedEventKey = eventMapping.get(inputEventKey);  // 映射成输出事件
        emit(mappedEventKey);
    }
}
