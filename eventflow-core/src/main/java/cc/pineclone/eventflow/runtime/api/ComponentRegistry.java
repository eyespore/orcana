package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.FlowComponent;

import java.util.Collection;
import java.util.Optional;

@Deprecated
public interface ComponentRegistry<C extends FlowComponent> {

    void register(C component);

    Optional<C> get(ComponentId identity);

    Collection<C> getAll();

    boolean contains(ComponentId identity);

    void unregister(ComponentId identity);

    void start(ComponentId identity);

    void stop(ComponentId identity);

    void close(ComponentId identity);

}
