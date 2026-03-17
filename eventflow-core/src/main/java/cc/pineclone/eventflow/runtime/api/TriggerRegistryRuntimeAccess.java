package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.event.EventSink;

import java.util.function.Consumer;

@Deprecated
public interface TriggerRegistryRuntimeAccess {

    /**
     * 在 Registry 内部上下文中访问某个 root Trigger 实例。
     * <p>
     * 注意：这是内部 SPI，用于 TriggerAdmin/TriggerService 等编排层执行 attach/detach，
     * 不应该把 Trigger 实例泄露到外部长期持有。
     */
    void withRootTrigger(ComponentId rootId, Consumer<RootTriggerHandle> handler);

    interface RootTriggerHandle {
        void attach(EventSink sink);
        void detach();
        ComponentId identity();
    }
}
