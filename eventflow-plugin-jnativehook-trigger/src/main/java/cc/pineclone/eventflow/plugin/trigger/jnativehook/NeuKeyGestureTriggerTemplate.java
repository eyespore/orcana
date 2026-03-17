package cc.pineclone.eventflow.plugin.trigger.jnativehook;

import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.*;
import cc.pineclone.eventflow.interaction.NeuKeySpec;
import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.api.ClickEdge;
import cc.pineclone.eventflow.interaction.api.Gesture;
import cc.pineclone.eventflow.core.api.trigger.Trigger;
import cc.pineclone.eventflow.core.api.binding.EventSelector;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.plugin.api.template.TriggerTemplate;
import cc.pineclone.eventflow.trigger.jnativehook.api.*;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.filter.NeuKeySpecFilter;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.gesture.ClickGesture;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.gesture.GestureDefinition;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.gesture.HoldGesture;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

public class NeuKeyGestureTriggerTemplate implements TriggerTemplate {

    private static final String TEMPLATE_TYPE_STRING = "jnativehook:neu_key_gesture_trigger";

    private static final String CLICK_GESTURE_EDGE = "edge";
    private static final String CLICK_GESTURE_CLICK_THRESHOLD_MS = "clickThresholdMs";
    private static final String CLICK_GESTURE_MAX_CLICK_COUNT = "maxClickCount";
    private static final String CLICK_GESTURE_DEBOUNCE_MS = "debounceMs";

    private static final String HOLD_GESTURE_HOLD_THRESHOLD_MS = "holdThresholdMs";

    private final ScheduledExecutorService scheduler;
    private final NativeInputEventSource<NativeKeyEvent> source;

    private SpecFilter<NeuKeySpec, JNativeHookKeySpec, NeuModifierConstraint, JNativeHookModifierConstraint, NativeKeyEvent>
    createFilter(SpecFilterDefinition<NeuKeySpec, NeuModifierConstraint> def) {

        NeuKeySpec scope = def.getOriginalSpec();
        NeuModifierConstraint modifier = def.getOriginalModifier();
        return new NeuKeySpecFilter(scope, modifier);
    }

    private GestureInterpreter<NativeKeyEvent> createGestureInterpreter(GestureDefinition def, ScheduledExecutorService scheduler) {
        GestureDefinition.GestureType type = def.getType();
        GestureInterpreter<NativeKeyEvent> gesture;

        switch (type) {
            case CLICK -> {
                ClickEdge edge = ClickEdge.valueOf(def.getString(CLICK_GESTURE_EDGE, ClickEdge.FALLING.name()));
                long clickThresholdMs = def.getLong(CLICK_GESTURE_CLICK_THRESHOLD_MS, 0L);
                int maxClickCount = def.getInt(CLICK_GESTURE_MAX_CLICK_COUNT, 1);
                long debounceMs = def.getLong(CLICK_GESTURE_DEBOUNCE_MS, 0L);

                gesture = new ClickGesture.KeyClickGesture(edge, clickThresholdMs, maxClickCount, debounceMs, scheduler);
            }
            case HOLD -> {
                long holdTimeMs = def.getLong(HOLD_GESTURE_HOLD_THRESHOLD_MS, 0L);
                gesture = new HoldGesture.KeyHoldGesture(holdTimeMs, scheduler);
            }
            default -> throw new IllegalArgumentException("Unsupported gesture type: " + type);
        }

        return gesture;
    }

    private Function<Gesture, EventSelector> createEventIdentityFunction(EventSelector original) {
        String typeTemplate = original.type();
        if (typeTemplate != null && typeTemplate.contains("{{GESTURE}}")) {
            return gesture -> {
                String resolvedType = typeTemplate.replace("{{GESTURE}}", gesture.name());
                return new EventSelector(original.domain(), original.name(), resolvedType);
            };
        } else {
            return gesture -> original;  /* 不含占位符，直接返回固定 TriggerEventIdentity */
        }
    }

    public NeuKeyGestureTriggerTemplate(
            ScheduledExecutorService scheduler,
            NativeInputEventSource<NativeKeyEvent> source) {
        this.scheduler = scheduler;
        this.source = source;
    }

    @Override
    protected NeuKeyGestureTriggerDefinition normalize(NeuKeyGestureTriggerDefinition definition) {
        NeuKeyGestureTriggerDefinition def = super.normalize(definition);

        /* 事件类型检查 */
        if (def.getEventIdentity() == null) {
            ComponentId identity = def.identity();
            def.setEventIdentity(new EventSelector(identity.domain(), identity.name(), "{{GESTURE}}"));
        }

        /* 原始事件过滤器检查 */
        SpecFilterDefinition<NeuKeySpec, NeuModifierConstraint> filterDefinition = def.getFilterDefinition();
        if (filterDefinition == null || !filterDefinition.validate()) {
            throw new IllegalArgumentException("GestureNeuKeyTriggerDefinition.filterDefinition cannot be null");
        }

        /* 手势检查 */
        GestureDefinition gestureDef = def.getGestureDefinition();
        if (gestureDef == null) {
            gestureDef = new GestureDefinition();
            gestureDef.setType(GestureDefinition.GestureType.CLICK);
            def.setGestureDefinition(gestureDef);
        }

        /* 手势参数检查 */
        Map<String, Object> params = gestureDef.getParams();
        if (params == null) {
            params = new HashMap<>();
            gestureDef.setParams(params);
        }

        return def;
    }

    @Override
    public Trigger doCreate(NeuKeyGestureTriggerDefinition def) {
        SpecFilter<NeuKeySpec, JNativeHookKeySpec, NeuModifierConstraint, JNativeHookModifierConstraint, NativeKeyEvent> filter =
                createFilter(def.getFilterDefinition());

        GestureInterpreter<NativeKeyEvent> gestureInterpreter =
                createGestureInterpreter(def.getGestureDefinition(), scheduler);

        Function<Gesture, EventSelector> eventIdentityFunction =
                createEventIdentityFunction(def.getEventIdentity());

        return new GestureTrigger.NeuKeyTrigger(
                def.identity(),
                eventIdentityFunction,
                filter,
                gestureInterpreter,
                source
        );
    }

    @Override
    public String type() {
        return TEMPLATE_TYPE_STRING;
    }
}
