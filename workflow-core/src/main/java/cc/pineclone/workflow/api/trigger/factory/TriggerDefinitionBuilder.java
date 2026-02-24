package cc.pineclone.workflow.api.trigger.factory;

import cc.pineclone.workflow.api.trigger.TriggerIdentity;

public abstract class TriggerDefinitionBuilder<T extends TriggerDefinition> {

    protected TriggerIdentity identity;

    public TriggerDefinitionBuilder<T> identity(TriggerIdentity id) {
        this.identity = id;
        return this;
    }

    public abstract T build();
}
