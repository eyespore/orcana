package cc.pineclone.eventflow.runtime.impl;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.Action;
import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.context.ActionContext;
import cc.pineclone.eventflow.core.api.context.MapperContext;
import cc.pineclone.eventflow.core.api.event.Event;
import cc.pineclone.eventflow.core.api.event.EventSink;
import cc.pineclone.eventflow.core.api.Mapper;
import cc.pineclone.eventflow.runtime.api.*;
import cc.pineclone.eventflow.runtime.api.Runtime;
import cc.pineclone.eventflow.runtime.api.bundle.CommandBundle;
import cc.pineclone.eventflow.runtime.api.bundle.EventBundle;
import cc.pineclone.eventflow.runtime.api.bundle.RootEventBundle;
import cc.pineclone.eventflow.runtime.api.bundle.RuntimeBundle;
import cc.pineclone.eventflow.runtime.api.event.RuntimeEventBus;
import cc.pineclone.eventflow.runtime.api.session.*;
import cc.pineclone.eventflow.runtime.impl.context.DefaultActionContext;
import cc.pineclone.eventflow.runtime.impl.context.DefaultMapperContext;
import cc.pineclone.eventflow.runtime.impl.context.SessionContext;
import cc.pineclone.eventflow.runtime.spi.RootTriggerControl;
import cc.pineclone.eventflow.runtime.spi.RuntimeSessionControl;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public final class DefaultRuntime implements Runtime {

    private static final long WORKER_POLL_MILLIS = 200L;

    private final RuntimeAssembly assembly;
    private final RuntimeSessionFactory sessionFactory;
    private final RuntimeSessionRegistry sessionRegistry;
    private final GlobalSession globalSession;
    private final PathValueAccessor pathValueAccessor;
    private final RuntimeEventBus runtimeEventBus;

    private Map<ComponentId, RootTriggerControl> rootTriggers;
    private Map<ComponentId, Mapper> mappers;
    private Map<ComponentId, Action> actions;
    private Router router;

    private final Object lock = new Object();
    private final AtomicLong runSequence = new AtomicLong(0);

    private volatile Status runtimeStatus = Status.IDLE;
    private volatile long activeRunId = -1L;

    private volatile BlockingQueue<RuntimeBundle> queue;
    private volatile Thread workerThread;

    public DefaultRuntime(
            RuntimeAssembly assembly,
            RuntimeSessionFactory sessionFactory,
            RuntimeSessionRegistry sessionRegistry,
            GlobalSession globalSession,
            PathValueAccessor pathValueAccessor,
            RuntimeEventBus runtimeEventBus
    ) {
        this.assembly = assembly;
        this.sessionFactory = sessionFactory;
        this.sessionRegistry = sessionRegistry;
        this.globalSession = globalSession;
        this.pathValueAccessor = pathValueAccessor;
        this.runtimeEventBus = runtimeEventBus;
    }

    @Override
    public void init() {
        synchronized (lock) {
            if (runtimeStatus != Status.IDLE) {
                throw new IllegalStateException("Runtime already initialized");
            }

            this.router = assembly.router();
            this.rootTriggers = indexRootTriggerControls(assembly.rootTriggers());
            this.mappers = indexMappers(assembly.mappers());
            this.actions = indexActions(assembly.actions());

            this.runtimeStatus = Status.INITIALIZED;
        }

        // publishRuntimeEvent(...)
    }

    private Map<ComponentId, Action> indexActions(List<Action> actions) {
        Map<ComponentId, Action> indexed = new LinkedHashMap<>();

        for (Action action : actions) {
            Action previous = indexed.putIfAbsent(action.id(), action);
            if (previous != null) {
                throw new IllegalStateException("Duplicate action id: " + action.id());
            }
        }

        return Collections.unmodifiableMap(new LinkedHashMap<>(indexed));
    }

    private Map<ComponentId, Mapper> indexMappers(List<Mapper> mappers) {
        Map<ComponentId, Mapper> indexed = new java.util.LinkedHashMap<>();

        for (Mapper mapper : mappers) {
            Mapper previous = indexed.putIfAbsent(mapper.id(), mapper);
            if (previous != null) {
                throw new IllegalStateException("Duplicate mapper id: " + mapper.id());
            }
        }

        return Collections.unmodifiableMap(new LinkedHashMap<>(indexed));
    }

    private Map<ComponentId, RootTriggerControl> indexRootTriggerControls(List<RootTrigger> rootTriggers) {
        Map<ComponentId, RootTriggerControl> indexed = new LinkedHashMap<>();

        for (RootTrigger rootTrigger : rootTriggers) {
            RootTriggerControl control = requireRootTriggerControl(rootTrigger);

            ComponentId delegateId = rootTrigger.delegate().id();
            RootTriggerControl previous = indexed.putIfAbsent(delegateId, control);
            if (previous != null) {  /* 先前已经添加过一个重复的 Control */
                throw new IllegalStateException("Duplicate rootTrigger id: " + delegateId);
            }

            rootTrigger.delegate().init();  /* 初始化 RootTrigger */
        }

        return Collections.unmodifiableMap(new LinkedHashMap<>(indexed));
    }

    @Override
    public void start() {
        long runId;
        BlockingQueue<RuntimeBundle> newQueue;
        Thread newWorker;

        synchronized (lock) {
            if (runtimeStatus != Status.INITIALIZED && runtimeStatus != Status.STOPPED) {
                throw new IllegalStateException("Runtime cannot start from status: " + runtimeStatus);
            }

            newQueue = new LinkedBlockingQueue<>();
            runId = runSequence.incrementAndGet();

            this.queue = newQueue;
            this.activeRunId = runId;
            this.runtimeStatus = Status.STARTED;

            newWorker = new Thread(() -> runLoop(runId), "eventflow-runtime-" + runId);
            this.workerThread = newWorker;
        }

        newWorker.start();

        for (RootTriggerControl rootTrigger : rootTriggers.values()) {
            rootTrigger.delegate().bind(event -> {
                if (cannotAcceptRootEvent(runId)) return;

                boolean offered = offerBundle(new RootEventBundle(rootTrigger, event, 0));
                if (!offered) {
                    // publishRuntimeEvent(...)
                }
            });
            rootTrigger.delegate().start();
        }

        // publishRuntimeEvent(...)
    }

    @Override
    public void stop() {
        synchronized (lock) {
            if (runtimeStatus != Status.STARTED) {
                throw new IllegalStateException("Runtime cannot stop from status: " + runtimeStatus);
            }

            runtimeStatus = Status.STOPPING;
        }

        for (RootTriggerControl rootTrigger : rootTriggers.values()) {
            rootTrigger.delegate().stop();
        }

        for (RuntimeSession session : sessionRegistry.sessions()) {
            RuntimeSessionControl control = requireSessionControl(session);
            control.markCancelRequested();
        }

        // publishRuntimeEvent(...)
    }

    @Override
    public RuntimeSessionRegistry runtimeSessionRegistry() {
        return sessionRegistry;
    }

    @Override
    public GlobalSession globalSession() {
        return globalSession;
    }

    @Override
    public Optional<DeploymentView> deploymentView() {
        return java.util.Optional.empty();
    }

    @Override
    public RuntimeEventBus runtimeEventBus() {
        return runtimeEventBus;
    }

    @Override
    public void deploy(RuntimeAssembly assembly) {
        throw new UnsupportedOperationException("deploy is not supported by DefaultRuntime");
    }

    private void runLoop(long runId) {
        while (activeRunId == runId
                && (this.runtimeStatus == Status.STARTED
                || this.runtimeStatus == Status.STOPPING)) {

            if (this.queue == null) return;
            RuntimeBundle bundle;

            try {
                bundle = this.queue.poll(WORKER_POLL_MILLIS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                bundle = null;
            }

            if (bundle == null) {
                if (runtimeStatus == Status.STOPPING && sessionRegistry.sessions().isEmpty()) {
                    finishStop(runId);
                    break;
                }
                continue;
            }

            try {
                if (bundle instanceof RootEventBundle rootEventBundle) {
                    handleRootEventBundle(rootEventBundle, runId);
                    return;
                }

                if (bundle instanceof EventBundle eventBundle) {
                    handleEventBundle(eventBundle, runId);
                    return;
                }

                if (bundle instanceof CommandBundle commandBundle) {
                    handleCommandBundle(commandBundle, runId);
                    return;
                }

                throw new IllegalStateException("Unsupported bundle type: " + bundle.getClass().getName());
            } catch (Throwable ex) {
                // publishRuntimeEvent(...)
            }
        }
    }

    private enum RootEventDecision {
        ACCEPT,
        REJECT,
        CANCEL_EXISTING_AND_ACCEPT,
        CANCEL_EXISTING_AND_REJECT
    }

    private RootEventDecision evaluateRootEvent(RootTriggerControl rootTrigger) {
        Set<RuntimeSession> liveSessions = rootTrigger.currentSessions().stream()
                .filter(session -> !session.isTerminated())
                .collect(Collectors.toSet());

        return switch (rootTrigger.concurrencyPolicy()) {
            case ALLOW_PARALLEL -> RootEventDecision.ACCEPT;
            case REJECT_NEW -> liveSessions.isEmpty()
                    ? RootEventDecision.ACCEPT
                    : RootEventDecision.REJECT;
            case CANCEL_PREVIOUS -> liveSessions.isEmpty()
                    ? RootEventDecision.ACCEPT
                    : RootEventDecision.CANCEL_EXISTING_AND_ACCEPT;
            case TOGGLE -> liveSessions.isEmpty()
                    ? RootEventDecision.ACCEPT
                    : RootEventDecision.CANCEL_EXISTING_AND_REJECT;
        };
    }

    private void handleRootEventBundle(RootEventBundle bundle, long runId) {
        RootTriggerControl rootTrigger = requireRootTriggerControl(bundle.rootTrigger());

        if (cannotAcceptRootEvent(runId)) {
            return;
        }

        if (!acceptRootEvent(rootTrigger)) {
            // publishRuntimeEvent(...)
            return;
        }

        RuntimeSession session = sessionFactory.create(rootTrigger, bundle.event());
        RuntimeSessionControl sessionControl = requireSessionControl(session);

        sessionRegistry.register(session);
        rootTrigger.addSession(session);

        sessionControl.onTerminated(() -> cleanup(rootTrigger, sessionControl));

        if (!sessionControl.retain()) {
            cleanup(rootTrigger, sessionControl);
            return;
        }

        boolean offered = offerBundle(new EventBundle(session, bundle.event()));
        if (!offered) {
            sessionControl.release();
            // publishRuntimeEvent(...)
        }

        // publishRuntimeEvent(...)
    }

    private void handleEventBundle(EventBundle bundle, long runId) {
        RuntimeSession session = bundle.runtimeSession();
        RuntimeSessionControl sessionControl = requireSessionControl(session);

        try {
            if (shouldSkipExecution(session)) {
                return;
            }

            MapperContext context = createMapperContext(session, bundle.event());

            List<ComponentId> mapperIds = router.routeEvent(bundle.event());
            boolean propagated = false;

            for (ComponentId mapperId : mapperIds) {
                if (shouldContinuePropagation(session, runId)) {
                    break;
                }

                Mapper mapper = requireMapper(mapperId);
                List<Command> commands = mapper.map(bundle.event(), context);

                for (Command command : commands) {
                    if (shouldContinuePropagation(session, runId)) {
                        break;
                    }

                    if (!sessionControl.retain()) {
                        break;
                    }

                    boolean offered = offerBundle(new CommandBundle(session, command));
                    if (!offered) {
                        sessionControl.release();
                        continue;
                    }

                    propagated = true;
                }
            }

            // 注意：COMPLETED 不由 Runtime 显式 mark
            // session 内部可根据状态 + workCount 自行达成 completed/canceled
            // 如果后续你决定需要一个 notifyNoMorePropagation()，可以再加一个很窄的方法
        } catch (Throwable ex) {
            sessionControl.markFailed(ex);
        } finally {
            sessionControl.release();
        }
    }

    private void handleCommandBundle(CommandBundle bundle, long runId) {
        RuntimeSession session = bundle.runtimeSession();
        RuntimeSessionControl sessionControl = requireSessionControl(session);

        try {
            if (shouldSkipExecution(session)) {
                return;
            }

            ActionContext context = createActionContext(session, bundle.command(), runId);

            List<ComponentId> actionIds = router.routeCommand(bundle.command());
            for (ComponentId actionId : actionIds) {
                if (shouldContinuePropagation(session, runId)) {
                    break;
                }

                Action action = requireAction(actionId);
                action.execute(bundle.command(), context);
            }
        } catch (Throwable ex) {
            sessionControl.markFailed(ex);
        } finally {
            sessionControl.release();
        }
    }

    private void cleanup(RootTriggerControl rootTrigger, RuntimeSessionControl session) {
        try {
            rootTrigger.removeSession(session.id());
        } catch (Exception ignored) {
            // keep cleanup idempotent
        }

        try {
            sessionRegistry.remove(session.id());
        } catch (Exception ignored) {
            // keep cleanup idempotent
        }

        if (runtimeStatus == Status.STOPPING && sessionRegistry.sessions().isEmpty()) {
            finishStop(activeRunId);
        }

        // publishRuntimeEvent(...)
    }

    private void finishStop(long runId) {
        synchronized (lock) {
            if (activeRunId != runId) {
                return;
            }

            if (runtimeStatus != Status.STOPPING) {
                return;
            }

            queue = null;
            workerThread = null;
            activeRunId = -1L;
            runtimeStatus = Status.STOPPED;
        }

        // publishRuntimeEvent(...)
    }

    private boolean cannotAcceptRootEvent(long runId) {
        return runtimeStatus != Status.STARTED || activeRunId != runId || queue == null;
    }

    private boolean acceptPropagation(long runId, RuntimeSession session) {
        return runtimeStatus == Status.STARTED && activeRunId == runId && queue != null && session.isActive();
    }

    private boolean shouldSkipExecution(RuntimeSession session) {
        return session.isCancelRequested() || session.isTerminated();
    }

    private boolean shouldContinuePropagation(RuntimeSession session, long runId) {
        return !acceptPropagation(runId, session);
    }

    private boolean acceptRootEvent(RootTriggerControl rootTrigger) {
        Set<RuntimeSession> liveSessions = rootTrigger.currentSessions().stream()
                .filter(session -> !session.isTerminated())
                .collect(java.util.stream.Collectors.toSet());

        return switch (rootTrigger.concurrencyPolicy()) {
            case ALLOW_PARALLEL -> true;
            case REJECT_NEW -> liveSessions.isEmpty();
            case CANCEL_PREVIOUS -> {
                if (liveSessions.isEmpty()) yield true;
                requestCancel(liveSessions);
                yield false;
            }
            case TOGGLE -> {
                if (liveSessions.isEmpty()) yield true;
                requestCancel(liveSessions);
                yield false;
            }
        };
    }

    private void requestCancel(Set<RuntimeSession> sessions) {
        for (RuntimeSession session : sessions) {
            RuntimeSessionControl control = requireSessionControl(session);
            control.markCancelRequested();
        }
    }

    private boolean offerBundle(RuntimeBundle bundle) {
        BlockingQueue<RuntimeBundle> currentQueue = this.queue;
        return currentQueue != null && currentQueue.offer(bundle);
    }

    private RuntimeSessionControl requireSessionControl(RuntimeSession session) {
        if (session instanceof RuntimeSessionControl control) return control;
        throw new IllegalStateException("RuntimeSession must implement RuntimeSessionControl: " + session.getClass().getName());
    }

    private RootTriggerControl requireRootTriggerControl(RootTrigger rootTrigger) {
        if (rootTrigger instanceof RootTriggerControl control) {
            return control;
        }

        throw new IllegalStateException(
                "RootTrigger must implement RootTriggerControl: " + rootTrigger.delegate().id()
        );
    }

    private Mapper requireMapper(ComponentId mapperId) {
        Mapper mapper = mappers.get(mapperId);
        if (mapper == null) {
            throw new IllegalStateException("Missing mapper: " + mapperId);
        }
        return mapper;
    }

    private Action requireAction(ComponentId actionId) {
        Action action = actions.get(actionId);
        if (action == null) {
            throw new IllegalStateException("Missing action: " + actionId);
        }
        return action;
    }

    private MapperContext createMapperContext(RuntimeSession session, Event event) {
        SessionContext sessionContext = new SessionContext(
                session,
                globalSession,
                pathValueAccessor,
                event.payload(),
                null
        );
        return new DefaultMapperContext(sessionContext);
    }

    private ActionContext createActionContext(RuntimeSession session, Command command, long runId) {
        SessionContext sessionContext = new SessionContext(
                session,
                globalSession,
                pathValueAccessor,
                null,
                command.params()
        );

        EventSink eventSink = event -> {
            RuntimeSessionControl sessionControl = requireSessionControl(session);

            if (shouldContinuePropagation(session, runId)) {
                return;
            }

            if (!sessionControl.retain()) {
                return;
            }

            boolean offered = offerBundle(new EventBundle(session, event));
            if (!offered) {
                sessionControl.release();
            }
        };

        return new DefaultActionContext(sessionContext, eventSink);
    }

    @Override
    public Status status() {
        return runtimeStatus;
    }
}
