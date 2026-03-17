package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.action.Action;
import cc.pineclone.eventflow.core.api.context.RuntimeSession;
import cc.pineclone.eventflow.core.api.mapper.EventMapper;
import cc.pineclone.eventflow.core.api.trigger.Trigger;
import cc.pineclone.eventflow.plugin.api.Plugin;
import cc.pineclone.eventflow.runtime.api.event.StatusEventBus;

import java.util.Optional;

public interface Runtime {

    void start();

    void stop();

    FlowSessionManager flowSessionManager();

    FlowSessionFactory flowSessionFactory();

    RuntimeSession runtimeSession();

    StatusEventBus statusEventBus();

    Optional<DeploymentView> deploymentView();

    void deploy(RuntimeAssembly assembly);

    @Deprecated
    CommandProcessor commandProcessor();

    @Deprecated
    EventProcessor eventProcessor();

    @Deprecated
    default CommandResolver commandResolver() {
        return null;
    }

    @Deprecated
    default EventResolver eventResolver() {
        return null;
    }

    @Deprecated
    default void registerPlugin(Plugin plugin) {}

    @Deprecated
    default TriggerService triggerService() {
        return null;
    };

    @Deprecated
    default void init() {}

    @Deprecated
    default ComponentRegistry<Trigger> triggers() {
        return null;
    }

    @Deprecated
    default ComponentRegistry<Action> actions() {
        return null;
    }

    @Deprecated
    default ComponentRegistry<EventMapper> eventMappers() {
        return null;
    }

}
