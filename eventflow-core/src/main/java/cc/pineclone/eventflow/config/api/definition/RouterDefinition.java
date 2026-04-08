package cc.pineclone.eventflow.config.api.definition;

import java.util.List;

public record RouterDefinition(
    List<EventRouteDefinition> eventRoutes,
    List<CommandRouteDefinition> commandRoutes
) {

}
