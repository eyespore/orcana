package cc.pineclone.workflow.api.trigger;

import cc.pineclone.workflow.api.trigger.event.TriggerEvent;

import java.util.List;

public interface TriggerAggregator {

    List<Trigger> getChildren();

    void addChildren(Trigger trigger);

    void removeChildren(Trigger trigger);

    void handleChildTriggerEvent(TriggerEvent event);

}
