package cc.pineclone.eventflow.plugin.api.template;

import cc.pineclone.eventflow.core.api.CoreComponent;
import cc.pineclone.eventflow.config.api.definition.ComponentDefinition;

public interface ComponentTemplate<D extends ComponentDefinition, C extends CoreComponent> {

    default String description() {
        return "";
    }

    String type();  /* 模板类型 */

    C createInstance(D Definition);
}
