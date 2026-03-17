package cc.pineclone.eventflow.runtime.api;

@Deprecated
public interface TriggerServiceLifecycle extends AutoCloseable {

    /**
     * 初始化 Trigger 子系统（创建 dispatcher/router/registry runtime 所需资源）。
     * 一般由 WorkflowCore.init() 调用。
     */
    void init();

    /**
     * 释放资源（停止线程、清空路由、deactivate 等）。
     */
    @Override
    void close();

}
