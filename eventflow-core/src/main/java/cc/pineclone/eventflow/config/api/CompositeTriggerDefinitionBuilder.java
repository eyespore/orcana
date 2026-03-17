package cc.pineclone.eventflow.config.api;

import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class CompositeTriggerDefinitionBuilder<T extends CompositeTriggerDefinition>
        extends TriggerDefinitionBuilder<T> {

    protected List<TriggerDefinition> childDefinitions = new ArrayList<>();

    public CompositeTriggerDefinitionBuilder<T> addChildren(TriggerDefinition child) {
        this.childDefinitions.add(child);
        return this;
    }
}
