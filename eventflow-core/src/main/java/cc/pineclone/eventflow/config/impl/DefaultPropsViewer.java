package cc.pineclone.eventflow.config.impl;

import cc.pineclone.eventflow.config.api.PropsCoercer;
import cc.pineclone.eventflow.config.api.PropsViewer;

import java.util.*;
import java.util.function.BiFunction;

public class DefaultPropsViewer implements PropsViewer {

    private final Map<String, Object> props;
    private final String basePath;
    private final PropsCoercer coercer;

    public DefaultPropsViewer(Map<String, Object> props, String basePath, PropsCoercer coercer) {
        this.props = new LinkedHashMap<>(Objects.requireNonNull(props, "props"));
        this.basePath = (basePath == null || basePath.isBlank()) ? "properties" : basePath;
        this.coercer = Objects.requireNonNull(coercer, "coercer");
    }

    @Override
    public Map<String, Object> freezeProps() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(props));
    }

    @Override
    public Optional<Object> raw(String key) {
        if (key == null || key.isBlank()) return Optional.empty();
        if (!props.containsKey(key)) return Optional.empty();
        return Optional.ofNullable(props.get(key));
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(type, "type");

        Object raw = props.get(key);
        return coercer.required(raw, type, pathOf(key));
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T defVal) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(type, "type");

        Object raw = props.get(key);
        return coercer.optional(raw, type, pathOf(key), defVal);
    }

    @Override
    public <T> Set<T> getSet(String key, Class<T> elementType) {
        Objects.requireNonNull(elementType, "elementType");
        return getSet(key, (raw, path) -> coercer.required(raw, elementType, path));
    }

    @Override
    public <T> Set<T> getSetOrDefault(String key, Class<T> elementType, Set<T> defVal) {
        Objects.requireNonNull(defVal, "defVal");
        Objects.requireNonNull(elementType, "elementType");
        return getSetOrDefault(key, (raw, path) -> coercer.required(raw, elementType, path), defVal);
    }

    @Override
    public <K, V> Map<K, V> getMap(String key, Class<K> keyType, Class<V> valueType) {
        Objects.requireNonNull(keyType, "keyType");
        Objects.requireNonNull(valueType, "valueType");
        return getMap(
                key,
                (raw, path) -> coercer.required(raw, keyType, path),
                (raw, path) -> coercer.required(raw, valueType, path)
        );
    }

    @Override
    public <K, V> Map<K, V> getMapOrDefault(
            String key,
            Class<K> keyType, Class<V> valueType, Map<K, V> defVal
    ) {
        Objects.requireNonNull(defVal, "defVal");
        Objects.requireNonNull(keyType, "keyType");
        Objects.requireNonNull(valueType, "valueType");
        return getMapOrDefault(
                key,
                (raw, path) -> coercer.required(raw, keyType, path), (raw, path) -> coercer.required(raw, valueType, path), defVal
        );
    }

    @Override
    public <T> Set<T> getSet(String key, BiFunction<Object, String, T> elementGetter) {
        return getSetOrDefault(key, true, elementGetter, null);
    }

    @Override
    public <T> Set<T> getSetOrDefault(String key, BiFunction<Object, String, T> elementGetter, Set<T> defVal) {
        return getSetOrDefault(key, false, elementGetter, defVal);
    }

    private <T> Set<T> getSetOrDefault(
            String key,
            boolean required, BiFunction<Object, String, T> elementGetter, Set<T> defVal
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(elementGetter, "elementGetter");

        Object raw = props.get(key);
        String path = pathOf(key);

        if (raw == null || (raw instanceof String s && s.isBlank())) {
            if (required) throw new IllegalArgumentException(path + " is required");
            return Set.copyOf(defVal == null ? Set.of() : defVal);
        }

        if (!(raw instanceof Iterable<?> it)) {
            throw new IllegalArgumentException(path + " must be a list/set (Iterable) but got: " + raw.getClass().getSimpleName());
        }

        LinkedHashSet<T> tmp = new LinkedHashSet<>();
        int i = 0;
        for (Object elem : it) {
            tmp.add(elementGetter.apply(elem, path + "[" + i + "]"));
            i++;
        }
        return Set.copyOf(tmp);
    }

    @Override
    public <K, V> Map<K, V> getMap(
            String key,
            BiFunction<Object, String, K> keyGetter,
            BiFunction<Object, String, V> valueGetter
    ) {
        return getMapOrDefault(key, null, true, keyGetter, valueGetter);
    }

    @Override
    public <K, V> Map<K, V> getMapOrDefault(
            String key,
            BiFunction<Object, String, K> keyGetter, BiFunction<Object, String, V> valueGetter, Map<K, V> defVal
    ) {
        return getMapOrDefault(key, defVal, false, keyGetter, valueGetter);
    }

    private <K, V> Map<K, V> getMapOrDefault(
            String key,
            Map<K, V> defVal,
            boolean required,
            BiFunction<Object, String, K> keyGetter,
            BiFunction<Object, String, V> valueGetter
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(keyGetter, "keyGetter");
        Objects.requireNonNull(valueGetter, "valueGetter");

        Object raw = props.get(key);
        String path = pathOf(key);

        if (raw == null || (raw instanceof String s && s.isBlank())) {
            if (required) throw new IllegalArgumentException(path + " is required");
            return Collections.unmodifiableMap(new LinkedHashMap<>(defVal == null ? Map.of() : defVal));
        }

        if (!(raw instanceof Map<?, ?> m)) {
            throw new IllegalArgumentException(path + " must be a Map but got: " + raw.getClass().getSimpleName());
        }

        Map<K, V> tmp = new LinkedHashMap<>();
        for (Map.Entry<?, ?> e : m.entrySet()) {
            K k = keyGetter.apply(e.getKey(), path + ".<key>");
            V v = valueGetter.apply(e.getValue(), path + "[" + k + "]");

            if (tmp.containsKey(k)) {
                throw new IllegalArgumentException(path + " has duplicate key after get: " + k);
            }
            tmp.put(k, v);
        }

        return Collections.unmodifiableMap(new LinkedHashMap<>(tmp));
    }

    @Override
    public <T> List<T> getList(String key, Class<T> elementType) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(elementType, "elementType");
        return getList(key, (raw, path) -> coercer.required(raw, elementType, path));
    }

    @Override
    public <T> List<T> getListOrDefault(String key, Class<T> elementType, List<T> defVal) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(elementType, "elementType");
        Objects.requireNonNull(defVal, "defVal");
        return getListOrDefault(key, (raw, path) -> coercer.required(raw, elementType, path), defVal);
    }

    @Override
    public <T> List<T> getList(String key, BiFunction<Object, String, T> elementGetter) {
        return getListOrDefault(key, true, elementGetter, null);
    }

    @Override
    public <T> List<T> getListOrDefault(String key, BiFunction<Object, String, T> elementGetter, List<T> defVal) {
        return getListOrDefault(key, false, elementGetter, defVal);
    }

    private <T> List<T> getListOrDefault(
            String key,
            boolean required,
            BiFunction<Object, String, T> elementGetter,
            List<T> defVal
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(elementGetter, "elementGetter");

        Object raw = props.get(key);
        String path = pathOf(key);

        if (raw == null || (raw instanceof String s && s.isBlank())) {
            if (required) throw new IllegalArgumentException(path + " is required");
            return List.copyOf(defVal == null ? List.of() : defVal);
        }

        if (!(raw instanceof Iterable<?> it)) {
            throw new IllegalArgumentException(path + " must be a list (Iterable) but got: " + raw.getClass().getSimpleName());
        }

        ArrayList<T> tmp = new ArrayList<>();
        int i = 0;
        for (Object elem : it) {
            tmp.add(elementGetter.apply(elem, path + "[" + i + "]"));
            i++;
        }
        return List.copyOf(tmp);
    }


    private String pathOf(String key) {
        return basePath + "." + key;
    }

}
