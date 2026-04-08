package cc.pineclone.eventflow.runtime.impl.event;

import cc.pineclone.eventflow.runtime.api.event.RuntimeEvent;
import cc.pineclone.eventflow.runtime.api.event.RuntimeSubjectType;

import java.util.Map;
import java.util.Objects;

public record DefaultRuntimeEvent(
        RuntimeSubjectType subjectType,
        String subjectId,
        String type,
        long timestamp,
        Map<String, Object> meta
) implements RuntimeEvent {

    public DefaultRuntimeEvent {
        Objects.requireNonNull(subjectType, "subjectType");
        Objects.requireNonNull(subjectId, "subjectId");
        Objects.requireNonNull(type, "type");
        meta = (meta == null) ? Map.of() : Map.copyOf(meta);
    }

}
