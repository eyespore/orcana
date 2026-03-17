package cc.pineclone.eventflow.core.api.binding;

import cc.pineclone.eventflow.core.api.ComponentId;

public record EventSelector(
        ComponentId source,
        String type
) {

    public static EventSelector fromString(String s) {
        if (s == null) {
            throw new IllegalArgumentException("TriggerEventKey string must not be null");
        }
        String[] parts = s.split(":", -1); // 保留空段，便于定位问题
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Invalid TriggerEventKey format, expected 'domain:name:type' but got: " + s
            );
        }
        return new EventSelector(new ComponentId(parts[0], parts[1]), parts[2]);
    }
}
