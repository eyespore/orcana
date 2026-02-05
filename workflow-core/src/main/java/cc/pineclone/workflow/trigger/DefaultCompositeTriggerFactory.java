package cc.pineclone.workflow.trigger;

import cc.pineclone.workflow.api.trigger.CompositeTriggerDefinition;
import cc.pineclone.workflow.api.trigger.TriggerDefinition;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultCompositeTriggerFactory<T extends CompositeTriggerDefinition>
        extends DefaultTriggerFactory<T> {

    @Override
    protected T normalize(T definition) {
        T def = super.normalize(definition);
        normalizeComposite(def);
        return def;
    }

    private void normalizeComposite(CompositeTriggerDefinition def) {
        List<TriggerDefinition> childDefinitions = def.getChildDefinitions();
        if (childDefinitions == null) {
            def.setChildDefinitions(new ArrayList<>());
            return;
        }

        /* 父 Identity 不应该为空 */
        TriggerIdentity parentId = def.getIdentity();
        if (parentId == null) {
            throw new IllegalStateException(
                    "CompositeTriggerDefinition identity must be normalized before children");
        }

        int index = 0;
        for (TriggerDefinition child : childDefinitions) {
            if (child.getIdentity() == null) {
                child.setIdentity(new TriggerIdentity(
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
