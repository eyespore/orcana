package cc.pineclone.eventflow.config.api;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;

@Deprecated
public abstract class TriggerDefinitionBuilder<T extends TriggerDefinition> {

    protected ComponentId identity;

    public TriggerDefinitionBuilder<T> identity(ComponentId id) {
        this.identity = id;
        return this;
    }

    public abstract T build();
}
