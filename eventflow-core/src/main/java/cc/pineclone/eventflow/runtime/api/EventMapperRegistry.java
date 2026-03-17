package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.mapper.EventMapper;

import java.util.Collection;
import java.util.Optional;

@Deprecated
public interface EventMapperRegistry {

    void register(EventMapper mapper);

    Optional<EventMapper> get(ComponentId identity);

    Collection<EventMapper> getAll();

}
