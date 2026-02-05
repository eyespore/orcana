package cc.pineclone.workflow.api.trigger;

public interface TriggerFactory<T extends TriggerDefinition> {

    Class<T> definitionType();

    Trigger createTrigger(T definition);

}
