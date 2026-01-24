package cc.pineclone.automation;

public interface AutomationJobEventListener {

    /**
     * 该方法会在Macro被注册时调用，通常是用户在GTA OPS中启用了某一项功能的时候
     * 对于某个Action，可以用这个方法来注册Action内部维护的子宏
     */
    default void onAutomationLaunch(AutomationJobEvent event) {}

    /**
     * 该方法会在Macro被注销时调用，通常是用户在GTA OPS中关闭了某一项功能的时候
     * 对于某个Action，如果其内部维护了一个子宏，应当用这个方法将子宏一并注销
     */
    default void onAutomationTerminate(AutomationJobEvent event) {}


    default void onAutomationJobEvent(AutomationJobEvent event) {}

}
