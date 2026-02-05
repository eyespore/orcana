package cc.pineclone.workflow.trigger.jnativehook.api;

public record JNativeHookKeySpec(
        int keyCode,
        int keyLocation
) implements JNativeHookSpec {

    public boolean match(int keyCode, int keyLocation) {
        return this.keyCode == keyCode && this.keyLocation == keyLocation;
    }
}
