package cc.pineclone.eventflow.config.impl;

import cc.pineclone.eventflow.config.api.PropsCoercer;
import cc.pineclone.eventflow.config.api.PropsNormalizer;

import java.util.*;
import java.util.function.BiFunction;

public class DefaultPropsNormalizer implements PropsNormalizer {

    private final Map<String, Object> props;     // 工作副本（可变）
    private final String basePath;               // 通常 "properties"
    private final PropsCoercer coercer;

    public DefaultPropsNormalizer(Map<String, Object> props, String basePath, PropsCoercer coercer) {
        this.props = Objects.requireNonNull(props, "props");
        this.basePath = (basePath == null || basePath.isBlank()) ? "properties" : basePath;
        this.coercer = Objects.requireNonNull(coercer, "coercer");
    }

    @Override
    public Map<String, Object> freezeProps() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(props));
    }

    @Override
    public void aliasKeyStrict(String canonicalKey, String... aliasKeys) {
        Objects.requireNonNull(canonicalKey, "canonicalKey");
        Objects.requireNonNull(aliasKeys, "aliasKeys");

        if (props.containsKey(canonicalKey)) {
            Object canonicalVal = props.get(canonicalKey);

            for (String alias : aliasKeys) {
                if (alias == null || alias.isBlank()) continue;
                if (!props.containsKey(alias)) continue;

                Object aliasVal = props.get(alias);
                if (!Objects.equals(canonicalVal, aliasVal)) {
                    throw new IllegalArgumentException(
                            "Conflicting properties: " + pathOf(canonicalKey) + "=" + canonicalVal +
                                    " but alias " + pathOf(alias) + "=" + aliasVal
                    );
                }
            }

            for (String alias : aliasKeys) {
                if (alias == null || alias.isBlank()) continue;
                if (alias.equals(canonicalKey)) continue;
                props.remove(alias);
            }
            return;
        }

        boolean found = false;
        Object chosen = null;
        String chosenKey = null;

        for (String alias : aliasKeys) {
            if (alias == null || alias.isBlank()) continue;
            if (!props.containsKey(alias)) continue;

            Object v = props.get(alias);
            if (!found) {
                found = true;
                chosen = v;
                chosenKey = alias;
            } else if (!Objects.equals(chosen, v)) {
                throw new IllegalArgumentException(
                        "Conflicting properties among aliases of " + pathOf(canonicalKey) + ": " +
                                pathOf(chosenKey) + "=" + chosen + " but " + pathOf(alias) + "=" + v
                );
            }
        }

        if (found) {
            props.put(canonicalKey, chosen);
            for (String alias : aliasKeys) {
                if (alias == null || alias.isBlank()) continue;
                if (alias.equals(canonicalKey)) continue;
                props.remove(alias);
            }
        }
    }

    @Override
    public <T> void normalize(String key, Class<T> type, T defVal) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(type, "type");

        Object raw = props.get(key);
        T v = coercer.optional(raw, type, pathOf(key), defVal);
        props.put(key, v);
    }

    @Override
    public <T> void normalizeList(String key, Class<T> elementType, List<T> defVal) {
        Objects.requireNonNull(elementType, "elementType");
        normalizeList(key, (raw, path) -> coercer.required(raw, elementType, path), defVal);
    }

    @Override
    public <T> void normalizeList(String key, BiFunction<Object, String, T> elementNormalizer, List<T> defVal) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(defVal, "defVal");
        Objects.requireNonNull(elementNormalizer, "elementNormalizer");

        Object raw = props.get(key);
        String path = pathOf(key);

        List<T> out;
        if (raw == null) {
            out = List.copyOf(defVal);
        } else if (raw instanceof Iterable<?> it) {
            ArrayList<T> tmp = new ArrayList<>();
            int i = 0;
            for (Object elem : it) {
                tmp.add(elementNormalizer.apply(elem, path + "[" + i + "]"));
                i++;
            }
            out = List.copyOf(tmp);
        } else {
            throw new IllegalArgumentException(path + " must be a list (Iterable) but got: " + raw.getClass().getSimpleName());
        }

        props.put(key, out);
    }

    @Override
    public <T> void normalizeSet(String key, Class<T> elementType, Set<T> defVal) {
        normalizeSet(key, (raw, path) -> coercer.required(raw, elementType, path), defVal);
    }

    @Override
    public <T> void normalizeSet(
            String key,
            BiFunction<Object, String, T> elementNormalizer, Set<T> defVal
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(defVal, "defVal");
        Objects.requireNonNull(elementNormalizer, "elementNormalizer");

        Object raw = props.get(key);
        String path = pathOf(key);

        Set<T> out;
        if (raw == null) {
            out = Set.copyOf(defVal);
        } else if (raw instanceof Iterable<?> it) {
            LinkedHashSet<T> tmp = new LinkedHashSet<>();
            int i = 0;
            for (Object elem : it) {
                tmp.add(elementNormalizer.apply(elem, path + "[" + i + "]"));
                i++;
            }
            out = Set.copyOf(tmp);
        } else {
            throw new IllegalArgumentException(path + " must be a list/set (Iterable) but got: " + raw.getClass().getSimpleName());
        }

        props.put(key, out);
    }

    @Override
    public <K, V> void normalizeMap(String key, Class<K> keyType, Class<V> valueType, Map<K, V> defVal) {
        normalizeMap(
                key,
                (raw, path) -> coercer.required(raw, keyType, path), (raw, path) -> coercer.required(raw, valueType, path), defVal
        );
    }

    @Override
    public <K, V> void normalizeMap(
            String key,
            BiFunction<Object, String, K> keyNormalizer, BiFunction<Object, String, V> valueNormalizer, Map<K, V> defVal
    ) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(defVal, "defVal");
        Objects.requireNonNull(keyNormalizer, "keyNormalizer");
        Objects.requireNonNull(valueNormalizer, "valueNormalizer");

        Object raw = props.get(key);
        String path = pathOf(key);

        Map<K, V> out;
        if (raw == null) {
            /* 使用 LinkedHashMap 保证迭代顺序不变 */
            out = Collections.unmodifiableMap(new LinkedHashMap<>(defVal));
        } else if (raw instanceof Map<?, ?> m) {
            Map<K, V> tmp = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                K k = keyNormalizer.apply(e.getKey(), path + ".<key>");
                V v = valueNormalizer.apply(e.getValue(), path + "[" + k + "]");

                if (tmp.containsKey(k)) {
                    throw new IllegalArgumentException(path + " has duplicate key after normalization: " + k);
                }
                tmp.put(k, v);
            }
            out = Collections.unmodifiableMap(new LinkedHashMap<>(tmp));
        } else {
            throw new IllegalArgumentException(path + " must be a Map but got: " + raw.getClass().getSimpleName());
        }

        props.put(key, out);
    }

    private String pathOf(String key) {
        return basePath + "." + key;
    }
}
