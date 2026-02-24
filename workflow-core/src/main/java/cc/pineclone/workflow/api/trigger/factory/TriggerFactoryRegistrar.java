package cc.pineclone.workflow.api.trigger.factory;

public interface TriggerFactoryRegistrar {

    <T extends TriggerDefinition> void registerTriggerFactory(TriggerFactory<T> factory);

}
