package cc.pineclone.eventflow.core.api;

import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.context.MapperContext;
import cc.pineclone.eventflow.core.api.event.Event;

import java.util.List;

public interface Mapper extends CoreComponent {

    List<Command> map(Event event, MapperContext context);

}
