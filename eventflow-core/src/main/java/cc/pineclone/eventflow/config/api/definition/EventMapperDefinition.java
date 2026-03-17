package cc.pineclone.eventflow.config.api.definition;

import cc.pineclone.eventflow.core.api.ComponentId;

import java.util.Map;

public record EventMapperDefinition(
        String templateType,
        ComponentId identity,
        Map<String, Object> properties
) implements ComponentDefinition {
}
