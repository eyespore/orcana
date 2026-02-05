package cc.pineclone.workflow.api;

/* 需要强制类型转换后调用，当用户面向 PluginManager 编程时将无法看到此接口 */
@Deprecated
public interface CapabilityRegistrar {

    <T> void registerCapability(Class<T> type, T capability);

}
