package cc.pineclone.eventflow.plugin.trigger.jnativehook;

import cc.pineclone.eventflow.plugin.trigger.jnativehook.api.*;
import cc.pineclone.eventflow.interaction.api.InteractionSpec;
import cc.pineclone.eventflow.interaction.api.ModifierConstraint;
import cc.pineclone.eventflow.runtime.impl.trigger.DefaultTrigger;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.trigger.TriggerLifecycle;
import cc.pineclone.eventflow.trigger.jnativehook.api.*;
import com.github.kwhat.jnativehook.NativeInputEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FilterableTrigger<
                T extends InteractionSpec,  /* 外部统一 Spec 接口 */
                E extends JNativeHookSpec,  /* 内部统一 Spec 接口 */
                M extends ModifierConstraint,  /* 外部统一 Modifier 接口 */
                N extends JNativeHookModifierConstraint,  /* 内部统一 Modifier 接口 */
                P extends NativeInputEvent>  /* 监听事件 */
        extends DefaultTrigger
        implements NativeInputEventListener<P>, TriggerLifecycle {

    private final ComponentId identity;
    private final SpecFilter<T, E, M, N, P> filter;  /* 对原始事件进行规则过滤 */
    private final NativeInputEventSource<P> source;  /* 事件源 */
    private final Logger log = LoggerFactory.getLogger(getClass());

    private volatile boolean closed = false;
    private volatile boolean started = false; // 是否已 registerListener

    private void checkNotDestroyed() {
        if (closed) {
            throw new IllegalStateException("Trigger is destroyed: " + id());
        }
    }

    protected FilterableTrigger(
            ComponentId identity,
            SpecFilter<T, E, M, N, P> filter,
            NativeInputEventSource<P> source) {
        this.filter = filter;
        this.source = source;
        this.identity = identity;
    }

    @Override
    public final void onNativeInputEvent(P event) {
        if (closed || !started) return; // destroy/stop 后硬丢弃输入事件
        filter.test(event).ifPresent(spec -> this.handleNativeInputEvent(event, spec));
    }

    public abstract void handleNativeInputEvent(P event, T originalSpec);

    @Override
    public synchronized void init() {
        checkNotDestroyed();
        if (started) return; // 幂等：重复 init 不重复注册
        log.debug("FilterableTrigger initialized: {}", id());
        source.registerListener(this);  /* 将自己注册到源 */
        started = true;
    }

    @Override
    public synchronized void stop() {
        checkNotDestroyed();
        if (!started) return; // 幂等：未启动无需 stop

        log.debug("FilterableTrigger stop: {}", id());
        try {
            source.unregisterListener(this);
        } finally {
            started = false;
        }
    }

    @Override
    public synchronized void close() {
        if (closed) return; // 幂等
        closed = true;

        log.debug("FilterableTrigger destroy: {}", id());

        if (started) {
            try {
                source.unregisterListener(this);
            } finally {
                started = false;
            }
        }
    }

    @Override
    public ComponentId id() {
        return this.identity;
    }
}
