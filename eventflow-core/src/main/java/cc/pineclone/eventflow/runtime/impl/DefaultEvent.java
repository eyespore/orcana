package cc.pineclone.eventflow.runtime.impl;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.event.Event;

import java.util.Map;
import java.util.Objects;

public record DefaultEvent(
        ComponentId source,
        String type,
        long timestamp,
        Map<String, Object> payload
) implements Event {

    public DefaultEvent {
        Objects.requireNonNull(source, "eventSourceId");
        Objects.requireNonNull(type, "type");
        if (type.isBlank()) {
            throw new IllegalArgumentException("eventType must not be blank");
        }
        payload = payload == null ? Map.of() : Map.copyOf(payload);
    }

    public DefaultEvent(ComponentId source, String type) {
        this(source, type, System.currentTimeMillis(), Map.of());
    }

    public DefaultEvent(ComponentId source, String type, Map<String, Object> meta) {
        this(source, type, System.currentTimeMillis(), meta);
    }

}
