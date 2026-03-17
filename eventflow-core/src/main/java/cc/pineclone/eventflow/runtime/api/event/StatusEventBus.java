package cc.pineclone.eventflow.runtime.api.event;

import java.util.function.Consumer;

public interface StatusEventBus {

    void publish(StatusEvent event);

    Subscription subscribe(Consumer<StatusEvent> event);

    interface Subscription extends AutoCloseable {
        @Override
        void close();
    }

}
