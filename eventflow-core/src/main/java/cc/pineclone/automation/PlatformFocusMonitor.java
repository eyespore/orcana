package cc.pineclone.automation;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/* 平台焦点监听 */
public class PlatformFocusMonitor {

    private final ScheduledExecutorService scheduler;
    private String lastTitle;
    private final Set<WindowTitleListener> listeners = new HashSet<>();
    private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    public PlatformFocusMonitor() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "platform-focus-scheduler");
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {  /* 启动平台焦点监听 */
//        System.load(loadDll().toAbsolutePath().toString());  /* 加载本地库 */
        loadDll();  /* 加载本地库 */
        log.info("Register platform focus monitor, ensure the macro can only be activate while target app is focusing");
        scheduler.scheduleAtFixedRate(this::poll, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private void poll() {
        try {
            String title = getForegroundWindowTitle();
            if (title == null || title.isBlank()) return;
            if (!title.equals(lastTitle)) {
                lastTitle = title;
                listeners.forEach(listener -> listener.accept(title));
            }
        } catch (UnsatisfiedLinkError e) {
            log.error(e.getMessage(), e);
        } catch (Throwable t) {
            log.warn(t.getMessage(), t);
        }
    }

    public void addListener(WindowTitleListener listener) {
        listeners.add(listener);
        if (lastTitle != null && !lastTitle.isBlank()) {  // 若上一次记录标题不为空，在添加监听器时立即触发一次执行
            listener.accept(lastTitle);
        }
    }

    public void removeListener(WindowTitleListener listener) {
        listeners.remove(listener);
    }

    private native String getForegroundWindowTitle();

    private void loadDll() {
        String libName = System.getProperty("automation.lib.name", "PlatformFocusMonitor");  /* 尝试加载 */

        try {
            System.loadLibrary(libName);
        } catch (UnsatisfiedLinkError e) {


            try {
//                String libLoader = System.getProperty("automation.lib.locator", DefaultLibraryLocator.class.getCanonicalName());
//                NativeLibraryLocator locator = Class.forName(libLoader).asSubclass(NativeLibraryLocator.class).getDeclaredConstructor().newInstance();

                NativeLibraryLocator locator = new NativeLibraryLocator() {
                    @Override
                    protected String getLibName() {
                        return "PlatformFocusMonitor";
                    }
                };

                Iterator<File> libs = locator.getLibraries();

                while(libs.hasNext()) {
                    File lib = libs.next();
                    if (lib.exists() && lib.isFile() && lib.canRead()) {
                        System.load(lib.getPath());
                    }
                }
            } catch (Exception ee) {
                log.error(ee.getMessage());
                throw new UnsatisfiedLinkError(ee.getMessage());
            }
        }
    }
}
