package cc.pineclone.eventflow.core.api.command;

import cc.pineclone.eventflow.core.api.ComponentId;

import java.util.Map;

public interface Command {

    ComponentId source();

    String command();

    Map<String, Object> args();

}
