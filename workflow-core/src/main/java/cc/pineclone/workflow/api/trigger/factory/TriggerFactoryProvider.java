package cc.pineclone.workflow.api.trigger.factory;

public interface TriggerFactoryProvider {

    <T extends TriggerDefinition> TriggerFactory<T> getTriggerFactory(Class<T> definitionType);

}
