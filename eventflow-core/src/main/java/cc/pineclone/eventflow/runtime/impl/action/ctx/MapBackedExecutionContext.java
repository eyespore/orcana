package cc.pineclone.eventflow.runtime.impl.action.ctx;

import cc.pineclone.eventflow.core.api.context.ExecutionContext;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

abstract class MapBackedExecutionContext implements ExecutionContext {

    private final ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<>();

    @Override
    public Object get(String key) {
        return data.get(key);
    }

    @Override
    public void put(String key, Object value) {
        Objects.requireNonNull(key, "key");
        if (value == null) {
            data.remove(key);
        } else {
            data.put(key, value);
        }
    }
}
