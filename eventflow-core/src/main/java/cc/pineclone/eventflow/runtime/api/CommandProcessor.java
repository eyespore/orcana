package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.context.FlowSession;
import cc.pineclone.eventflow.config.api.definition.CommandBindingDefinition;

import java.util.List;

@Deprecated
public interface CommandProcessor {

    void process(Command command, FlowSession session);

    List<CommandBindingDefinition> bindings();

    boolean addBinding(CommandBindingDefinition binding);

    boolean removeBinding(String bindingId);

    void replaceBindings(List<CommandBindingDefinition> bindings);

    void clearBindings();

}
