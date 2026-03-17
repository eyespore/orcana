package cc.pineclone.eventflow.runtime.impl.action;

import cc.pineclone.eventflow.core.api.action.*;
import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.context.*;
import cc.pineclone.eventflow.runtime.api.FlowSessionManager;
import cc.pineclone.eventflow.runtime.impl.action.ctx.DefaultFlowSession;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DefaultActionEngine implements ActionEngine {

    private final ActionRegistry registry;  /* Action 注册 */
    private final FlowSessionManager sessionManager;  /* 会话管理器 */
    private final CallContextFactory callContextFactory;  /* 上下文工厂 */

    private final Clock clock;
    private final Executor executor;

    private final GlobalContext globalCtx;

    public DefaultActionEngine(
            ActionRegistry registry,
            FlowSessionManager sessionManager,
            CallContextFactory callContextFactory,
            GlobalContextFactory globalContextFactory,
            Clock clock,
            Executor executor
    ) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.sessionManager = Objects.requireNonNull(sessionManager, "sessionManager");
        this.callContextFactory = Objects.requireNonNull(callContextFactory, "callContextFactory");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.executor = Objects.requireNonNull(executor, "executor");
        this.globalCtx = Objects.requireNonNull(globalContextFactory.createContext(), "global");
    }

    @Override
    public SubmitAck submit(ActionIdentity actionIdentity, Command command) {
        Objects.requireNonNull(actionIdentity, "actionIdentity");
        Objects.requireNonNull(command, "command");

        /* 通过注册表获取目标动作 */
        Action action = registry.get(actionIdentity).orElse(null);
        if (action == null) {  /* 未找到目标动作 */
            return new SubmitAck.Denied("action not found: " + actionIdentity);
        }

        /* 确保目标动作存在可用的会话，如果没有会话此时会创建一个新的会话 */
        ActionSession session = sessionManager.getOrCreateNewSession(action);
        UUID sessionId = session.sessionId();

        /* 挂载动作完成时的回调，当动作被标记完成时，会话会被清除 */
        if (session.markCleanupAttachedOnce()) {
            session.action().handler().outcome().whenCompleteAsync((r, ex) ->
                    sessionManager.removeSession(actionIdentity, sessionId), executor);
        }

        /* 构建调用上下文 */
        Instant now = Instant.now(clock);
        CallContext callCtx = callContextFactory.createContext(actionIdentity, sessionId, command, now);

        SessionContext sessionCtx = session.sessionContext();
        FlowSession ctx = new DefaultFlowSession(globalCtx, sessionCtx, callCtx); // TODO: 改用享元

        /* 并行执行命令调用 */
        return sessionManager.attachSessionSync(sessionId, s -> dispatchToHandler(s, ctx, command));
    }

    /* 执行批处理任务 */
    @Override
    public SubmitAck submitBatch(ActionCommandBatch batch) {
        Objects.requireNonNull(batch, "batch");

        /* 同步预检：只做“无副作用”的确定性检查，避免批处理中途才发现是配置/输入问题 */
        for (int i = 0; i < batch.steps().size(); i++) {
            ActionCommandBatch.Step step = batch.steps().get(i);
            if (registry.get(step.target()).isEmpty()) {
                return new SubmitAck.Denied("action not found at step[" + i + "]: " + step.target());
            }
        }

        /* 构建一个“头”，通过 themCompose 链接后续的批处理任务响应，确保第一个批处理任务一定以 SUCCEEDED 结束 */
        CompletableFuture<ActionCommandBatch.StepOutcome> chain =
                CompletableFuture.completedFuture(ActionCommandBatch.StepOutcome.SUCCEEDED);

        /* 遍历所有的批处理子命令 */
        for (ActionCommandBatch.Step step : batch.steps()) {
            chain = chain.thenCompose(prev -> {
                if (prev != ActionCommandBatch.StepOutcome.SUCCEEDED) {
                    /* 若上一条命令执行失败，那么直接结束整条调用链（由于使用“头”，因此第一个结果必然是SUCCEEDED） */
                    return CompletableFuture.completedFuture(prev);
                }

                /* 将批处理命令通过 submit 作为单条命令提交 */
                SubmitAck ack = submit(step.target(), step.command());

                /* -------- 对 Engine 提交结果 SubmitAck 进行有效性判断 -------- */
                /* 若命令提交被 Engine 直接拒绝，那么视为整个批处理命令提交失败 */
                if (ack instanceof SubmitAck.Denied) {
                    return CompletableFuture.completedFuture(ActionCommandBatch.StepOutcome.FAILED);
                }

                /* 若某一条命令被拒绝，那么视为整个批处理任务结束且失败 */
                if (!(ack instanceof SubmitAck.Admitted admitted)) {
                    return CompletableFuture.completedFuture(ActionCommandBatch.StepOutcome.FAILED);
                }

                /* -------- 对 ActionHandler 提交命令后返回的结果进行有效性判断 -------- */
                /* 若命令提交成功，但是 Handler 仍然不接受命令（非ACCEPTED），视为相响应失败 */
                if (admitted.acceptance().status() != CommandAcceptanceResult.Status.ACCEPTED) {
                    return CompletableFuture.completedFuture(ActionCommandBatch.StepOutcome.FAILED);
                }

                /* 命令提交成功，视批处理任务执行模式，决定立即执行下一个批处理子命令，还是等待上一个子命令执行完毕后，在执行下一个 */
                if (batch.mode() == ActionCommandBatch.Mode.AFTER_ACCEPTED) {
                    /* 采用“命令提交完毕后，立即执行下一个命令的形式” */
                    return CompletableFuture.completedFuture(ActionCommandBatch.StepOutcome.SUCCEEDED);
                }

                /* 否则必须等待上一个命令执行完毕后，才能执行下一个 */
                return admitted.application().thenApply(app ->
                        app.status() == CommandApplicationResult.Status.SUCCEEDED
                        ? ActionCommandBatch.StepOutcome.SUCCEEDED
                        : ActionCommandBatch.StepOutcome.FAILED).toCompletableFuture();
            });
        }

        /* 及时响应，表示接受所有的批处理任务提交 */
        CommandAcceptanceResult acceptance = new CommandAcceptanceResult(
                CommandAcceptanceResult.Status.ACCEPTED,
                "batch accepted, steps=" + batch.steps().size() + ", mode=" + batch.mode()
        );

        CompletableFuture<CommandApplicationResult> application = chain.thenApply(outcome -> outcome == ActionCommandBatch.StepOutcome.SUCCEEDED
                ? new CommandApplicationResult(CommandApplicationResult.Status.SUCCEEDED, "batch succeeded")
                : new CommandApplicationResult(CommandApplicationResult.Status.FAILED, "batch failed")
        );

        return new SubmitAck.Admitted(acceptance, application);
    }

    /* 将调用上下文派发往具体的会话并执行任务 */
    private SubmitAck dispatchToHandler(ActionSession session, FlowSession ctx, Command command) {
        ActionHandler.CommandHandlingReceipt receipt;
        try {
            receipt = session.action().handler().handle(ctx, command);
        } catch (Throwable t) {
            /* 执行命令期间抛出异常，命令执行失败 */
            return new SubmitAck.Admitted(
                    new CommandAcceptanceResult(CommandAcceptanceResult.Status.FAILED, t.getMessage()),
                    CompletableFuture.completedFuture(
                            new CommandApplicationResult(CommandApplicationResult.Status.FAILED, t.getMessage())
                    )
            );
        }

        return new SubmitAck.Admitted(receipt.acceptance(), receipt.application());
    }
}