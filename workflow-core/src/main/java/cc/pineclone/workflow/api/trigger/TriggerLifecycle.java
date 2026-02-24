package cc.pineclone.workflow.api.trigger;

public interface TriggerLifecycle extends AutoCloseable {

    void init();

    void stop();

    @Override
    void close();

}
