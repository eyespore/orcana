package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.runtime.api.event.RuntimeEventBus;
import cc.pineclone.eventflow.runtime.api.session.GlobalSession;
import cc.pineclone.eventflow.runtime.api.session.RuntimeSessionRegistry;

import java.util.Optional;

public interface Runtime {

    void init();

    void start();

    void stop();

    Status status();

    RuntimeSessionRegistry runtimeSessionRegistry();

    GlobalSession globalSession();

    Optional<DeploymentView> deploymentView();

    void deploy(RuntimeAssembly assembly);

    RuntimeEventBus runtimeEventBus();

    enum Status {
        IDLE,
        INITIALIZED,
        STARTED,
        STOPPING,
        STOPPED
    }

}
