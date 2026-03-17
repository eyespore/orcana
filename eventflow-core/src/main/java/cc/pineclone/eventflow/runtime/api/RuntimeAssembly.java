package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.action.Action;
import cc.pineclone.eventflow.core.api.binding.CommandBinding;
import cc.pineclone.eventflow.core.api.binding.EventBinding;
import cc.pineclone.eventflow.core.api.mapper.EventMapper;
import cc.pineclone.eventflow.core.api.trigger.Trigger;

import java.util.List;
import java.util.Objects;

public record RuntimeAssembly(
        List<Trigger> triggers,
        List<Action> actions,
        List<EventMapper> eventMappers,
        List<EventBinding> eventBindings,
        List<CommandBinding> commandBindings
) {

    public RuntimeAssembly {
        triggers = List.copyOf(Objects.requireNonNull(triggers, "rootTriggers"));
        actions = List.copyOf(Objects.requireNonNull(actions, "actions"));
        eventMappers = List.copyOf(Objects.requireNonNull(eventMappers, "eventMappers"));
        eventBindings = List.copyOf(Objects.requireNonNull(eventBindings, "eventBindings"));
        commandBindings = List.copyOf(Objects.requireNonNull(commandBindings, "commandBindings"));
    }

}
