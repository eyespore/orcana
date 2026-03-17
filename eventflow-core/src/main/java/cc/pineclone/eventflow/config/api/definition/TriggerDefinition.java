package cc.pineclone.eventflow.config.api.definition;

import cc.pineclone.eventflow.core.api.ComponentId;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 应当确保两个属性相同的 TriggerDefinition 的 Hash 校验是唯一的，来避免相同的 TriggerDefinition 却创建了两个
 * Trigger
 */
public record TriggerDefinition(
        String templateType,
        ComponentId identity,
        Map<String, Object> properties,
        List<TriggerDefinition> children
) implements ComponentDefinition {

    public TriggerDefinition {
        templateType = Objects.requireNonNull(templateType, "templateType");
        if (templateType.isBlank()) throw new IllegalArgumentException("templateType must be non-blank");

        Objects.requireNonNull(identity, "identity");

        properties = Collections.unmodifiableMap(
                Objects.requireNonNull(properties, "properties"));

        children = List.copyOf(Objects.requireNonNull(children, "children"));
    }

}
