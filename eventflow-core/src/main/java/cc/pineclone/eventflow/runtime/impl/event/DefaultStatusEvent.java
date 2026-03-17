package cc.pineclone.eventflow.runtime.impl.event;

import cc.pineclone.eventflow.runtime.api.event.StatusEvent;
import cc.pineclone.eventflow.runtime.api.event.StatusSubjectType;

import java.util.Map;
import java.util.Objects;

public record DefaultStatusEvent(
        StatusSubjectType subjectType,
        String subjectId,
        String type,
        long timestamp,
        Map<String, Object> meta
) implements StatusEvent {

    public DefaultStatusEvent {
        Objects.requireNonNull(subjectType, "subjectType");
        Objects.requireNonNull(subjectId, "subjectId");
        Objects.requireNonNull(type, "type");
        meta = (meta == null) ? Map.of() : Map.copyOf(meta);
    }

}
