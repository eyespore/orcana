package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.Action;
import cc.pineclone.eventflow.core.api.Mapper;

import java.util.List;

public interface DeploymentView {

    List<RootTrigger> rootTriggers();

    List<Action> actions();

    List<Mapper> eventMappers();

    Router router();

}
