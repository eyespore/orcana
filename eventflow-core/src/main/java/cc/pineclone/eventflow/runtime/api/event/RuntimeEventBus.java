package cc.pineclone.eventflow.runtime.api.event;

import java.util.function.Consumer;

public interface RuntimeEventBus {

    void publish(RuntimeEvent event);

    Subscription subscribe(Consumer<RuntimeEvent> event);

    interface Subscription extends AutoCloseable {
        @Override
        void close();
    }

}
