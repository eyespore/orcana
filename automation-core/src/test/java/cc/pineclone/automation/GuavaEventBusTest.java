package cc.pineclone.automation;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuavaEventBusTest {

    private final static Logger log = LoggerFactory.getLogger(GuavaEventBusTest.class);

    @Test
    public void testEventBus() {
        EventBus eventBus = new EventBus("test-bus");
        eventBus.register(new EventBusSubscriber());
        eventBus.post(new TestEvent("Hello World"));
    }

    private static class EventBusSubscriber {
        @Subscribe
        public void handleEvent(TestEvent event) {
            log.debug(event.key);
        }
    }

    private record TestEvent(String key) {
    }

}
