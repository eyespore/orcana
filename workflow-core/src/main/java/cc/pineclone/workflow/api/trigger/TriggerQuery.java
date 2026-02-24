package cc.pineclone.workflow.api.trigger;

import java.util.List;

public interface TriggerQuery {

    /**
     * 列出所有已部署的 root triggers。
     */
    List<TriggerIdentity> listDeployed();

    /**
     * 是否已部署。
     */
    boolean isDeployed(TriggerIdentity rootId);

}
