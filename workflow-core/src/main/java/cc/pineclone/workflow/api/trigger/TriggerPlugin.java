package cc.pineclone.workflow.api.trigger;

import cc.pineclone.workflow.api.WorkflowPlugin;

public interface TriggerPlugin extends WorkflowPlugin {

    void registerTriggerFactories(TriggerFactoryRegistrar registrar);

    default int order() {
        return 0;
    }

}
