package cc.pineclone.eventflow.plugin.api.template;

import java.util.Map;
import java.util.Objects;

public record CommandDesc(
        String name,  /* 命令 */
        String description,  /* 命令描述 */
        Map<String, String> params  /* 命令参数描述 */
) {

    public CommandDesc {
        Objects.requireNonNull(name, "commandName");
        description = (description == null) ? "" : description;
        params = (params == null) ? Map.of() : Map.copyOf(params);
    }

}
