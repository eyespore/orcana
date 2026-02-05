package cc.pineclone.workflow.api.action;

import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {

    private final Map<String, Object> data = new HashMap<>();

    public <T> T get(String key, Class<T> clazz) {
        Object value = data.get(key);
        if (value == null) return null;
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        throw new ClassCastException("Expected " + clazz + " but got " + value.getClass());
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

}
