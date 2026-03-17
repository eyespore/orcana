package cc.pineclone.eventflow.config.api.definition;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.binding.CommandSelector;

import java.util.List;
import java.util.Objects;

public record CommandBindingDefinition(
        String bindingId,
        CommandSelector selector,
        List<ComponentId> actionIdentities
) {

    public CommandBindingDefinition {
        Objects.requireNonNull(selector, "command");
        actionIdentities = List.copyOf(Objects.requireNonNull(actionIdentities, "actionIdentities"));
        if (actionIdentities.isEmpty()) {
            throw new IllegalArgumentException("actionIdentities must not be empty");
        }
    }

}
