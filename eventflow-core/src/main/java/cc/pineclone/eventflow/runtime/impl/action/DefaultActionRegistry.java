package cc.pineclone.eventflow.runtime.impl.action;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.action.Action;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultActionRegistry implements ActionRegistry {

    private final ConcurrentHashMap<ComponentId, Action> actions = new ConcurrentHashMap<>();

    @Override
    public Optional<Action> get(ComponentId identity) {
        Objects.requireNonNull(identity, "identity");
        return Optional.ofNullable(actions.get(identity));
    }

    @Override
    public void register(Action action) {
        Objects.requireNonNull(action, "action");
        ComponentId id = Objects.requireNonNull(action.id(), "action.identity()");
        Action prev = actions.putIfAbsent(id, action);
        if (prev != null) {
            throw new IllegalStateException("Action already registered: " + id);
        }
    }

    public void replace(Action action) {
        Objects.requireNonNull(action, "action");
        ComponentId id = Objects.requireNonNull(action.id(), "action.identity()");
        actions.put(id, action);
    }

    @Override
    public boolean unregister(ComponentId identity) {
        Objects.requireNonNull(identity, "identity");
        return actions.remove(identity) != null;
    }
}
