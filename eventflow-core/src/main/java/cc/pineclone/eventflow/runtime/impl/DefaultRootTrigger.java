package cc.pineclone.eventflow.runtime.impl;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.runtime.api.session.RuntimeSession;
import cc.pineclone.eventflow.core.api.event.EventSink;
import cc.pineclone.eventflow.core.api.ConcurrencyPolicy;
import cc.pineclone.eventflow.core.api.Trigger;
import cc.pineclone.eventflow.runtime.spi.RootTriggerControl;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultRootTrigger implements RootTriggerControl {

    private final Trigger delegate;
    private final ComponentId delegateId;
    private final Set<String> groups;
    private final ConcurrencyPolicy concurrencyPolicy;

    private final Set<RuntimeSession> sessions = ConcurrentHashMap.newKeySet();
    private final AtomicReference<RuntimeSession> currentSession = new AtomicReference<>();

    public DefaultRootTrigger(
            ConcurrencyPolicy concurrencyPolicy,
            Set<String> groups,
            Trigger delegate) {

        this.concurrencyPolicy = Objects.requireNonNull(concurrencyPolicy, "concurrencyPolicy");
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.groups = Set.copyOf(Objects.requireNonNull(groups, "groups"));
        this.delegateId = Objects.requireNonNull(delegate.id(), "delegateId");
    }

    @Override
    public Trigger delegate() {
        return delegate;
    }

    @Override
    public ConcurrencyPolicy concurrencyPolicy() {
        return concurrencyPolicy;
    }

    @Override
    public Set<String> groups() {
        return groups;
    }

    @Override
    public Set<RuntimeSession> currentSessions() {
        return sessions;
    }

    @Override
    public void setCurrentSession(RuntimeSession session) {
        currentSession.set(Objects.requireNonNull(session, "session"));
    }

    @Override
    public void clearCurrentSessionIfTerminal() {
        RuntimeSession session = currentSession.get();
        if (session == null) return;
        if (session.isTerminated()) {
            currentSession.compareAndSet(session, null);
        }
    }

    @Override
    public void removeSession(RuntimeSession session) {
        Objects.requireNonNull(session, "session");
        currentSession.compareAndSet(session, null);
    }

}
