package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.NeuMouseSpec;
import cc.pineclone.interaction.api.ClickEdge;
import cc.pineclone.interaction.api.Gesture;
import cc.pineclone.workflow.api.trigger.Trigger;
import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.impl.trigger.factory.DefaultTriggerFactory;
import cc.pineclone.workflow.trigger.jnativehook.api.*;
import cc.pineclone.workflow.trigger.jnativehook.filter.NeuMouseSpecFilter;
import cc.pineclone.workflow.trigger.jnativehook.gesture.ClickGesture;
import cc.pineclone.workflow.trigger.jnativehook.gesture.GestureDefinition;
import cc.pineclone.workflow.trigger.jnativehook.gesture.HoldGesture;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

public class NeuMouseGestureTriggerFactory extends DefaultTriggerFactory<NeuMouseGestureTriggerDefinition> {

    private static final String CLICK_GESTURE_EDGE = "edge";
    private static final String CLICK_GESTURE_CLICK_THRESHOLD_MS = "clickThresholdMs";
    private static final String CLICK_GESTURE_MAX_CLICK_COUNT = "maxClickCount";
    private static final String CLICK_GESTURE_DEBOUNCE_MS = "debounceMs";

    private static final String HOLD_GESTURE_HOLD_THRESHOLD_MS = "holdThresholdMs";

    private final ScheduledExecutorService scheduler;
    private final NativeInputEventSource<NativeMouseEvent> source;

    private SpecFilter<NeuMouseSpec, JNativeHookMouseSpec, NeuModifierConstraint, JNativeHookModifierConstraint, NativeMouseEvent>
    createFilter(SpecFilterDefinition<NeuMouseSpec, NeuModifierConstraint> def) {

        NeuMouseSpec scope = def.getOriginalSpec();
        NeuModifierConstraint modifier = def.getOriginalModifier();
        return new NeuMouseSpecFilter(scope, modifier);
    }

    private GestureInterpreter<NativeMouseEvent> createGestureInterpreter(GestureDefinition def, ScheduledExecutorService scheduler) {
        GestureDefinition.GestureType type = def.getType();
        GestureInterpreter<NativeMouseEvent> gesture;

        switch (type) {
            case CLICK -> {
                ClickEdge edge = ClickEdge.valueOf(def.getString(CLICK_GESTURE_EDGE, ClickEdge.FALLING.name()));
                long clickThresholdMs = def.getLong(CLICK_GESTURE_CLICK_THRESHOLD_MS, 0L);
                int maxClickCount = def.getInt(CLICK_GESTURE_MAX_CLICK_COUNT, 1);
                long debounceMs = def.getLong(CLICK_GESTURE_DEBOUNCE_MS, 0L);

                gesture = new ClickGesture.MouseButtonClickGesture(edge, clickThresholdMs, maxClickCount, debounceMs, scheduler);
            }
            case HOLD -> {
                long holdTimeMs = def.getLong(HOLD_GESTURE_HOLD_THRESHOLD_MS, 0L);
                gesture = new HoldGesture.MouseButtonHoldGesture(holdTimeMs, scheduler);
            }
            default -> throw new IllegalArgumentException("Unsupported gesture type: " + type);
        }

        return gesture;
    }

    private Function<Gesture, TriggerEventIdentity> createEventIdentityFunction(TriggerEventIdentity original) {
        String typeTemplate = original.type();
        if (typeTemplate != null && typeTemplate.contains("{{GESTURE}}")) {
            return gesture -> {
                String resolvedType = typeTemplate.replace("{{GESTURE}}", gesture.name());
                return new TriggerEventIdentity(original.domain(), original.name(), resolvedType);
            };
        } else {
            return gesture -> original;  /* 不含占位符，直接返回固定 TriggerEventIdentity */
        }
    }

    public NeuMouseGestureTriggerFactory(
            ScheduledExecutorService scheduler,
            NativeInputEventSource<NativeMouseEvent> source) {
        this.scheduler = scheduler;
        this.source = source;
    }

    @Override
    protected NeuMouseGestureTriggerDefinition normalize(NeuMouseGestureTriggerDefinition definition) {
        NeuMouseGestureTriggerDefinition def = super.normalize(definition);

        /* 事件类型检查 */
        if (def.getEventIdentity() == null) {
            TriggerIdentity identity = def.getIdentity();
            def.setEventIdentity(new TriggerEventIdentity(identity.domain(), identity.name(), "{{GESTURE}}"));
        }

        /* 原始事件过滤器检查 */
        SpecFilterDefinition<NeuMouseSpec, NeuModifierConstraint> filterDefinition = def.getFilterDefinition();
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
    public Trigger doCreate(NeuMouseGestureTriggerDefinition def) {
        SpecFilterDefinition<NeuMouseSpec, NeuModifierConstraint> filterDefinition = def.getFilterDefinition();
        SpecFilter<NeuMouseSpec, JNativeHookMouseSpec, NeuModifierConstraint, JNativeHookModifierConstraint, NativeMouseEvent> filter =
                createFilter(filterDefinition);

        GestureInterpreter<NativeMouseEvent> gestureInterpreter =
                createGestureInterpreter(def.getGestureDefinition(), scheduler);

        Function<Gesture, TriggerEventIdentity> eventIdentityFunction =
                createEventIdentityFunction(def.getEventIdentity());

        return new GestureTrigger.NeuMouseTrigger(
                def.getIdentity(),
                eventIdentityFunction,
                filter,
                gestureInterpreter,
                source
        );
    }

    @Override
    public Class<NeuMouseGestureTriggerDefinition> definitionType() {
        return NeuMouseGestureTriggerDefinition.class;
    }
}
