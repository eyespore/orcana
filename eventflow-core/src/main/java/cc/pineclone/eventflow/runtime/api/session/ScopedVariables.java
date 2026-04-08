package cc.pineclone.eventflow.runtime.api.session;

import java.util.Map;
import java.util.Optional;

@Deprecated
public interface ScopedVariables {

    boolean contains(String key);

    Optional<Object> get(String key);

    <T> Optional<T> get(String key, Class<T> clazz);

    void put(String key, Object value);

    Object remove(String key);

    Map<String, Object> asMap();

}
