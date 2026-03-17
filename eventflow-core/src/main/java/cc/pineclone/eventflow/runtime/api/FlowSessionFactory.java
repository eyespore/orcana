package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.context.FlowSession;
import cc.pineclone.eventflow.core.api.event.Event;

public interface FlowSessionFactory {

    FlowSession create(Event event);

}
