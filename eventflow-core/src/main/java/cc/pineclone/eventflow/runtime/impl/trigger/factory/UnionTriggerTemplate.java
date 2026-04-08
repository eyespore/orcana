package cc.pineclone.eventflow.runtime.impl.trigger.factory;

import cc.pineclone.eventflow.config.api.PropsNormalizer;
import cc.pineclone.eventflow.config.api.PropsViewer;
import cc.pineclone.eventflow.config.api.TemplateSession;
import cc.pineclone.eventflow.runtime.api.selector.EventSelector;
import cc.pineclone.eventflow.core.api.Trigger;
import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;
import cc.pineclone.eventflow.plugin.api.template.TriggerTemplate;
import cc.pineclone.eventflow.runtime.impl.trigger.UnionTrigger;

import java.util.Map;

public class UnionTriggerTemplate implements TriggerTemplate {

    private static final String TEMPLATE_TYPE_STRING = "default:union_trigger";
    private static final String CANONICAL_KEY_EVENT_MAPPING = "eventMapping";

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

        // 兼容旧 key（如有需要可以继续补充）
        normalizer.aliasKeyStrict(CANONICAL_KEY_EVENT_MAPPING,
                "event_mapping", "event-mapping", "EVENT_MAPPING");

        // 缺省给空 Map；如果后续你希望强类型 key/value，可以换成 normalizeMap + coercer/自定义 normalizer
        normalizer.normalizeMap(CANONICAL_KEY_EVENT_MAPPING,
                EventSelector.class, EventSelector.class, Map.of());
    }

    @Override
    public void validate(TemplateSession<TriggerDefinition> session) {
        PropsViewer viewer = session.propsViewer();

        Map<EventSelector, EventSelector> mapping =
                viewer.getMap(CANONICAL_KEY_EVENT_MAPPING, EventSelector.class, EventSelector.class);

        if (mapping.isEmpty()) {
            throw new IllegalArgumentException(session.pathOf(CANONICAL_KEY_EVENT_MAPPING) + " must be non-empty");
        }
    }

    @Override
    public Trigger createInstance(TemplateSession<TriggerDefinition> session) {
        PropsViewer viewer = session.propsViewer();

        Map<EventSelector, EventSelector> mapping =
                viewer.getMap(CANONICAL_KEY_EVENT_MAPPING, EventSelector.class, EventSelector.class);

        return new UnionTrigger(
                session.rawDefinition().identity(),
                mapping
        );
    }
}
