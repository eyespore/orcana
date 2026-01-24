package cc.pineclone.automation;

public interface MacroLifecycleAware {

    /**
     * 该方法会在Macro被注册时调用，通常是用户在GTA OPS中启用了某一项功能的时候
     * 对于某个Action，可以用这个方法来注册Action内部维护的子宏
     */
    default void onMacroLaunch(MacroEvent event) {}

    /**
     * 该方法会在Macro被注销时调用，通常是用户在GTA OPS中关闭了某一项功能的时候
     * 对于某个Action，如果其内部维护了一个子宏，应当用这个方法将子宏一并注销
     */
    default void onMacroTerminate(MacroEvent event) {}

    /**
     * 挂起当前ACTION，如果你使用子ACTION，那么不应该通过这个方法来控制子ACTION，该方法仅随MacroRegistry被调用
     * 在子ACTION中可能会导致和实际逻辑相悖
     */
    default void onMacroSuspend(MacroEvent event) {}

    /**
     * 唤醒当前ACTION，如果你使用子ACTION，那么不应该通过这个方法来控制子ACTION，该方法仅随MacroRegistry被调用
     * 在子ACTION中可能会导致和实际逻辑相悖
     */
    default void onMacroResume(MacroEvent event) {}

}
