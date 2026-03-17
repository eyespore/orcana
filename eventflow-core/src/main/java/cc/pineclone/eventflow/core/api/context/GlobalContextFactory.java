package cc.pineclone.eventflow.core.api.context;

@Deprecated
public interface GlobalContextFactory {

    /**
     * Engine 启动时创建一次，全局复用。
     * 如果你希望 lazy，也可以让实现自己缓存并返回同一个实例。
     */
    GlobalContext createContext();

}
