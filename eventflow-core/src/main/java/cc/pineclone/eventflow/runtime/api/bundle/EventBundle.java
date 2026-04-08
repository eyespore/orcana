package cc.pineclone.eventflow.runtime.api.bundle;

import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.runtime.api.session.RuntimeSession;

public record EventBundle(
        RuntimeSession runtimeSession,
        Event event
) implements RuntimeBundle {
}
