package cc.pineclone.workflow.trigger.jnativehook.api;

public record JNativeHookMouseSpec(
        int button
) implements JNativeHookSpec {

    public boolean match(int button) {
        return this.button == button;
    }
}
