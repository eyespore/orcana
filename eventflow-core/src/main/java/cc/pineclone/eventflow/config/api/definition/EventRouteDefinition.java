package cc.pineclone.eventflow.config.api.definition;

import cc.pineclone.eventflow.core.api.ComponentId;

import java.util.List;

public record EventRouteDefinition(
        EventSelectorDefinition eventSelector,
        List<ComponentId> targetMappers
) {

}
