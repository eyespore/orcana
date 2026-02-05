package cc.pineclone.workflow.trigger;

import cc.pineclone.workflow.api.trigger.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AnyOrderTriggerDefinition extends DefaultCompositeTriggerDefinition {

    private TriggerEventIdentity eventIdentity;
    private Map<TriggerEventIdentity, Integer> requiredCounts;
    private Set<TriggerEventIdentity> forbiddenEvents;
    private long timeoutMs;

    public static AnyOrderTriggerDefinitionBuilder builder() {
        return new AnyOrderTriggerDefinitionBuilder();
    }

    public static class AnyOrderTriggerDefinitionBuilder
            extends CompositeTriggerDefinitionBuilder<AnyOrderTriggerDefinition> {

        private TriggerEventIdentity eventIdentity;
        private final Map<TriggerEventIdentity, Integer> requiredCounts = new HashMap<>();
        private final Set<TriggerEventIdentity> forbiddenEvents = new HashSet<>();
        private long timeoutMs;

        public AnyOrderTriggerDefinitionBuilder eventIdentity(TriggerEventIdentity eventId) {
            this.eventIdentity = eventId;
            return this;
        }

        public AnyOrderTriggerDefinitionBuilder requiredCount(TriggerEventIdentity event, int count) {
            this.requiredCounts.put(event, count);
            return this;
        }

        public AnyOrderTriggerDefinitionBuilder forbidEvent(TriggerEventIdentity event) {
            this.forbiddenEvents.add(event);
            return this;
        }

        public AnyOrderTriggerDefinitionBuilder timeoutMs(long ms) {
            this.timeoutMs = ms;
            return this;
        }

        @Override
        public AnyOrderTriggerDefinition build() {
            AnyOrderTriggerDefinition def = new AnyOrderTriggerDefinition();
            def.setIdentity(identity);
            def.setEventIdentity(eventIdentity);
            def.setChildDefinitions(childDefinitions);
            def.setRequiredCounts(requiredCounts);
            def.setForbiddenEvents(forbiddenEvents);
            def.setTimeoutMs(timeoutMs);
            return def;
        }
    }
}
