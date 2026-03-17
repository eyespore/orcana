package cc.pineclone.eventflow.config.api;

public interface PropsCoercer {

    <T> T required(Object raw, Class<T> type, String path);

    <T> T optional(Object raw, Class<T> type, String path, T defVal);

}
