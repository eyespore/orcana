package cc.pineclone.eventflow.runtime.impl.trigger.factory;

import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.config.api.CompositeTriggerDefinitionBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class SequentialTriggerDefinition extends DefaultCompositeTriggerDefinition {

    private EventSelector eventIdentity;
    private List<EventSelector> sequentialEvents;
    private Set<EventSelector> forbiddenEvents;
    private long timeoutMs;

    public static SequentialTriggerDefinitionBuilder builder() {
        return new SequentialTriggerDefinitionBuilder();
    }

    public static class SequentialTriggerDefinitionBuilder
            extends CompositeTriggerDefinitionBuilder<SequentialTriggerDefinition> {

        private EventSelector eventIdentity;
        private final List<EventSelector> sequentialEvents = new ArrayList<>();
        private final Set<EventSelector> forbiddenEvents = new HashSet<>();
        private long timeoutMs;

        public SequentialTriggerDefinitionBuilder eventIdentity(EventSelector eventId) {
            this.eventIdentity = eventId;
            return this;
        }

        public SequentialTriggerDefinitionBuilder sequentialEvent(EventSelector event) {
            this.sequentialEvents.add(event);
            return this;
        }

        public SequentialTriggerDefinitionBuilder forbidEvent(EventSelector event) {
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
            def.setChildren(childDefinitions);
            def.setSequentialEvents(sequentialEvents);
            def.setForbiddenEvents(forbiddenEvents);
            def.setTimeoutMs(timeoutMs);
            return def;
        }
    }
}
