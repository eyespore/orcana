package cc.pineclone.eventflow.core.api.command;

import cc.pineclone.eventflow.core.api.action.ActionIdentity;

import java.util.List;
import java.util.Objects;

@Deprecated
public record ActionCommandBatch(
    Mode mode,
    List<Step> steps
) {

    public ActionCommandBatch {
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(steps, "steps");
        // 视偏好：你也可以允许空 steps，并在 engine 里直接返回 OK
        if (steps.isEmpty()) {
            throw new IllegalArgumentException("steps must not be empty");
        }
    }

    public enum Mode {
        AFTER_ACCEPTED,
        AFTER_SUCCEEDED
    }

    /* 批处理子命令 */
    public record Step(
            ActionIdentity target,
            Command command
    ) {
        public Step {
            Objects.requireNonNull(target, "target");
            Objects.requireNonNull(command, "command");
        }
    }

    /* 每一步批处理子命令的结果 */
    public enum StepOutcome {  // TODO: 封装为更详细的步骤结果表达DTO
        SUCCEEDED,
        FAILED
    }

}


