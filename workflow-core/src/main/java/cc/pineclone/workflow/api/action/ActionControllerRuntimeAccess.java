package cc.pineclone.workflow.api.action;

import cc.pineclone.workflow.api.action.command.CommandAcceptanceResult;

import java.util.UUID;

public interface ActionControllerRuntimeAccess {

    /**
     * 仅供 Coordinator 调用：放行一个已排队请求。
     * 实现应当幂等：如果 arbitrationRequestId 已取消/已执行，应安全 no-op。
     */
    void dispatch(UUID requestId);

    /**
     * 仅供 Coordinator 调用：通知当前 owner 被抢占，需要 PAUSE（yield permit）。
     * 你也可以不单独开这个方法，而是让 coord 通过 submit(PAUSE) 走同一套入口。
     */
    CommandAcceptanceResult.Status preemptPause(UUID requestId);

    /**
     * 仅供 Coordinator 调用：B 执行完毕后恢复 A。
     */
    CommandAcceptanceResult.Status resumeAfterPreempt(UUID requestId);

}
