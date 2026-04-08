package cc.pineclone.eventflow.plugin.api.template;

import cc.pineclone.eventflow.core.api.Action;
import cc.pineclone.eventflow.config.api.definition.ActionDefinition;

import java.util.Collections;
import java.util.Set;

public interface ActionTemplate extends ComponentTemplate<ActionDefinition, Action> {

    default Set<CommandDesc> supportedCommands() {
        return Collections.emptySet();
    }
}
