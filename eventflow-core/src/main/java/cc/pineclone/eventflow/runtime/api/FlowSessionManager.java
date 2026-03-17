package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.action.Action;
import cc.pineclone.eventflow.core.api.action.ActionIdentity;
import cc.pineclone.eventflow.core.api.context.FlowSession;
import cc.pineclone.eventflow.core.api.context.SessionId;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public interface FlowSessionManager {

    Optional<FlowSession> get(SessionId sessionId);

    Collection<SessionId> getAllSessionIds();

    Collection<FlowSession> getAllSessions();

    boolean contains(SessionId sessionId);

    boolean cancel(SessionId sessionId);

    boolean remove(SessionId sessionId);

    /**
     * 获取或创建（原子语义）：同一个 ActionIdentity 永远只会得到同一个 active session。
     *
     * 注意：sessionId 在你设计里是“排队就创建”，因此通常由 Engine 生成并传入。
     * 若 session 已存在，你需要决定是否忽略新 sessionId、还是返回 Deny/Queue 等。
     * 推荐：由 Engine 在更上层决定“同 identity 并发 submit”的策略。
     */
    @Deprecated
    ActionSession getOrCreateNewSession(Action action);

    @Deprecated
    Optional<ActionSession> findSessionByIdentity(ActionIdentity identity);

    @Deprecated
    Optional<ActionSession> findSessionById(UUID sessionId);

    /**
     * 当 run 进入终态（completed/canceled/failed）后移除 session。
     * 返回 true 表示确实移除了；false 表示 session 已不在表中或 sessionId 不匹配。
     */
    @Deprecated
    boolean removeSession(ActionIdentity identity, UUID sessionId);

    /* 异步执行 */
    @Deprecated
    <T> CompletionStage<T> attachSessionAsync(UUID sessionId, Function<ActionSession, T> task);

    /* 串行执行 */
    @Deprecated
    <T> T attachSessionSync(UUID sessionId, Function<ActionSession, T> task);

}
