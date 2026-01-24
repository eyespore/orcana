package cc.pineclone.automation.action;

import cc.pineclone.automation.AutomationJobEvent;

public class ActionDecorator extends Action {

    protected final Action delegate;

    public ActionDecorator(final Action delegate) {
        super(delegate.actionId);
        this.delegate = delegate;
    }

    @Override
    public void activate(AutomationJobEvent event) {
        delegate.activate(event);
    }

    @Override
    public void deactivate(AutomationJobEvent event) {
        delegate.deactivate(event);
    }
}
