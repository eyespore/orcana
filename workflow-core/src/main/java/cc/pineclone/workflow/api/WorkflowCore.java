package cc.pineclone.workflow.api;

import cc.pineclone.workflow.api.trigger.TriggerDefinition;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;

public interface WorkflowCore {

    void registerPlugin(WorkflowPlugin plugin);

    void unregisterTrigger(TriggerIdentity identity);

    TriggerIdentity registerTrigger(TriggerDefinition definition);

    void init();

    void start();

    void stop();

    void destroy();

    void addRoute();

    void delRoute();

}
