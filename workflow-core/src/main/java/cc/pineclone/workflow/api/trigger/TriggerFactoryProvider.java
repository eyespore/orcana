package cc.pineclone.workflow.api.trigger;

public interface TriggerFactoryProvider {

    <T extends TriggerDefinition> TriggerFactory<T> getTriggerFactory(Class<T> definitionType);

}
