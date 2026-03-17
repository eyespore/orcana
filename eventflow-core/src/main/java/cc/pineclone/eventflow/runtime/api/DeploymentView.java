package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.action.Action;
import cc.pineclone.eventflow.core.api.binding.CommandBinding;
import cc.pineclone.eventflow.core.api.binding.EventBinding;
import cc.pineclone.eventflow.core.api.mapper.EventMapper;

import java.util.List;

public interface DeploymentView {

    List<RootTrigger> rootTriggers();

    List<Action> actions();

    List<EventMapper> eventMappers();

    List<EventBinding> eventBindings();

    List<CommandBinding> commandBindings();

}
