package cc.pineclone.workflow.api.trigger;

import cc.pineclone.workflow.api.action.ActionIdentity;

public interface TriggerEventRouter {

    void route(TriggerEvent event);

    void addRoute(TriggerEventIdentity from, ActionIdentity to);

    void delRoute(TriggerEventIdentity from, ActionIdentity to);

}
