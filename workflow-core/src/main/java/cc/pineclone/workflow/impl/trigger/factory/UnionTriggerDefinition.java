package cc.pineclone.workflow.impl.trigger.factory;

import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.factory.CompositeTriggerDefinitionBuilder;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UnionTriggerDefinition extends DefaultCompositeTriggerDefinition {

    private Map<TriggerEventIdentity, TriggerEventIdentity> eventMapping;

    public static SequentialTriggerDefinitionBuilder builder() {
        return new SequentialTriggerDefinitionBuilder();
    }

    public static class SequentialTriggerDefinitionBuilder
            extends CompositeTriggerDefinitionBuilder<UnionTriggerDefinition> {

        private final Map<TriggerEventIdentity, TriggerEventIdentity> eventMapping = new HashMap<>();

        public SequentialTriggerDefinitionBuilder mapEvent(TriggerEventIdentity from, TriggerEventIdentity to) {
            this.eventMapping.put(from, to);
            return this;
        }

        @Override
        public UnionTriggerDefinition build() {
            UnionTriggerDefinition def = new UnionTriggerDefinition();
            def.setIdentity(identity);
            def.setChildDefinitions(childDefinitions);
            def.setEventMapping(eventMapping);
            return def;
        }
    }
}
