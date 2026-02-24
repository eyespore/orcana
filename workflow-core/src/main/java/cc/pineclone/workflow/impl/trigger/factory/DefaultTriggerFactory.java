package cc.pineclone.workflow.impl.trigger.factory;

import cc.pineclone.workflow.api.trigger.Trigger;
import cc.pineclone.workflow.api.trigger.factory.TriggerDefinition;
import cc.pineclone.workflow.api.trigger.factory.TriggerFactory;

public abstract class DefaultTriggerFactory<T extends TriggerDefinition>
        implements TriggerFactory<T> {

    @Override
    public final Trigger createTrigger(T definition) {
        T normalized = normalize(definition);
        validate(normalized);
        return doCreate(normalized);
    }

    protected T normalize(T definition) {
        return definition;
    }

    protected void validate(T definition) {
        if (definition.getIdentity() == null) {
            throw new IllegalStateException(
                    "TriggerDefinition identity must not be null after normalization"
            );
        }
    }

    protected abstract Trigger doCreate(T definition);

}
