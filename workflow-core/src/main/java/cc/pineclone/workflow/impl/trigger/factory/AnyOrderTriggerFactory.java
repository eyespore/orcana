package cc.pineclone.workflow.impl.trigger.factory;

import cc.pineclone.workflow.api.trigger.Trigger;
import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.impl.trigger.AnyOrderTrigger;

import java.util.HashMap;
import java.util.HashSet;

public class AnyOrderTriggerFactory
        extends DefaultCompositeTriggerFactory<AnyOrderTriggerDefinition> {

    private static final String DEF_TRIGGER_EVENT_IDENTITY_TYPE = "ANY_ORDER_TRIGGER_ACTIVE";

    @Override
    public Class<AnyOrderTriggerDefinition> definitionType() {
        return AnyOrderTriggerDefinition.class;
    }

    @Override
    protected AnyOrderTriggerDefinition normalize(AnyOrderTriggerDefinition definition) {
        AnyOrderTriggerDefinition def = super.normalize(definition);

        /* 事件类型检查 */
        TriggerEventIdentity eventIdentity = def.getEventIdentity();
        if (eventIdentity == null) {
            TriggerIdentity identity = def.getIdentity();
            def.setEventIdentity(new TriggerEventIdentity(
                    identity.domain(), identity.name(), DEF_TRIGGER_EVENT_IDENTITY_TYPE
            ));
        }

        /* 事件列表检查 */
        if (def.getRequiredCounts() == null) {
            definition.setRequiredCounts(new HashMap<>());
        }

        /* 禁用事件列表检查 */
        if (def.getForbiddenEvents() == null) {
            def.setForbiddenEvents(new HashSet<>());
        }

        /* 超时默认值检查 */
        long timeoutMs = def.getTimeoutMs();
        if (timeoutMs <= 0) def.setTimeoutMs(2000);

        return def;
    }

    @Override
    protected Trigger doCreate(AnyOrderTriggerDefinition definition) {
        return new AnyOrderTrigger(
                definition.getIdentity(),
                definition.getEventIdentity(),
                definition.getRequiredCounts(),
                definition.getForbiddenEvents(),
                definition.getTimeoutMs()
        );
    }
}
