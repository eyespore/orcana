package cc.pineclone.eventflow.config.api;

public interface PropsCoercionRule {

    default int order() {
        return 0;
    };

    boolean supports(Object raw, Class<?> targetType);

    <T> T coerce(Object raw, Class<T> targetType, String path);

}
