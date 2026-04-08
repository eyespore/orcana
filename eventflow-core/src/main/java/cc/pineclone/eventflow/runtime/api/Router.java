package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.event.Event;

import java.util.List;

public interface Router {

    List<ComponentId> routeEvent(Event event);

    List<ComponentId> routeCommand(Command command);

}
