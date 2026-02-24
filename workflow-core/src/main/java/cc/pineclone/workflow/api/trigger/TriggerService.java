package cc.pineclone.workflow.api.trigger;


import cc.pineclone.workflow.api.trigger.event.TriggerEventSource;
import cc.pineclone.workflow.api.trigger.factory.TriggerFactoryRegistrar;

public interface TriggerService {

    TriggerAdmin admin();

    TriggerQuery query();

    TriggerEventSource events();

    TriggerFactoryRegistrar factories();

}
