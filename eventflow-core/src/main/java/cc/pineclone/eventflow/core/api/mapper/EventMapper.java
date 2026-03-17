package cc.pineclone.eventflow.core.api.mapper;

import cc.pineclone.eventflow.core.api.FlowComponent;
import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.context.FlowContext;
import cc.pineclone.eventflow.core.api.event.Event;

import java.util.List;

public interface EventMapper extends FlowComponent {

    List<Command> map(Event event, FlowContext context);

}
