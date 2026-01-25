package cc.pineclone.automation.trigger.source;

import cc.pineclone.automation.input.Key;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ToString
@Getter
@Builder
@Setter
public class InputSourceEvent {

    private static final Map<KeyOperationPair, InputSourceEvent> CACHE = new ConcurrentHashMap<>();

    // 享元静态工厂方法
    public static InputSourceEvent of(Operation operation, Key key) {
        KeyOperationPair pair = new KeyOperationPair(operation, key);
        return CACHE.computeIfAbsent(pair, k -> new InputSourceEvent(operation, key));
    }

    private Operation operation;  /* 按键为按下状态 */
    private Key key;  /* 描述用户按下的按键 */

    private InputSourceEvent(Operation operation, Key key) {
        this.operation = operation;
        this.key = key;
    }

    public enum Operation {
        KEY_PRESSED,
        KEY_RELEASED,
        MOUSE_WHEEL_MOVED,
        MOUSE_PRESSED,
        MOUSE_RELEASED,
    }

    private static class KeyOperationPair {
        private final Operation operation;
        private final Key key;

        public KeyOperationPair(Operation operation, Key key) {
            this.operation = operation;
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KeyOperationPair that = (KeyOperationPair) o;
            return operation == that.operation && key.equals(that.key);
        }

        @Override
        public int hashCode() {
            return 31 * operation.hashCode() + key.hashCode();
        }
    }
}
