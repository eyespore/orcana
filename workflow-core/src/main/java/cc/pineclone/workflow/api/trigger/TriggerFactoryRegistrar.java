package cc.pineclone.workflow.api.trigger;

public interface TriggerFactoryRegistrar {

    <T extends TriggerDefinition> void registerTriggerFactory(TriggerFactory<T> factory);

}
