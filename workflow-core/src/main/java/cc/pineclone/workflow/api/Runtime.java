package cc.pineclone.workflow.api;

import cc.pineclone.workflow.api.plugin.Plugin;
import cc.pineclone.workflow.api.trigger.TriggerService;

public interface Runtime {

    void registerPlugin(Plugin plugin);

    TriggerService triggerService();

    void init();

    void start();

    void stop();
}
