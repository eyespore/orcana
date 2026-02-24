package cc.pineclone.workflow.api.trigger.registry;

import cc.pineclone.workflow.api.trigger.Trigger;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.api.trigger.factory.TriggerDefinition;

import java.util.List;

public interface TriggerRegistry {

    TriggerIdentity register(TriggerDefinition definition);

    void unregister(TriggerIdentity identity);

    /**
     * 基于 TriggerIdentity 增加某个 Trigger 的引用计数，增加引用
     * @param identity Trigger 标识符
     */
    void retain(TriggerIdentity identity);

    /**
     * 基于 TriggerIdentity 减少某个 Trigger 的引用计数，释放引用
     * @param identity Trigger 标识符
     */
    void release(TriggerIdentity identity);

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
    List<TriggerIdentity> listRootIdentities();

    /**
     * 查询：判断某个 rootId 是否已注册。
     */
    boolean contains(TriggerIdentity rootId);

}
