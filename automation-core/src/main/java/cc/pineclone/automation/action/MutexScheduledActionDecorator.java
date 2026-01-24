package cc.pineclone.automation.action;

import cc.pineclone.automation.AutomationJobEvent;

/* todo: 引入动作优先级 */
/* 优先级互斥定时动作装饰器 */
@Deprecated
public class MutexScheduledActionDecorator extends ScheduledAction  {

    protected final ScheduledAction delegate;

    public MutexScheduledActionDecorator(final ScheduledAction delegate) {
        super(delegate.getActionId(), delegate.getInterval());
        this.delegate = delegate;
    }

    @Override
    public void schedule(AutomationJobEvent event) {
        delegate.schedule(event);
    }

    @Override
    public boolean beforeSchedule(AutomationJobEvent event) {
        return delegate.beforeSchedule(event);
    }

    @Override
    public void afterSchedule(AutomationJobEvent event) {
        delegate.afterSchedule(event);
    }


}
