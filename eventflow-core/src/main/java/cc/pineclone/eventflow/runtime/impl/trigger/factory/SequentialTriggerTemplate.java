package cc.pineclone.eventflow.runtime.impl.trigger.factory;

import cc.pineclone.eventflow.config.api.PropsNormalizer;
import cc.pineclone.eventflow.config.api.PropsViewer;
import cc.pineclone.eventflow.config.api.TemplateSession;
import cc.pineclone.eventflow.core.api.trigger.Trigger;
import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;
import cc.pineclone.eventflow.plugin.api.template.TriggerTemplate;
import cc.pineclone.eventflow.runtime.impl.trigger.SequentialTrigger;

import java.util.*;

public class SequentialTriggerTemplate implements TriggerTemplate {

    private static final String TEMPLATE_TYPE_STRING = "default:sequential_trigger";
    private static final String DEF_EVENT_TYPE = "SEQUENTIAL_TRIGGER_ACTIVE";

    private static final String CANONICAL_KEY_EVENT_TYPE = "eventType";
    private static final String CANONICAL_KEY_SEQUENTIAL_COUNTS = "sequentialCounts";
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

        normalizer.aliasKeyStrict(CANONICAL_KEY_EVENT_TYPE,
                "event_identity", "event-identity", "EVENT_IDENTITY");

        // 新：sequentialCounts（推荐）
        // 旧：sequentialEvents（List）不做兼容迁移（否则得写 List->Map 的转换逻辑）
        normalizer.aliasKeyStrict(CANONICAL_KEY_SEQUENTIAL_COUNTS,
                "sequential_counts", "sequential-counts", "SEQUENTIAL_COUNTS");

        normalizer.aliasKeyStrict(CANONICAL_KEY_FORBIDDEN_EVENTS,
                "forbidden_events", "forbidden-events", "FORBIDDEN_EVENTS");

        normalizer.aliasKeyStrict(CANONICAL_KEY_TIMEOUT_MS,
                "timeout_ms", "timeout-ms", "TIMEOUT_MS");

        normalizer.normalize(CANONICAL_KEY_EVENT_TYPE, String.class, DEF_EVENT_TYPE);
        normalizer.normalizeMap(CANONICAL_KEY_SEQUENTIAL_COUNTS, EventSelector.class, Integer.class, Map.of());
        normalizer.normalizeSet(CANONICAL_KEY_FORBIDDEN_EVENTS, EventSelector.class, Set.of());
        normalizer.normalize(CANONICAL_KEY_TIMEOUT_MS, Long.class, 3000L);
    }
    @Override
    public void validate(TemplateSession<TriggerDefinition> session) {
        PropsViewer viewer = session.propsViewer();

        /* 超时时间校验 */
        long timeoutMs = viewer.get(CANONICAL_KEY_TIMEOUT_MS, Long.class);
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException(session.pathOf(CANONICAL_KEY_TIMEOUT_MS) + " must be > 0");
        }

        Map<EventSelector, Integer> sequentialCounts =
                viewer.getMap(CANONICAL_KEY_SEQUENTIAL_COUNTS, EventSelector.class, Integer.class);

        if (sequentialCounts.isEmpty()) {
            throw new IllegalArgumentException(session.pathOf(CANONICAL_KEY_SEQUENTIAL_COUNTS) + " must be non-empty");
        }
        if (sequentialCounts.containsKey(null)) {
            throw new IllegalArgumentException(session.pathOf(CANONICAL_KEY_SEQUENTIAL_COUNTS) + " must not contain null key");
        }
        if (sequentialCounts.values().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(session.pathOf(CANONICAL_KEY_SEQUENTIAL_COUNTS) + " must not contain null value");
        }
        for (Map.Entry<EventSelector, Integer> e : sequentialCounts.entrySet()) {
            if (e.getValue() <= 0) {
                throw new IllegalArgumentException(
                        session.pathOf(CANONICAL_KEY_SEQUENTIAL_COUNTS) + " count must be > 0 for key=" + e.getKey());
            }
        }

        Set<EventSelector> forbiddenEvents =
                viewer.getSet(CANONICAL_KEY_FORBIDDEN_EVENTS, EventSelector.class);
        if (forbiddenEvents.contains(null)) {
            throw new IllegalArgumentException(session.pathOf(CANONICAL_KEY_FORBIDDEN_EVENTS) + " must not contain null");
        }

        /* sequentialCounts 与 forbiddenEvents 不允许出现交集 */
        if (!Collections.disjoint(sequentialCounts.keySet(), forbiddenEvents)) {
            Set<EventSelector> overlap = new HashSet<>(sequentialCounts.keySet());
            overlap.retainAll(forbiddenEvents);
            throw new IllegalArgumentException("Events " + overlap + " cannot be both sequentialCounts and forbiddenEvents");
        }
    }

    @Override
    public Trigger createInstance(TemplateSession<TriggerDefinition> session) {
        PropsViewer viewer = session.propsViewer();

        String eventType = viewer.get(CANONICAL_KEY_EVENT_TYPE, String.class);
        Map<EventSelector, Integer> sequentialCounts =
                viewer.getMap(CANONICAL_KEY_SEQUENTIAL_COUNTS, EventSelector.class, Integer.class);
        Set<EventSelector> forbiddenEvents =
                viewer.getSet(CANONICAL_KEY_FORBIDDEN_EVENTS, EventSelector.class);
        long timeoutMs = viewer.get(CANONICAL_KEY_TIMEOUT_MS, Long.class);

        return new SequentialTrigger(
                session.rawDefinition().identity(),
                eventType,
                sequentialCounts,
                forbiddenEvents,
                timeoutMs
        );
    }
}
