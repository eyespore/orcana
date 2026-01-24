package cc.pineclone.automation.trigger.source;

import cc.pineclone.automation.AutomationJobEvent;
import cc.pineclone.automation.AutomationJobEventListener;
import lombok.Setter;

public abstract class InputSource implements AutomationJobEventListener {

    /* 是否已经安装了监听器，避免监听器重复安装 */
    private volatile boolean installed = false;
    @Setter protected InputSourceListener listener;

    protected abstract void init();  /* 初始化 */
    protected abstract void stop();  /* 清理资源 */

    @Override
    public void onAutomationLaunch(AutomationJobEvent event) {
        if (installed) return;
        init();
        installed = true;
    }

    @Override
    public void onAutomationTerminate(AutomationJobEvent event) {
        if (!installed) return;
        stop();
        installed = false;
    }

    protected void fire(JNativeHookInputSourceEvent event) {
        if (listener != null) {
            listener.onInputSourceEvent(event);
        }
    }
}
