package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.interaction.api.InteractionSpec;
import cc.pineclone.interaction.api.ModifierConstraint;
import cc.pineclone.workflow.trigger.DefaultTrigger;
import cc.pineclone.workflow.api.trigger.TriggerIdentity;
import cc.pineclone.workflow.api.trigger.TriggerLifecycleAware;
import cc.pineclone.workflow.trigger.jnativehook.api.*;
import com.github.kwhat.jnativehook.NativeInputEvent;

public abstract class FilterableTrigger<
                T extends InteractionSpec,  /* 外部统一 Spec 接口 */
                E extends JNativeHookSpec,  /* 内部统一 Spec 接口 */
                M extends ModifierConstraint,  /* 外部统一 Modifier 接口 */
                N extends JNativeHookModifierConstraint,  /* 内部统一 Modifier 接口 */
                P extends NativeInputEvent>  /* 监听事件 */
        extends DefaultTrigger
        implements NativeInputEventListener<P>, TriggerLifecycleAware {

    private final TriggerIdentity identity;
    private final SpecFilter<T, E, M, N, P> filter;  /* 对原始事件进行规则过滤 */
    private final NativeInputEventSource<P> source;  /* 事件源 */

    protected FilterableTrigger(
            TriggerIdentity identity,
            SpecFilter<T, E, M, N, P> filter,
            NativeInputEventSource<P> source) {
        this.filter = filter;
        this.source = source;
        this.identity = identity;
    }

    @Override
    public final void onNativeInputEvent(P event) {
        filter.test(event).ifPresent(spec -> this.handleNativeInputEvent(event, spec));
    }

    public abstract void handleNativeInputEvent(P event, T originalSpec);

    @Override
    public void init() {
        source.registerListener(this);  /* 将自己注册到源 */
    }

    @Override
    public void destroy() {
        source.unregisterListener(this);  /* 将自己从源当中移除 */
    }

    @Override
    public TriggerIdentity getIdentity() {
        return this.identity;
    }
}
