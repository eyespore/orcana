package cc.pineclone.eventflow.plugin.trigger.jnativehook.api;

public record JNativeHookMouseWheelSpec(
        int wheelRotation
) implements JNativeHookSpec {

    public boolean match(int wheelRotation) {
        return this.wheelRotation * wheelRotation > 0;
    }

}
