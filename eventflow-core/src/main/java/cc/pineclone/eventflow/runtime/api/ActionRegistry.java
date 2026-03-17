package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.action.Action;

import java.util.Collection;
import java.util.Optional;

@Deprecated
public interface ActionRegistry {

    /**
     * 解析 Action：Engine submit 的第一步。
     * 找不到则 Engine 返回 SubmitAck.Denied（action not found）。
     */
    Optional<Action> get(ComponentId identity);

    /**
     * 注册一个 Action 定义到 Registry。
     *
     * 约定：同一个 identity 已存在时的行为由实现决定（拒绝 / 覆盖 / 抛异常）。
     * 如果你希望强约束，我建议在实现里提供两种方法：
     * - register(action): 已存在就抛异常
     * - replace(action): 允许覆盖
     */
    void register(Action action);

    Collection<Action> getAll();
}
