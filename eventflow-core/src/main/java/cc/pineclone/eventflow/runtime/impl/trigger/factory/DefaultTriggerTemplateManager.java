package cc.pineclone.eventflow.runtime.impl.trigger.factory;

import cc.pineclone.eventflow.plugin.api.template.ComponentTemplate;
import cc.pineclone.eventflow.plugin.api.ComponentTemplateProvider;
import cc.pineclone.eventflow.plugin.api.ComponentTemplateRegistrar;
import cc.pineclone.eventflow.plugin.api.template.TriggerTemplate;

import java.util.*;

public class DefaultTriggerTemplateManager implements
        ComponentTemplateRegistrar,
        ComponentTemplateProvider<TriggerTemplate> {

    private final Map<String, ComponentTemplate<?, ?>> table = new HashMap<>();

    public DefaultTriggerTemplateManager() {  /* 注册默认触发器 */
        register(new AnyOrderTriggerTemplate());  /* 无序触发器 */
        register(new SequentialTriggerTemplate());  /* 顺序触发器 */
        register(new UnionTriggerTemplate());  /* 事件映射触发器 */
    }

    @Override
    public Optional<TriggerTemplate> findTemplate(String templateType) {
        return Optional.ofNullable(table.get(templateType));
    }

    @Override
    public void register(ComponentTemplate<?, ?> template) {
        this.table.put(template.type(), template);
    }

}
