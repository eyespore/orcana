package cc.pineclone.eventflow.runtime.impl.trigger.factory;

import cc.pineclone.eventflow.config.api.CompositeTriggerDefinition;
import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.plugin.api.template.TriggerTemplate;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class DefaultCompositeTriggerTemplate<T extends CompositeTriggerDefinition>
        implements TriggerTemplate {

    @Override
    protected T normalize(T definition) {
        T def = super.normalize(definition);
        normalizeComposite(def);
        return def;
    }

    private void normalizeComposite(CompositeTriggerDefinition def) {
        List<TriggerDefinition> childDefinitions = def.children();
        if (childDefinitions == null) {
            def.setChildren(new ArrayList<>());
            return;
        }

        /* 父 Identity 不应该为空 */
        ComponentId parentId = def.identity();
        if (parentId == null) {
            throw new IllegalStateException(
                    "CompositeTriggerDefinition identity must be normalized before children");
        }

        int index = 0;
        for (TriggerDefinition child : childDefinitions) {
            if (child.identity() == null) {
                child.setIdentity(new ComponentId(
                        parentId.domain(),
                        parentId.name() + "_" + index++)
                );
            }

            if (child instanceof CompositeTriggerDefinition composite) {
                normalizeComposite(composite);
            }
        }
    }
}
