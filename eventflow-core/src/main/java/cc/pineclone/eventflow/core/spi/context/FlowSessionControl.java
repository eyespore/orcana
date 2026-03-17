package cc.pineclone.eventflow.core.spi.context;

public interface FlowSessionControl {

    void cancel();

    void cancel(String reason);

}
