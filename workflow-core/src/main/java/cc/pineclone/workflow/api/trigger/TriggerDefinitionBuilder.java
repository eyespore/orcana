package cc.pineclone.workflow.api.trigger;

public abstract class TriggerDefinitionBuilder<T extends TriggerDefinition> {

    protected TriggerIdentity identity;

    public TriggerDefinitionBuilder<T> identity(TriggerIdentity id) {
        this.identity = id;
        return this;
    }

    public abstract T build();
}
