package cc.pineclone.automation.trigger;

import cc.pineclone.automation.common.TriggerMode;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.trigger.policy.ActivationPolicy;
import cc.pineclone.automation.trigger.source.KeyboardSource;
import cc.pineclone.automation.trigger.source.MouseButtonSource;
import cc.pineclone.automation.trigger.source.MouseScrollSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class TriggerFactory {

    /** 用来存放已创建的 CompositeTrigger，键为 id */
    private static final ConcurrentMap<TriggerIdentity, Trigger> singleTriggerRegistry = new ConcurrentHashMap<>();

    @Deprecated
    private static final ConcurrentMap<TriggerIdentityGroup, Trigger> multipleTriggerRegistry = new ConcurrentHashMap<>();

    private TriggerFactory() {}

    public static Trigger simple(TriggerIdentity identity) {
        return singleTriggerRegistry.computeIfAbsent(identity, key -> createSimpleTrigger(identity));
    }

    public static Trigger composite(TriggerIdentity... identities) {
        Set<TriggerIdentity> set = new HashSet<>(Arrays.asList(identities));  /* 去重 */
        if (set.size() == 1) return simple(set.iterator().next());
        else return new CompositeTrigger(set.stream().map(TriggerFactory::simple).collect(Collectors.toSet()));
    }

    public static Trigger union(TriggerIdentity... identities) {
        Set<TriggerIdentity> set = new HashSet<>(Arrays.asList(identities));  /* 去重 */
        if (set.size() == 1) return simple(set.iterator().next());
        return new UnionTrigger(set.stream().map(TriggerFactory::simple).collect(Collectors.toSet()));
    }

    /* 构建最小的触发器单位 */
    private static Trigger createSimpleTrigger(TriggerIdentity identity) {
        Set<Key> key = identity.getKeys();
        TriggerType type = identity.getType();
        TriggerMode mode = identity.getMode();

        long doubleClickInterval = identity.getDoubleClickInterval();

        switch (type) {
            case KEYBOARD -> {
                final KeyboardSource source = new KeyboardSource(key);
                return switch (mode) {
                    case HOLD   -> new SimpleTrigger(source, ActivationPolicy.hold());
                    case TOGGLE -> new SimpleTrigger(source, ActivationPolicy.toggle());
                    case CLICK -> new SimpleTrigger(source, ActivationPolicy.click());
                    case DOUBLE_CLICK -> new SimpleTrigger(source, ActivationPolicy.doubleClick(doubleClickInterval));
                };
            }
            case MOUSE_BUTTON -> {
                final MouseButtonSource source = new MouseButtonSource(key);
                return switch (mode) {
                    case HOLD   -> new SimpleTrigger(source, ActivationPolicy.hold());
                    case TOGGLE -> new SimpleTrigger(source, ActivationPolicy.toggle());
                    case CLICK -> new SimpleTrigger(source, ActivationPolicy.click());
                    case DOUBLE_CLICK -> new SimpleTrigger(source, ActivationPolicy.doubleClick(doubleClickInterval));
                };
            }
            case SCROLL_WHEEL -> {
                // 已在 TriggerIdentity 中保证只能与 TOGGLE 组合
                final MouseScrollSource source = new MouseScrollSource(key);
                return switch (mode) {
                    case HOLD -> throw new IllegalStateException("Unexpected value: " + mode);
                    case TOGGLE -> new SimpleTrigger(source, ActivationPolicy.toggle());
                    case CLICK -> new SimpleTrigger(source, ActivationPolicy.click());
                    case DOUBLE_CLICK -> new SimpleTrigger(source, ActivationPolicy.doubleClick(doubleClickInterval));
                };
            }
            default -> throw new IllegalArgumentException("Unsupported TriggerType: " + type);
        }
    }
}
