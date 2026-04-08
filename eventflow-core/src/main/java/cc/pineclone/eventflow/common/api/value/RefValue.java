package cc.pineclone.eventflow.common.api.value;

public record RefValue(
        Scope scope,
        String path
) implements ValueBinding {

    public enum Scope {
        EVENT_PAYLOAD,
        COMMAND_PARAMS,
        RUNTIME_VARS,
        GLOBAL_VARS
    }
}
