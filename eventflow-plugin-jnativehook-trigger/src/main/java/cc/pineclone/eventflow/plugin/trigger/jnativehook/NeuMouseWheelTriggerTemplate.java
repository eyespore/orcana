package cc.pineclone.eventflow.plugin.trigger.jnativehook;

import cc.pineclone.eventflow.runtime.api.selector.EventSelector;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.*;
import cc.pineclone.eventflow.interaction.NeuModifierConstraint;
import cc.pineclone.eventflow.interaction.NeuMouseWheelSpec;
import cc.pineclone.eventflow.core.api.Trigger;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.plugin.api.template.TriggerTemplate;
import cc.pineclone.eventflow.trigger.jnativehook.api.*;
import cc.pineclone.eventflow.plugin.trigger.jnativehook.filter.NeuMouseWheelSpecFilter;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;

import java.util.function.Function;

public class NeuMouseWheelTriggerTemplate implements TriggerTemplate {

    private static final String TEMPLATE_TYPE_STRING = "jnativehook:neu_mouse_wheel_trigger";

    private final NativeInputEventSource<NativeMouseWheelEvent> source;

    private SpecFilter<
                NeuMouseWheelSpec,
            JNativeHookMouseWheelSpec,
                NeuModifierConstraint,
            JNativeHookModifierConstraint,
                NativeMouseWheelEvent> createFilter(SpecFilterDefinition<NeuMouseWheelSpec, NeuModifierConstraint> def) {

        NeuMouseWheelSpec scope = def.getOriginalSpec();
        NeuModifierConstraint modifier = def.getOriginalModifier();
        return new NeuMouseWheelSpecFilter(scope, modifier);
    }

    private Function<String, EventSelector> createEventIdentityFunction(EventSelector original) {
        String typeTemplate = original.eventType();
        if (typeTemplate != null && typeTemplate.contains("{{SIGNAL}}")) {
            return gesture -> {
                String resolvedType = typeTemplate.replace("{{SIGNAL}}", gesture);
                return new EventSelector(original.domain(), original.name(), resolvedType);
            };
        } else {
            return gesture -> original;  /* 不含占位符，直接返回固定 TriggerEventIdentity */
        }
    }

    public NeuMouseWheelTriggerTemplate(NativeInputEventSource<NativeMouseWheelEvent> source) {
        this.source = source;
    }

    @Override
    protected NeuMouseWheelTriggerDefinition normalize(NeuMouseWheelTriggerDefinition definition) {
        NeuMouseWheelTriggerDefinition def = super.normalize(definition);

        /* 事件类型检查 */
        if (def.getEventIdentity() == null) {
            ComponentId identity = def.identity();
            def.setEventIdentity(new EventSelector(identity.domain(), identity.name(), "{{SIGNAL}}"));
        }

        /* 原始事件过滤器检查 */
        SpecFilterDefinition<NeuMouseWheelSpec, NeuModifierConstraint> filterDefinition = def.getFilterDefinition();
        if (filterDefinition == null || !filterDefinition.validate()) {
            throw new IllegalArgumentException("GestureNeuKeyTriggerDefinition.filterDefinition cannot be null");
        }

        return def;
    }

    @Override
    public Trigger doCreate(NeuMouseWheelTriggerDefinition def) {
        SpecFilterDefinition<NeuMouseWheelSpec, NeuModifierConstraint> filterDefinition = def.getFilterDefinition();
        SpecFilter<NeuMouseWheelSpec,
                JNativeHookMouseWheelSpec,
                NeuModifierConstraint,
                JNativeHookModifierConstraint,
                NativeMouseWheelEvent> filter =
                createFilter(filterDefinition);


        Function<String, EventSelector> eventIdentityFunction =
                createEventIdentityFunction(def.getEventIdentity());

        return new SignalTrigger.NeuMouseWheelTrigger(
                def.identity(),
                eventIdentityFunction,
                filter,
                source
        );
    }

    @Override
    public String type() {
        return TEMPLATE_TYPE_STRING;
    }
}
