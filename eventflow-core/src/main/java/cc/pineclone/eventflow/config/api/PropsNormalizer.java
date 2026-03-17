package cc.pineclone.eventflow.config.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public interface PropsNormalizer {

    Map<String, Object> freezeProps();

    void aliasKeyStrict(String canonicalKey, String... aliasKeys);

    <T> void normalize(String key, Class<T> type, T defVal);

    <T> void normalizeList(
            String key,
            Class<T> elementType,
            List<T> defVal
    );

    <T> void normalizeList(
            String key,
            BiFunction<Object, String, T> elementNormalizer,
            List<T> defVal
    );

    <T> void normalizeSet(
            String key,
            Class<T> elementType,
            Set<T> defVal);

    <T> void normalizeSet(
            String key,
            BiFunction<Object, String, T> elementNormalizer,
            Set<T> defVal
    );

    <K, V> void normalizeMap(String key, Class<K> keyType, Class<V> valueType, Map<K, V> defVal);

    <K, V> void normalizeMap(
            String key,
            BiFunction<Object, String, K> keyNormalizer,
            BiFunction<Object, String, V> valueNormalizer,
            Map<K, V> defVal
    );

}
