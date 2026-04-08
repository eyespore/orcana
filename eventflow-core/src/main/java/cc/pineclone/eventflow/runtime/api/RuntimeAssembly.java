package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.Action;
import cc.pineclone.eventflow.core.api.Mapper;

import java.util.List;
import java.util.Objects;

public record RuntimeAssembly(
        List<RootTrigger> rootTriggers,
        List<Mapper> mappers,
        List<Action> actions,
        Router router
) {

    public RuntimeAssembly {
        rootTriggers = List.copyOf(Objects.requireNonNull(rootTriggers, "rootTriggers"));
        actions = List.copyOf(Objects.requireNonNull(actions, "actions"));
        mappers = List.copyOf(Objects.requireNonNull(mappers, "eventMappers"));
        Objects.requireNonNull(router, "router");
    }

}
