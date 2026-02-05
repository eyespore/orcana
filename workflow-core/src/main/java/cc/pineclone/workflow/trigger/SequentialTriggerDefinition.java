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
public class SequentialTriggerDefinition extends DefaultCompositeTriggerDefinition {

    private TriggerEventIdentity eventIdentity;
    private List<TriggerEventIdentity> sequentialEvents;
    private Set<TriggerEventIdentity> forbiddenEvents;
    private long timeoutMs;

    public static SequentialTriggerDefinitionBuilder builder() {
        return new SequentialTriggerDefinitionBuilder();
    }

    public static class SequentialTriggerDefinitionBuilder
            extends CompositeTriggerDefinitionBuilder<SequentialTriggerDefinition> {

        private TriggerEventIdentity eventIdentity;
        private final List<TriggerEventIdentity> sequentialEvents = new ArrayList<>();
        private final Set<TriggerEventIdentity> forbiddenEvents = new HashSet<>();
        private long timeoutMs;

        public SequentialTriggerDefinitionBuilder eventIdentity(TriggerEventIdentity eventId) {
            this.eventIdentity = eventId;
            return this;
        }

        public SequentialTriggerDefinitionBuilder sequentialEvent(TriggerEventIdentity event) {
            this.sequentialEvents.add(event);
            return this;
        }

        public SequentialTriggerDefinitionBuilder forbidEvent(TriggerEventIdentity event) {
            this.forbiddenEvents.add(event);
            return this;
        }

        public SequentialTriggerDefinitionBuilder timeoutMs(long ms) {
            this.timeoutMs = ms;
            return this;
        }

        @Override
        public SequentialTriggerDefinition build() {
            SequentialTriggerDefinition def = new SequentialTriggerDefinition();
            def.setIdentity(identity);
            def.setEventIdentity(eventIdentity);
            def.setChildDefinitions(childDefinitions);
            def.setSequentialEvents(sequentialEvents);
            def.setForbiddenEvents(forbiddenEvents);
            def.setTimeoutMs(timeoutMs);
            return def;
        }
    }
}
