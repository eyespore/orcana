package cc.pineclone.workflow.api.trigger;

import org.jetbrains.annotations.Nullable;

public interface TriggerEventSource {

    /* 阻塞获取 TriggerEvent */
    TriggerEvent take() throws InterruptedException;

    /* 异步获取 TriggerEvent，或TriggerEvent不存在则返回 null */
    @Nullable
    TriggerEvent poll();

}
