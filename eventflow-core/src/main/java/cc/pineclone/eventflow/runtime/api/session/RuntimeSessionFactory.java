package cc.pineclone.eventflow.runtime.api.session;

import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.runtime.api.RootTrigger;

public interface RuntimeSessionFactory {

    /* Session 直接面向某个 Trigger */
    RuntimeSession create(RootTrigger rootTrigger, Event rootEvent);

}
