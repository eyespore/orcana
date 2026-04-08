package cc.pineclone.eventflow.core.api;

public enum ConcurrencyPolicy {

    /**
     * 允许新的 session 和旧的 session 并存，这意味着在 Trigger 触发产生 Event 时总是创建新的 session
     */
    ALLOW_PARALLEL,
    /**
     * 若已有旧的 session，则先请求取消旧的 session，在旧的 session 真正结束之前，不会接受新的 root event，
     * 旧的 session 停止之后，立即创建一个新的 session 执行任务
     */
    CANCEL_PREVIOUS,
    /**
     * 若存在还未结束的上一个 session，那么拒绝创建新的 session，直到旧的 session 执行完毕
     */
    REJECT_NEW,
    /**
     * 若已有旧的 session，取消旧的 session
     * 若不存在旧的 session，那么启动一个新的 session
     */
    TOGGLE

}
