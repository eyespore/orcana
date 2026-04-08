package cc.pineclone.eventflow.runtime.api.session;

import java.util.Map;

public interface RuntimeSession extends Session {

    Map<String, Object> vars();

    Status status();

    int workCount();

    default boolean isActive() {
        return status() == Status.ACTIVE;
    }

    default boolean isTerminated() {
        Status currentStatus = status();
        return currentStatus == Status.CANCELED
                || currentStatus == Status.COMPLETED
                || currentStatus == Status.FAILED;
    }

    default boolean isCancelRequested() {
        return status() == Status.CANCEL_REQUESTED;
    }

    enum Status {
        /**
         * RuntimeSession 刚创建时，未被 retain 添加工作计数之前
         */
        IDLE,
        /**
         * RuntimeSession 处于正常执行链当中，且允许继续派生新的工作
         */
        ACTIVE,
        /**
         * 由 Runtime 基于外部控制命令或 ConcurrencyPolicy 请求取消该 RuntimeSession，但其目前
         * 仍然处于工作状态
         */
        CANCEL_REQUESTED,
        /**
         * RuntimeSession 正常走到尽头，并被 Runtime 明确标记完成
         */
        COMPLETED,
        /**
         * RuntimeSession 进入 CancelRequested 状态，在协作式过程中停止排空，最终被 Runtime 确认完成
         */
        CANCELED,
        /**
         * RuntimeSession 在某一结点，可能是 Mapper Router 或 Action 运行时触发异常
         */
        FAILED
    }
}
