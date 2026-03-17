package cc.pineclone.eventflow.runtime.impl.trigger.factory;

import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.config.api.CompositeTriggerDefinitionBuilder;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class UnionTriggerDefinition extends DefaultCompositeTriggerDefinition {

    private Map<EventSelector, EventSelector> eventMapping;

    public static SequentialTriggerDefinitionBuilder builder() {
        return new SequentialTriggerDefinitionBuilder();
    }

    public static class SequentialTriggerDefinitionBuilder
            extends CompositeTriggerDefinitionBuilder<UnionTriggerDefinition> {

        private final Map<EventSelector, EventSelector> eventMapping = new HashMap<>();

        public SequentialTriggerDefinitionBuilder mapEvent(EventSelector from, EventSelector to) {
            this.eventMapping.put(from, to);
            return this;
        }

        @Override
        public UnionTriggerDefinition build() {
            UnionTriggerDefinition def = new UnionTriggerDefinition();
            def.setIdentity(identity);
            def.setChildren(childDefinitions);
            def.setEventMapping(eventMapping);
            return def;
        }
    }
}
