package cc.pineclone.workflow.api.trigger;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeTriggerDefinitionBuilder<T extends CompositeTriggerDefinition>
        extends TriggerDefinitionBuilder<T>  {

    protected List<TriggerDefinition> childDefinitions = new ArrayList<>();

    public CompositeTriggerDefinitionBuilder<T> addChildren(TriggerDefinition child) {
        this.childDefinitions.add(child);
        return this;
    }
}
