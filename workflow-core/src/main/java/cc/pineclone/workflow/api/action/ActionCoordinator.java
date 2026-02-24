package cc.pineclone.workflow.api.action;

import cc.pineclone.workflow.api.action.command.ActionCommand;

import java.util.UUID;

public interface ActionCoordinator {
    /**
     * 仲裁请求：调用方提交“我是谁 + 我是什么调度画像 + 我想做什么命令”。
     *
     * 语义：
     * - START/RESUME 这类“需要互斥资源”的命令：通常会被排队或直接放行
     * - PAUSE/CANCEL 等“控制命令”：通常不需要仲裁（建议直接走 controller.submit）
     *
     * 返回的是调度层的回执（不是 handler 的回执）。
     */
    ArbitrationAck arbitrate(ArbitrationRequest request);

    /**
     * Controller 上报：某次 run 已经发生状态事实变化。
     * Coordinator 用它来释放互斥资源、完成抢占流程、推进队列。
     */
    void onRunEvent(RunEvent event);

    sealed interface ArbitrationAck permits ArbitrationAck.Admitted, ArbitrationAck.Queued, ArbitrationAck.Denied {
        record Admitted(UUID arbitrationRequestId, String message) implements ArbitrationAck {}
        record Queued(UUID arbitrationRequestId, String message) implements ArbitrationAck {}
        record Denied(UUID arbitrationRequestId, String reason) implements ArbitrationAck {}
    }

    record ArbitrationRequest(
            UUID arbitrationRequestId,
            ActionControllerIdentity controllerId,
            ActionControllerProfile profileSnapshot,
            ActionCommand.CoordinateCommand command
    ) {}

    enum RunEventType {
        /**
         * run 终态（COMPLETED/CANCELED/FAILED）；释放资源的唯一可靠时机
         */
        FINISHED,
        /**
         * 已经进入 PAUSED（如果你把 PAUSE 定义为 yield，让权交接以它为准）
         */
        PAUSED,
        /**
         * 已经 RESUMED（通常非必需；用于观测/一致性）
         */
        RESUMED
    }

    record RunEvent(
            UUID arbitrationRequestId,
            ActionControllerIdentity controllerId,
            RunEventType type,
            ActionHandler.ActionExecutionOutcome outcome,
            String message
    ) {}
}
