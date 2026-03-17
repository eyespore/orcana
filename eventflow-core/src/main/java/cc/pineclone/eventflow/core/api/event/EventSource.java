package cc.pineclone.eventflow.core.api.event;

import org.jetbrains.annotations.Nullable;

public interface EventSource {

    /* 阻塞获取 TriggerEvent */
    Event take() throws InterruptedException;

    /* 异步获取 TriggerEvent，或TriggerEvent不存在则返回 null */
    @Nullable
    Event poll();

}
