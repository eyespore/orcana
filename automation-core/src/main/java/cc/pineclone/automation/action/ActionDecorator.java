package cc.pineclone.automation.action;

import cc.pineclone.automation.MacroEvent;

public class ActionDecorator extends Action {

    protected final Action delegate;

    public ActionDecorator(final Action delegate) {
        super(delegate.actionId);
        this.delegate = delegate;
    }

    @Override
    public void activate(MacroEvent event) {
        delegate.activate(event);
    }

    @Override
    public void deactivate(MacroEvent event) {
        delegate.deactivate(event);
    }
}
