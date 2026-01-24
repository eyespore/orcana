package cc.pineclone.automation.action;


import cc.pineclone.automation.MacroEvent;

/* 动作生命周期 */
public interface ActionLifecycle {

    /**
     * 执行动作之前，可通过返回值来控制是否放行当前动作
     */
    default boolean beforeActivate(MacroEvent event) {
        return true;
    }

    default void activate(MacroEvent event) {}

    /**
     * 动作执行之后，执行后处理工作
     */
    default void afterActivate(MacroEvent event) {}

    /**
     * 动作撤销之前，可通过返回值来控制是否撤销当前动作
     */
    default boolean beforeDeactivate(MacroEvent event) {
        return true;
    }

    /**
     * 动作撤销之后，执行撤销之后的后处理工作
     */
    default void afterDeactivate(MacroEvent event) {}

    /**
     * 由于此方法不仅会在宏显式关闭时调用，也会在宏被挂起时调用，如果某个Action在撤销阶段会触发
     * 按键操作，那么应该确保不要在挂起时调用，即MacroExecutionStatus为ACTIVE时才触发调用
     */
    default void deactivate(MacroEvent event) {}

}
