package cc.pineclone.eventflow.core.api.action;

import cc.pineclone.eventflow.core.api.FlowComponent;
import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.context.FlowContext;
import cc.pineclone.eventflow.core.api.event.EventSink;

public interface Action extends FlowComponent {

    void execute(Command command, FlowContext context, EventSink sink);

}

