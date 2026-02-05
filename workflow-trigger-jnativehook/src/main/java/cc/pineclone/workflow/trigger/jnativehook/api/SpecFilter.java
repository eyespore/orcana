package cc.pineclone.workflow.trigger.jnativehook.api;

import cc.pineclone.interaction.api.InteractionSpec;
import cc.pineclone.interaction.api.InteractionSpecAdapter;
import cc.pineclone.interaction.api.ModifierConstraint;
import cc.pineclone.interaction.api.ModifierConstraintAdapter;
import cc.pineclone.interaction.exception.InteractionSpecAdapteeException;
import cc.pineclone.interaction.exception.ModifierConstraintAdapteeException;
import com.github.kwhat.jnativehook.NativeInputEvent;

import java.util.Optional;

public abstract class SpecFilter<
        T extends InteractionSpec,
        E extends JNativeHookSpec,
        M extends ModifierConstraint,
        N extends JNativeHookModifierConstraint,
        P extends NativeInputEvent> {

    protected final T originalSpec;
    protected final E compiledSpec;

    protected final M originalModifier;
    protected final N compiledModifier;

    public SpecFilter(
            T originalSpec, InteractionSpecAdapter<T, E> specAdapter,
            M originalModifier, ModifierConstraintAdapter<M, N> modifierAdapter) {
        this.originalSpec = originalSpec;
        try {
            this.compiledSpec = specAdapter.adaptee(originalSpec);
        } catch (InteractionSpecAdapteeException e) {
            throw new RuntimeException(e);
        }

        this.originalModifier = originalModifier;
        try {
            this.compiledModifier = modifierAdapter.adaptee(originalModifier);
        } catch (ModifierConstraintAdapteeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 测试事件是否匹配某个内部 Spec
     * @param event 原始事件
     * @return 如果匹配，返回对应的原始 Spec T，否则返回 null
     */
    public abstract Optional<T> test(P event);
}
