package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;
import cc.pineclone.eventflow.core.api.ComponentId;

@Deprecated
public interface TriggerAdmin {

    /**
     * 部署：把 TriggerDefinition 注册为一个可激活的 root trigger tree。
     * 返回 rootId（通常等于 definition.identity，但也允许实现进行规范化/重写）。
     */
    ComponentId deploy(TriggerDefinition definition);

    /**
     * 卸载：删除已部署的 root trigger tree（要求未激活/无引用）。
     */
    void undeploy(ComponentId rootId);

    /**
     * 激活：使 root trigger tree 接入事件管线（内部负责 retain + attach 的边界一致性）。
     * 多次 activate 幂等（0->1 才发生真正 attach/init）
     */
    void activate(ComponentId rootId);

    /**
     * 停用：从事件管线断开（内部负责 detach + release 的边界一致性）。
     * 多次 deactivate 幂等（1->0 才发生真正 detach/destroy）。
     */
    void deactivate(ComponentId rootId);

    default void redeploy(TriggerDefinition definition) {
        ComponentId id = deploy(definition);
        activate(id);
    }

}
