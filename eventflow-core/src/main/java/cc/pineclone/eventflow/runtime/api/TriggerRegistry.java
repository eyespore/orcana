package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.trigger.Trigger;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;

import java.util.Collection;
import java.util.List;

@Deprecated
public interface TriggerRegistry {

    ComponentId register(TriggerDefinition definition);

    Trigger get(TriggerDefinition definition);

    Collection<Trigger> getAll();

    @Deprecated
    void unregister(ComponentId identity);

    /**
     * 基于 TriggerIdentity 增加某个 Trigger 的引用计数，增加引用
     * @param identity Trigger 标识符
     */
    @Deprecated
    void retain(ComponentId identity);

    /**
     * 基于 TriggerIdentity 减少某个 Trigger 的引用计数，释放引用
     * @param identity Trigger 标识符
     */
    @Deprecated
    void release(ComponentId identity);

    /**
     * 测试用途
     * @return 当前注册到 Registry 的根 Trigger 列表
     */
    @Deprecated
    List<Trigger> getRootTriggers();

    /**
     * 查询：列出当前 Registry 中已注册的根 TriggerIdentity。
     * 不暴露 Trigger 实例，避免外部持有内部对象引用导致生命周期/线程模型被破坏。
     */
    @Deprecated
    List<ComponentId> listRootIdentities();

    /**
     * 查询：判断某个 rootId 是否已注册。
     */
    @Deprecated
    boolean contains(ComponentId rootId);

}
