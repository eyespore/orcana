package cc.pineclone.eventflow.config.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public interface PropsViewer {

    Map<String, Object> freezeProps();

    Optional<Object> raw(String key);

    <T> T get(String key, Class<T> type);

    <T> T getOrDefault(
            String key,
            Class<T> type,
            T defVal
    );

    <T> Set<T> getSet(
            String key,
            Class<T> elementType
    );

    <T> Set<T> getSetOrDefault(
            String key,
            Class<T> elementType,
            Set<T> defVal
    );

    <T> Set<T> getSet(
            String key,
            BiFunction<Object, String, T> elementGetter
    );

    <T> Set<T> getSetOrDefault(
            String key,
            BiFunction<Object, String, T> elementGetter,
            Set<T> defVal
    );

    <K, V> Map<K, V> getMap(
            String key,
            Class<K> keyType,
            Class<V> valueType
    );

    <K, V> Map<K, V> getMapOrDefault(
            String key,
            Class<K> keyType,
            Class<V> valueType,
            Map<K, V> defVal
    );

    <K, V> Map<K, V> getMap(
            String key,
            BiFunction<Object, String, K> keyGetter,
            BiFunction<Object, String, V> valueGetter
    );

    <K, V> Map<K, V> getMapOrDefault(
            String key,
            BiFunction<Object, String, K> keyGetter,
            BiFunction<Object, String, V> valueGetter,
            Map<K, V> defVal
    );

    <T> List<T> getList(String key, Class<T> elementType);

    <T> List<T> getListOrDefault(String key, Class<T> elementType, List<T> defVal);

    <T> List<T> getList(String key, BiFunction<Object, String, T> elementGetter);

    <T> List<T> getListOrDefault(String key, BiFunction<Object, String, T> elementGetter, List<T> defVal);
}
