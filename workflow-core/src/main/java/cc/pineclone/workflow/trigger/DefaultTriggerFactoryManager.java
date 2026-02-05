package cc.pineclone.workflow.trigger;

import cc.pineclone.workflow.DefaultPluginManager;
import cc.pineclone.workflow.api.trigger.TriggerDefinition;
import cc.pineclone.workflow.api.trigger.TriggerFactory;
import cc.pineclone.workflow.api.trigger.TriggerFactoryManager;

import java.util.HashMap;
import java.util.Map;

public class DefaultTriggerFactoryManager implements TriggerFactoryManager {

    private final Map<Class<? extends TriggerDefinition>, TriggerFactory<?>> factories = new HashMap<>();

    public DefaultTriggerFactoryManager() {  /* 注册默认触发器 */
        registerTriggerFactory(new AnyOrderTriggerFactory());  /* 无序触发器 */
        registerTriggerFactory(new SequentialTriggerFactory());  /* 顺序触发器 */
        registerTriggerFactory(new UnionTriggerFactory());  /* 事件映射触发器 */
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TriggerDefinition> TriggerFactory<T> getTriggerFactory(Class<T> definitionType) {
        return (TriggerFactory<T>) factories.get(definitionType);
    }

    @Override
    public <T extends TriggerDefinition> void registerTriggerFactory(TriggerFactory<T> factory) {
        this.factories.put(factory.definitionType(), factory);
    }
}
