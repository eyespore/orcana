package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.NeuModifierConstraint;
import cc.pineclone.interaction.NeuMouseWheelSpec;
import cc.pineclone.workflow.api.trigger.Trigger;
import cc.pineclone.workflow.api.trigger.event.TriggerEventIdentity;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.impl.trigger.factory.DefaultTriggerFactory;
import cc.pineclone.workflow.trigger.jnativehook.api.*;
import cc.pineclone.workflow.trigger.jnativehook.filter.NeuMouseWheelSpecFilter;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;

import java.util.function.Function;

public class NeuMouseWheelTriggerFactory extends DefaultTriggerFactory<NeuMouseWheelTriggerDefinition> {

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

    private Function<String, TriggerEventIdentity> createEventIdentityFunction(TriggerEventIdentity original) {
        String typeTemplate = original.type();
        if (typeTemplate != null && typeTemplate.contains("{{SIGNAL}}")) {
            return gesture -> {
                String resolvedType = typeTemplate.replace("{{SIGNAL}}", gesture);
                return new TriggerEventIdentity(original.domain(), original.name(), resolvedType);
            };
        } else {
            return gesture -> original;  /* 不含占位符，直接返回固定 TriggerEventIdentity */
        }
    }

    public NeuMouseWheelTriggerFactory(NativeInputEventSource<NativeMouseWheelEvent> source) {
        this.source = source;
    }

    @Override
    protected NeuMouseWheelTriggerDefinition normalize(NeuMouseWheelTriggerDefinition definition) {
        NeuMouseWheelTriggerDefinition def = super.normalize(definition);

        /* 事件类型检查 */
        if (def.getEventIdentity() == null) {
            TriggerIdentity identity = def.getIdentity();
            def.setEventIdentity(new TriggerEventIdentity(identity.domain(), identity.name(), "{{SIGNAL}}"));
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


        Function<String, TriggerEventIdentity> eventIdentityFunction =
                createEventIdentityFunction(def.getEventIdentity());

        return new SignalTrigger.NeuMouseWheelTrigger(
                def.getIdentity(),
                eventIdentityFunction,
                filter,
                source
        );
    }

    @Override
    public Class<NeuMouseWheelTriggerDefinition> definitionType() {
        return NeuMouseWheelTriggerDefinition.class;
    }
}
