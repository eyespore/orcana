package cc.pineclone.workflow.api.trigger;

import cc.pineclone.workflow.api.trigger.event.TriggerEventSink;

public interface Trigger {

    void attach(TriggerEventSink sink);

    void detach();

    TriggerIdentity getIdentity();

}
