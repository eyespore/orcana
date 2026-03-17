package cc.pineclone.eventflow.runtime.api.event;

import java.util.Map;

public interface StatusEvent {

    StatusSubjectType subjectType();

    String subjectId();

    String type();

    long timestamp();

    Map<String, Object> meta();

}
