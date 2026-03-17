package cc.pineclone.eventflow.core.api.event;

import cc.pineclone.eventflow.core.api.ComponentId;

import java.util.Map;

public interface Event {

    ComponentId source();

    String type();

    long timestamp();

    Map<String, Object> meta();

}
