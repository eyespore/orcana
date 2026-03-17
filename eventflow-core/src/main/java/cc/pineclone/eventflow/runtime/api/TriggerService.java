package cc.pineclone.eventflow.runtime.api;


import cc.pineclone.eventflow.plugin.api.ComponentTemplateRegistrar;
import cc.pineclone.eventflow.core.api.event.EventSource;
import cc.pineclone.eventflow.plugin.api.template.TriggerTemplate;

@Deprecated
public interface TriggerService {

    TriggerAdmin admin();

    TriggerQuery query();

    EventSource events();

    ComponentTemplateRegistrar<TriggerTemplate> triggerTemplateRegistrar();

}
