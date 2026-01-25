package cc.pineclone.automation.definition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* 宏参数封装 */
public class MacroParams {

    private final Map<String, Object> params = new HashMap<>();

    public Set<String> keySet() {
        return params.keySet();
    }

    public void put(String key, Object value) {
        params.put(key, value);
    }

    public Object get(String key) {
        return params.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = params.get(key);
        if (value == null) return null;
        if (type.isInstance(value)) {
            return (T) value;
        }

        if (type == Integer.class && value instanceof Number) return (T) Integer.valueOf(((Number) value).intValue());
        if (type == Long.class && value instanceof Number) return (T) Long.valueOf(((Number) value).longValue());
        if (type == Double.class && value instanceof Number) return (T) Double.valueOf(((Number) value).doubleValue());
        if (type == Boolean.class && value instanceof Boolean) return (T) value;
        throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to " + type);
    }
}
