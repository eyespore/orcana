package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.mapper.EventMapper;

import java.util.List;

@Deprecated
public interface EventResolver {

    List<EventMapper> resolve(Event event);

}
