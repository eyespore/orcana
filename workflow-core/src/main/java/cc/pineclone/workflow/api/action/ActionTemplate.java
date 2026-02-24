package cc.pineclone.workflow.api.action;

import java.util.Set;

public interface ActionTemplate {

    Set<String> supportedCommands();

    ActionHandler newHandler();

}
