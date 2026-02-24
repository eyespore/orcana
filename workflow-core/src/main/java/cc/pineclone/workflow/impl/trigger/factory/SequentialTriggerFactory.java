package cc.pineclone.workflow.impl.trigger.factory;

import cc.pineclone.workflow.api.trigger.Trigger;
import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.impl.trigger.SequentialTrigger;

import java.util.ArrayList;
import java.util.HashSet;

public class SequentialTriggerFactory extends DefaultCompositeTriggerFactory<SequentialTriggerDefinition> {

    private static final String DEF_TRIGGER_EVENT_IDENTITY_TYPE = "SEQUENTIAL_TRIGGER_ACTIVE";

    @Override
    public Class<SequentialTriggerDefinition> definitionType() {
        return SequentialTriggerDefinition.class;
    }

    @Override
    protected SequentialTriggerDefinition normalize(SequentialTriggerDefinition definition) {
        SequentialTriggerDefinition def = super.normalize(definition);

        TriggerEventIdentity eventIdentity = def.getEventIdentity();

        if (eventIdentity == null) {
            TriggerIdentity identity = def.getIdentity();
            def.setEventIdentity(new TriggerEventIdentity(
                    identity.domain(), identity.name(), DEF_TRIGGER_EVENT_IDENTITY_TYPE
            ));  /* 默认事件类型 */
        }

        /* 事件序列检查 */
        if (def.getSequentialEvents() == null) {
            def.setSequentialEvents(new ArrayList<>());
        }

        /* 禁用事件列表检查 */
        if (def.getForbiddenEvents() == null) {
            def.setForbiddenEvents(new HashSet<>());
        }

        long timeoutMs = def.getTimeoutMs();
        if (timeoutMs <= 0) def.setTimeoutMs(3000);  /* 超时默认值 */

        return def;
    }

    @Override
    protected Trigger doCreate(SequentialTriggerDefinition definition) {
        return new SequentialTrigger(
                definition.getIdentity(),
                definition.getEventIdentity(),
                definition.getSequentialEvents(),
                definition.getForbiddenEvents(),
                definition.getTimeoutMs()
        );
    }
}
