package cc.pineclone.workflow.api.trigger.event;

import java.util.Collection;

public interface TriggerEventRouter {

    Collection<TriggerEventTarget> match(TriggerEvent event);

}
