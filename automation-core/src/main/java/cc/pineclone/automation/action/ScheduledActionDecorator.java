package cc.pineclone.automation.action;

import cc.pineclone.automation.AutomationJobEvent;

public class ScheduledActionDecorator extends ScheduledAction {

    protected final ScheduledAction delegate;

    public ScheduledActionDecorator(final ScheduledAction delegate) {
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
