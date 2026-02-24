package cc.pineclone.workflow.impl.trigger.factory;

import cc.pineclone.workflow.api.trigger.Trigger;
import cc.pineclone.workflow.impl.trigger.UnionTrigger;

import java.util.HashMap;

public class UnionTriggerFactory extends DefaultCompositeTriggerFactory<UnionTriggerDefinition> {

    @Override
    public Class<UnionTriggerDefinition> definitionType() {
        return UnionTriggerDefinition.class;
    }

    @Override
    protected UnionTriggerDefinition normalize(UnionTriggerDefinition definition) {
        UnionTriggerDefinition def = super.normalize(definition);

        /* 事件映射检查 */
        if (def.getEventMapping() == null) {
            def.setEventMapping(new HashMap<>());
        }

        return def;
    }

    @Override
    protected Trigger doCreate(UnionTriggerDefinition definition) {
        return new UnionTrigger(
                definition.getIdentity(),
                definition.getEventMapping()
        );
    }
}
