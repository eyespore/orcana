package cc.pineclone.eventflow.core.api.binding;

import cc.pineclone.eventflow.core.api.ComponentId;

public record CommandSelector(
        ComponentId source,
        String command
) {
}
