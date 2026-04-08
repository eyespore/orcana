package cc.pineclone.eventflow.config.api.definition;

import java.util.List;
import java.util.Objects;

public record RuntimeDefinition(
        List<RootTriggerDefinition> rootTriggers,
        List<EventMapperDefinition> eventMappers,
        List<ActionDefinition> actions,
        List<EventRouteDefinition> eventBindings,
        List<CommandRouteDefinition> commandBindings
) {

    public RuntimeDefinition {
        rootTriggers = List.copyOf(Objects.requireNonNull(rootTriggers, "rootTriggers"));
        eventMappers = List.copyOf(Objects.requireNonNull(eventMappers, "eventMappers"));
        actions = List.copyOf(Objects.requireNonNull(actions, "actions"));
        eventBindings = List.copyOf(Objects.requireNonNull(eventBindings, "eventBindings"));
        commandBindings = List.copyOf(Objects.requireNonNull(commandBindings, "commandBindings"));
    }

}
