package cc.pineclone.eventflow.runtime.api;

import java.util.Objects;
import java.util.UUID;

@Deprecated
public interface MutexCoordinator {

    /**
     * Engine 询问：对某个 run 的“执行权”做仲裁。
     * 这里的 sessionId 是 Engine 创建的（你当前 Queued 里就返回 sessionId 的设计）。
     */
    Decision decide(UUID sessionId, MutexProfile mutexProfile);

    /**
     * Engine 通知：某个 run 已经“实际占有”资源（开始执行）。
     * 一般在 decide 返回 ADMIT / PREEMPT_* 后，Engine 成功开始执行时调用。
     */
    void markSessionAsAdmitted(UUID sessionId);

    /**
     * Engine 通知：run 释放资源（完成 / 取消 / 暂停 yield 等）。
     * Scheduler 应该据此选择下一个可运行的 queued run（如有）。
     */
    ReleaseResult markSessionAsReleased(UUID sessionId);

    // ---- DTOs / sealed results ----


    sealed interface Decision permits Decision.Admit, Decision.Queue, Decision.Deny, Decision.Preempt {

        /** 允许立刻执行（占有资源） */
        record Admit(String message) implements Decision { }

        /** 入队等待（scheduler 记录排队信息） */
        record Queue(String message) implements Decision { }

        /** 拒绝（例如资源策略不允许、profile 非法等） */
        record Deny(String message) implements Decision { }

        /**
         * 需要抢占：先让 victim 让出资源（pause/cancel），再 admit 当前 run。
         * Engine 负责对 victim 下发 PAUSE/CANCEL 命令并等待其释放资源，
         * 之后再重新调用 decide 或由 scheduler 在 onReleased 时选中当前 run。
         */
        record Preempt(PreemptPlan plan) implements Decision { }
    }

    sealed interface ReleaseResult permits ReleaseResult.Nothing, ReleaseResult.AdmitNext {
        /** 没有可运行的 queued run */
        record Nothing() implements ReleaseResult { }
        /**
         * 释放后选择了一个 queued run 可以被 admit（Engine 据此开始执行它）。
         * 这里返回 sessionId 就够了：Engine 自己有 SessionStore 能找到 action/profile/resources。
         */
        record AdmitNext(UUID sessionId, String message) implements ReleaseResult { }
    }

    record PreemptPlan(
            UUID victimSessionId,
            PreemptionPolicy policy
    ) {
        public PreemptPlan {
            Objects.requireNonNull(victimSessionId, "victimSessionId");
            Objects.requireNonNull(policy, "policy");
        }
    }
}
