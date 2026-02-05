package cc.pineclone.workflow.api.trigger;

import java.util.List;

public interface TriggerRegistry {

    TriggerIdentity register(TriggerDefinition definition);

    void unregister(TriggerIdentity identity);

    void retain(TriggerIdentity identity);

    void release(TriggerIdentity identity);

    List<Trigger> getRootTriggers();

}
