package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.binding.EventBinding;
import cc.pineclone.eventflow.core.api.context.FlowSession;
import cc.pineclone.eventflow.config.api.definition.EventBindingDefinition;
import cc.pineclone.eventflow.core.api.event.Event;

import java.util.List;

@Deprecated
public interface EventProcessor {

    void process(Event event);

    void process(Event event, FlowSession session);

    List<EventBinding> bindings();

    boolean addBinding(EventBinding binding);

    boolean removeBinding(ComponentId bindingId);

    void replaceBindings(List<EventBinding> bindings);

    void clearBindings();

}
