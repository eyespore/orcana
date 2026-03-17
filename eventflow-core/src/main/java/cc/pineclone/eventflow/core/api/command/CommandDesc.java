package cc.pineclone.eventflow.core.api.command;

import java.util.Map;
import java.util.Objects;

public record CommandDesc(
        String command,  /* 命令 */
        String description,  /* 命令描述 */
        Map<String, String> params  /* 命令参数描述 */
) {

    public CommandDesc {
        Objects.requireNonNull(command, "command");
        description = (description == null) ? "" : description;
        params = (params == null) ? Map.of() : Map.copyOf(params);
    }

}
