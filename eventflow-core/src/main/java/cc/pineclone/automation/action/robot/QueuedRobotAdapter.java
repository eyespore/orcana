package cc.pineclone.automation.action.robot;

import cc.pineclone.automation.input.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单消息队列机器人装饰器
 */
@Deprecated
public class QueuedRobotAdapter extends VCRobotAdapter {

    private final VCRobotAdapter delegate;
    private final ExecutorService queue = Executors.newSingleThreadExecutor();
    private final Logger log = LoggerFactory.getLogger(getClass());

    public QueuedRobotAdapter(VCRobotAdapter delegate) {
        super(delegate.robot);
        this.delegate = delegate;
    }

    @Override
    public void simulate(Key key) {
        queue.submit(() -> {
            try {
                delegate.simulate(key);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
