package cc.pineclone.eventflow.config.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultPropsCoercer {

    private final ObjectMapper mapper;

    public DefaultPropsCoercer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T required(Object raw, Class<T> type, String path) {
        if (raw == null) throw new IllegalArgumentException(path + " is required");
        if (raw instanceof String s && s.isBlank()) throw new IllegalArgumentException(path + " is required");
        return convert(raw, type, path);
    }

    public <T> T optional(Object raw, Class<T> type, String path, T defVal) {
        if (raw == null) return defVal;
        if (raw instanceof String s && s.isBlank()) return defVal;
        return convert(raw, type, path);
    }

    private <T> T convert(Object raw, Class<T> type, String path) {
        try {
            return mapper.convertValue(raw, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(path + " must be " + type.getSimpleName(), e);
        }
    }

}
