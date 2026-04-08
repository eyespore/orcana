package cc.pineclone.eventflow.runtime.api.bundle;

import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.runtime.api.RootTrigger;

public record RootEventBundle(
        RootTrigger rootTrigger,
        Event event,
        int attempt
) implements RuntimeBundle {

}


