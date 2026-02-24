package cc.pineclone.workflow.api.trigger.factory;

import cc.pineclone.workflow.api.trigger.Trigger;

public interface TriggerFactory<T extends TriggerDefinition> {

    Class<T> definitionType();

    Trigger createTrigger(T definition);

}
