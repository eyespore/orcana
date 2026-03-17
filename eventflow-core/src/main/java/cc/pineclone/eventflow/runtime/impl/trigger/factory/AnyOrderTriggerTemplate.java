package cc.pineclone.eventflow.runtime.impl.trigger.factory;

import cc.pineclone.eventflow.config.api.PropsNormalizer;
import cc.pineclone.eventflow.config.api.PropsViewer;
import cc.pineclone.eventflow.config.api.TemplateSession;
import cc.pineclone.eventflow.core.api.trigger.Trigger;
import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;
import cc.pineclone.eventflow.plugin.api.template.TriggerTemplate;
import cc.pineclone.eventflow.runtime.impl.trigger.AnyOrderTrigger;

import java.util.*;

public class AnyOrderTriggerTemplate implements TriggerTemplate {

    private static final String TEMPLATE_TYPE_STRING = "default:any_order_trigger";
    private static final String DEF_EVENT_TYPE = "ANY_ORDER_TRIGGER_ACTIVE";

    private static final String CANONICAL_KEY_EVENT_TYPE = "eventType";
    private static final String CANONICAL_KEY_REQUIRED_COUNTS = "requiredCounts";
    private static final String CANONICAL_KEY_FORBIDDEN_EVENTS = "forbiddenEvents";
    private static final String CANONICAL_KEY_TIMEOUT_MS = "timeoutMs";

    @Override
    public String type() {
        return TEMPLATE_TYPE_STRING;
    }

    @Override
    public ChildrenPolicy childPolicy() {
        return TriggerTemplate.ChildrenPolicy.atLeast(1);
    }

    @Override
    public void normalize(TemplateSession<TriggerDefinition> session) {
        PropsNormalizer normalizer = session.propsNormalizer();

        /* 事件类型 */
        normalizer.aliasKeyStrict(CANONICAL_KEY_EVENT_TYPE,
                "event_type", "event-type", "EVENT_TYPE");

        /* 对不同事件类型的要求次数 */
        normalizer.aliasKeyStrict(CANONICAL_KEY_REQUIRED_COUNTS,
                "required_counts", "required_counts", "REQUIRED_COUNT");

        /* 超时时间 */
        normalizer.aliasKeyStrict(CANONICAL_KEY_TIMEOUT_MS,
                "timeoutMs", "timeoutMs", "TIMEOUT_MS");

        /* 禁用事件 */
        normalizer.aliasKeyStrict(CANONICAL_KEY_FORBIDDEN_EVENTS,
                "forbidden_events", "forbidden_events", "FORBIDDEN_EVENTS");

        // 1) eventType 归一化
        normalizer.normalize(CANONICAL_KEY_EVENT_TYPE, String.class, DEF_EVENT_TYPE);

        // 2) requiredCounts 归一化，缺省给空 Map，允许 Map<String/Number/String, TriggerEventKey>
        normalizer.normalizeMap(CANONICAL_KEY_REQUIRED_COUNTS, EventSelector.class, Integer.class, Map.of());

        // 3) forbiddenEvents：补齐 Set
        normalizer.normalizeSet(CANONICAL_KEY_FORBIDDEN_EVENTS, EventSelector.class, Set.of());

        // 4) timeoutMs：缺省补默认值；统一为 long
        normalizer.normalize(CANONICAL_KEY_TIMEOUT_MS, Long.class, 2000L);
    }

    @Override
    public void validate(TemplateSession<TriggerDefinition> session) {
        PropsViewer viewer = session.propsViewer();

        long timeoutMs = viewer.get(CANONICAL_KEY_TIMEOUT_MS, Long.class);
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException(
                    session.pathOf(CANONICAL_KEY_TIMEOUT_MS) +
                            "properties.timeoutMs must be > 0");
        }

        // 业务层面的小一致性检查（可选）：requiredCounts 和 forbiddenEvents 交叉冲突
        Map<EventSelector, Integer> requiredCounts = viewer.getMap(CANONICAL_KEY_REQUIRED_COUNTS, EventSelector.class, Integer.class);
        Set<EventSelector> forbiddenEvents = viewer.getSet(CANONICAL_KEY_FORBIDDEN_EVENTS, EventSelector.class);
        for (EventSelector key : requiredCounts.keySet()) {
            if (forbiddenEvents.contains(key)) {
                throw new IllegalArgumentException("Event '" + key + "' cannot be both required and forbidden");
            }
        }
    }

    @Override
    public Trigger createInstance(TemplateSession<TriggerDefinition> session) {
        PropsViewer viewer = session.propsViewer();

        String eventType = viewer.get(CANONICAL_KEY_EVENT_TYPE, String.class);
        Map<EventSelector, Integer> requiredCounts = viewer.getMap(CANONICAL_KEY_REQUIRED_COUNTS, EventSelector.class, Integer.class);
        Set<EventSelector> forbiddenEvents = viewer.getSet(CANONICAL_KEY_FORBIDDEN_EVENTS, EventSelector.class);
        long timeoutMs = viewer.get(CANONICAL_KEY_TIMEOUT_MS, Long.class);

        return new AnyOrderTrigger(
                session.rawDefinition().identity(),
                eventType,
                requiredCounts,
                forbiddenEvents,
                timeoutMs
        );
    }
}
