package cc.pineclone.eventflow.runtime.impl.trigger.factory;

import cc.pineclone.eventflow.core.api.binding.EventSelector;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Deprecated
@EqualsAndHashCode(callSuper = true)
public class AnyOrderTriggerDefinition extends DefaultCompositeTriggerDefinition {

    private EventSelector eventIdentity;
    private Map<EventSelector, Integer> requiredCounts;
    private Set<EventSelector> forbiddenEvents;
    private long timeoutMs;

}
