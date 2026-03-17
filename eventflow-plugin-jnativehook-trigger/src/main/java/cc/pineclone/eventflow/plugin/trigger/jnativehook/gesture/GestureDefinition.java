package cc.pineclone.eventflow.plugin.trigger.jnativehook.gesture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestureDefinition {

    private GestureType type;
    private Map<String, Object> params;

    public enum GestureType {
        CLICK,
        HOLD
    }

    public long getLong(String key, long defaultValue) {
        Object val = params.get(key);
        if (val instanceof Number) return ((Number) val).longValue();
        return defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        Object val = params.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        return defaultValue;
    }

    public String getString(String key, String defaultValue) {
        Object val = params.get(key);
        return val != null ? val.toString() : defaultValue;
    }

}
