package cc.pineclone.workflow.api.trigger.event;

import cc.pineclone.workflow.api.trigger.Trigger;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.EventObject;
import java.util.Map;
import java.util.Objects;

@ToString
public final class TriggerEvent extends EventObject {

    @Serial
    private static final long serialVersionUID = 1243142141123121252L;

    @Getter
    private final TriggerEventIdentity identity;

    /**
     * 时间戳，记录TriggerEvent触发的时间节点
     */
    @Getter
    private final long timestamp;

    /**
     * 数据传递容器
     */
    @Getter
    private final Map<String,Object> meta;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public TriggerEvent(Trigger source, TriggerEventIdentity identity) {
        this(source, identity, Map.of());
    }

    public TriggerEvent(Trigger source, TriggerEventIdentity identity, Map<String, Object> meta) {
        super(source);
        this.identity = identity;
        this.timestamp = System.currentTimeMillis();
        this.meta = meta;
    }

    @Nullable
    public <T> T get(String key, Class<T> clazz) {
        Object value = meta.get(key);
        if (value == null) return null;
        if (clazz.isInstance(value)) return clazz.cast(value);
        throw new ClassCastException("Expected type " + clazz + " but got " + value.getClass());
    }

    public void put(String key, Object value) {
        meta.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TriggerEvent that = (TriggerEvent) o;
        return Objects.equals(identity, that.identity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identity);
    }
}
