package cc.pineclone.eventflow.runtime.impl.action;

import cc.pineclone.eventflow.core.api.action.Action;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class DefaultActionSession implements ActionSession {

    private final UUID sessionId;
    private final Action action;
    private final SessionContext sessionContext;

    private final AtomicReference<State> state = new AtomicReference<>(State.ACTIVE);
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicBoolean cleanupAttachedOnce = new AtomicBoolean(false);

    DefaultActionSession(UUID sessionId,
                         Action action,
                         SessionContext sessionContext) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId is null");
        this.action = Objects.requireNonNull(action, "action");
        this.sessionContext = Objects.requireNonNull(sessionContext, "sessionContext");
    }

    @Override
    public UUID sessionId() {
        return sessionId;
    }

    @Override
    public Action action() {
        return action;
    }

    @Override
    public SessionContext sessionContext() {
        return sessionContext;
    }

    @Override
    public State state() {
        return state.get();
    }

    @Override
    public void setState(State newState) {
        state.set(Objects.requireNonNull(newState, "newState"));
    }

    @Override
    public boolean markCleanupAttachedOnce() {
        return cleanupAttachedOnce.compareAndSet(false, true);
    }

    public Lock getLock() {
        return lock;
    }
}
