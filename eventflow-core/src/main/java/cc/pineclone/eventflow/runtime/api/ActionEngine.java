package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.action.ActionIdentity;
import cc.pineclone.eventflow.core.api.command.Command;
import cc.pineclone.eventflow.core.api.command.ActionCommandBatch;
import cc.pineclone.eventflow.core.api.command.CommandAcceptanceResult;
import cc.pineclone.eventflow.core.api.command.CommandApplicationResult;

import java.util.concurrent.CompletionStage;

@Deprecated
public interface ActionEngine {

    SubmitAck submit(ActionIdentity actionIdentity, Command command);

    SubmitAck submitBatch(ActionCommandBatch batch);

    sealed interface SubmitAck permits SubmitAck.Denied, SubmitAck.Admitted {
        record Denied(String message) implements SubmitAck {}
        record Admitted(
                CommandAcceptanceResult acceptance,  /* 及时响应 */
                CompletionStage<CommandApplicationResult> application  /* 命令执行完成响应 */
        ) implements SubmitAck {}
    }
}
