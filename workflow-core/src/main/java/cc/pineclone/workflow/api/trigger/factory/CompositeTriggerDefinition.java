package cc.pineclone.workflow.api.trigger.factory;

import java.util.List;

public interface CompositeTriggerDefinition extends TriggerDefinition {

    List<TriggerDefinition> getChildDefinitions();

    void setChildDefinitions(List<TriggerDefinition> childDefinitions);

}
