package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.ComponentId;

import java.util.List;

@Deprecated
public interface TriggerQuery {

    /**
     * 列出所有已部署的 root rootTriggers。
     */
    List<ComponentId> listDeployed();

    /**
     * 是否已部署。
     */
    boolean isDeployed(ComponentId rootId);

}
