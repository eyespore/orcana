package cc.pineclone.eventflow.config.api.definition;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.binding.EventSelector;

import java.util.List;
import java.util.Objects;

public record EventBindingDefinition(
        String bindingId,
        EventSelector selector,
        List<ComponentId> mapperIdentities
) {

    public EventBindingDefinition {
        Objects.requireNonNull(selector, "selector");
        mapperIdentities = List.copyOf(Objects.requireNonNull(mapperIdentities, "mapperIdentities"));
        if (mapperIdentities.isEmpty()) {
            throw new IllegalArgumentException("mapperIdentities must not be empty");
        }
    }

}
