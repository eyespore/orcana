package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.action.Action;
import cc.pineclone.eventflow.core.api.context.SessionContext;

import java.util.UUID;

@Deprecated
public interface ActionSession {
    UUID sessionId();

    Action action();

    SessionContext sessionContext();  // 这就是你说的 SessionContext

    State state();

    void setState(State newState);

    boolean markCleanupAttachedOnce();  /* 标记当前会话已经挂在了释放资源回调 */

    /**
     * 可选：如果你想在 session 上挂一些“运行时状态”，例如是否已占有 mutex 资源、
     * 当前是否 paused、最后一次命令时间戳等。
     */
    enum State {
        ACTIVE,        // 刚创建尚未 start/admit（取决于你的语义）
        FINISHED    // 已终态（但可能还没从表中清理）
    }
}
