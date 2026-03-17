package cc.pineclone.automation;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformFocusMonitorTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void testPlatformFocusMonitoring() throws InterruptedException {
        PlatformFocusMonitor monitor = new PlatformFocusMonitor();
        monitor.addListener(log::debug);
        monitor.start();
        Thread.sleep(5000);
    }

}
