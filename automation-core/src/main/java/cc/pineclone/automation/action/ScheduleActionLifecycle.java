package cc.pineclone.automation.action;

import cc.pineclone.automation.AutomationJobEvent;

public interface ScheduleActionLifecycle extends ActionLifecycle {

    /**
     * 循环具体逻辑，该循环逻辑基于ScheduledExecutorService.scheduleAtFixedRate()方法执行
     */
    default void schedule(AutomationJobEvent event) {}

    /**
     * 每次循环之前，可通过返回boolean来决定是否放行执行之后的schedule
     */
    default boolean beforeSchedule(AutomationJobEvent event) {
        return true;
    }

    /**
     * 每次循环之后，执行后处理工作
     */
    default void afterSchedule(AutomationJobEvent event) {}

}
