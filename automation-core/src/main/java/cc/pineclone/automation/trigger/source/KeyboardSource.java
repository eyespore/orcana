package cc.pineclone.automation.trigger.source;

import cc.pineclone.automation.input.Key;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* 键盘输入源 */
public class KeyboardSource extends InputSource implements NativeKeyListener {

    private final Map<Integer, Key> keys = new HashMap<>();

    public KeyboardSource(Key... keys) {
        Arrays.stream(keys).forEach(k -> this.keys.put(k.key.code, k));
    }

    public KeyboardSource(Set<Key> keys) {
        keys.forEach(k -> this.keys.put(k.key.code, k));
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        int keyCode = nativeEvent.getKeyCode();
        if (keys.containsKey(keyCode)) {
            listener.onInputSourceEvent(InputSourceEvent.of(InputSourceEvent.Operation.KEY_PRESSED, keys.get(keyCode)));
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        int keyCode = nativeEvent.getKeyCode();
        if (keys.containsKey(keyCode)) {
            listener.onInputSourceEvent(InputSourceEvent.of(InputSourceEvent.Operation.KEY_RELEASED, keys.get(keyCode)));
        }
    }
}
