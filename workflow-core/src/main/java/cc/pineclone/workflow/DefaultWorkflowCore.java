package cc.pineclone.workflow;

import cc.pineclone.workflow.api.PluginManager;
import cc.pineclone.workflow.api.WorkflowCore;
import cc.pineclone.workflow.api.WorkflowPlugin;
import cc.pineclone.workflow.api.trigger.*;
import cc.pineclone.workflow.trigger.DefaultTriggerEventDispatcher;
import cc.pineclone.workflow.trigger.DefaultTriggerFactoryManager;
import cc.pineclone.workflow.trigger.DefaultTriggerRegistry;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultWorkflowCore implements WorkflowCore {

    private final TriggerFactoryManager triggerFactoryManager;
    private final TriggerRegistry triggerRegistry;
    private final PluginManager pluginManager;

    private final BlockingQueue<TriggerEvent> eventQueue;
    private final TriggerEventDispatcher dispatcher;

    public DefaultWorkflowCore() {
        this.triggerFactoryManager = new DefaultTriggerFactoryManager();
        this.triggerRegistry = new DefaultTriggerRegistry(triggerFactoryManager);
        this.pluginManager = new DefaultPluginManager(triggerFactoryManager);

        eventQueue = new LinkedBlockingQueue<>();
        dispatcher = new DefaultTriggerEventDispatcher(eventQueue);
    }

    @Override
    public void registerPlugin(WorkflowPlugin plugin) {
        this.pluginManager.registerPlugin(plugin);
    }

    @Override
    public void unregisterTrigger(TriggerIdentity identity) {
        this.triggerRegistry.unregister(identity);
    }

    @Override
    public TriggerIdentity registerTrigger(TriggerDefinition definition) {
        return this.triggerRegistry.register(definition);
    }

    @Override
    public void init() {
        pluginManager.initializePlugins();
    }

    @Override
    public void start() {
        pluginManager.startPlugins();

//        triggerRegistry.getRootTriggers().forEach(t -> t.attach(this.dispatcher));
    }

    @Override
    public void stop() {
        pluginManager.stopPlugins();

//        triggerRegistry.getRootTriggers().forEach(Trigger::detach);
        eventQueue.clear();
    }

    @Override
    public void destroy() {
        pluginManager.destroyPlugins();
    }

    @Override
    public void addRoute() {

    }

    @Override
    public void delRoute() {

    }

    public List<Trigger> getRootTriggers() {
        return triggerRegistry.getRootTriggers();
    }
}
