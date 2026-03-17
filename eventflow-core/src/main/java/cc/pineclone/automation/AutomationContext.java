package cc.pineclone.automation;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

/**
 * 宏上下文类
 */
public class AutomationContext {

    private static volatile AutomationContext INSTANCE;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final PlatformFocusMonitor platformFocusMonitor;  /* 平台焦点监听 */
    private final MacroRegistry macroRegistry;  /* 宏注册中心 */
    private final MacroFactory macroFactory;  /* 宏创建工厂 */

    @Getter private final MacroTaskScheduler scheduler;

    private AutomationContext() {
        this.scheduler = new MacroTaskScheduler();
        this.platformFocusMonitor = new PlatformFocusMonitor();
        this.macroFactory = new MacroFactory();
        this.macroRegistry = new MacroRegistry(platformFocusMonitor);
    }

    public static AutomationContext getInstance() {
        if (INSTANCE == null) {
            synchronized (AutomationContext.class) {
                if (INSTANCE == null) INSTANCE = new AutomationContext();
            }
        }
        return INSTANCE;
    }

    public void init() {
    }

    public void start() {
        platformFocusMonitor.start();
    }

    public void stop() {
        scheduler.shutdown();  /* 停止任务调度 */
        platformFocusMonitor.stop();  /* 停止平台焦点监听 */
    }

    public static void main(String[] args) throws InterruptedException {
        AutomationContext context = AutomationContext.getInstance();
        context.start();

        Thread.sleep(100000);
    }
}
