package cc.pineclone.eventflow.config.api;

import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;

import java.util.List;

@Deprecated
public interface CompositeTriggerDefinition extends TriggerDefinition {

    List<TriggerDefinition> children();

    void setChildren(List<TriggerDefinition> children);

}
