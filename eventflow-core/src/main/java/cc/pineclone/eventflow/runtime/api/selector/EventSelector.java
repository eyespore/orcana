package cc.pineclone.eventflow.runtime.api.selector;

import cc.pineclone.eventflow.core.api.event.Event;

public interface EventSelector {

    boolean matches(Event event);

}
