package cc.pineclone.workflow.api.trigger;

import cc.pineclone.workflow.api.plugin.Plugin;
import cc.pineclone.workflow.api.trigger.factory.TriggerFactoryRegistrar;

public interface TriggerPlugin extends Plugin {

    void registerTriggerFactories(TriggerFactoryRegistrar registrar);

}
