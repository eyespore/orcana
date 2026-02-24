package cc.pineclone.workflow.trigger.jnativehook;

import cc.pineclone.workflow.api.plugin.PluginLifecycle;
import cc.pineclone.workflow.api.trigger.factory.TriggerFactoryRegistrar;
import cc.pineclone.workflow.api.trigger.TriggerPlugin;
import cc.pineclone.workflow.trigger.jnativehook.api.NativeInputEventSource;
import cc.pineclone.workflow.trigger.jnativehook.source.*;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class JNativeHookTriggerPlugin implements TriggerPlugin, PluginLifecycle {

    public static final String PLUGIN_ID = "jnativehook-trigger";
    public static final int PLUGIN_ORDER = 0;

    private final ScheduledExecutorService globalScheduler;

    private static final NativeInputEventSourceHolder<NativeKeyEvent> NATIVE_KEY_EVENT_SOURCE_HOLDER =
            new NativeInputEventSourceHolder<>(new NativeKeyEventSource());
    private static final NativeInputEventSourceHolder<NativeMouseEvent> NATIVE_MOUSE_EVENT_SOURCE_HOLDER =
            new NativeInputEventSourceHolder<>(new NativeMouseEventSource());
    private static final NativeInputEventSourceHolder<NativeMouseWheelEvent> NATIVE_MOUSE_WHEEL_EVENT_SOURCE_HOLDER =
            new NativeInputEventSourceHolder<>(new NativeMouseWheelEventSource());
    private static final NativeInputEventSourceHolder<NativeMouseEvent> NATIVE_MOUSE_MOTION_EVENT_SOURCE_HOLDER =
            new NativeInputEventSourceHolder<>(new NativeMouseMotionEventSource());

    public JNativeHookTriggerPlugin() {
        globalScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    @Override
    public void close() {  /* 清理资源 */
        globalScheduler.shutdown();
        NATIVE_KEY_EVENT_SOURCE_HOLDER.release(this);
        NATIVE_MOUSE_EVENT_SOURCE_HOLDER.release(this);
        NATIVE_MOUSE_WHEEL_EVENT_SOURCE_HOLDER.release(this);
        NATIVE_MOUSE_MOTION_EVENT_SOURCE_HOLDER.release(this);
    }

    @Override
    public void registerTriggerFactories(TriggerFactoryRegistrar registrar) {
        /* 键盘手势触发器 */
        registrar.registerTriggerFactory(new NeuKeyGestureTriggerFactory(
                globalScheduler, NATIVE_KEY_EVENT_SOURCE_HOLDER.acquire(this)));

        /* 鼠标手势触发器 */
        registrar.registerTriggerFactory(new NeuMouseGestureTriggerFactory(
                globalScheduler, NATIVE_MOUSE_EVENT_SOURCE_HOLDER.acquire(this)));

        /* 滚轮手势触发器 */
        registrar.registerTriggerFactory(new NeuMouseWheelTriggerFactory(
                NATIVE_MOUSE_WHEEL_EVENT_SOURCE_HOLDER.acquire(this)));
    }

    private static class NativeInputEventSourceHolder<T extends NativeInputEvent> {

        private final NativeInputEventSource<T> instance;
        private final Map<TriggerPlugin, Integer> refCountMap = new HashMap<>();

        /* 引用计数，支持多个JNativeHookTriggerPlugin注册，每个Plugin最多持有一个NativeInputEventSource实例 */
        private int totalRefCount = 0;

        private NativeInputEventSourceHolder(NativeInputEventSource<T> instance) {
            this.instance = instance;
        }

        public synchronized NativeInputEventSource<T> acquire(TriggerPlugin plugin) {
            refCountMap.merge(plugin, 1, Integer::sum);
            totalRefCount++;
            if (totalRefCount == 1) instance.install();
            return instance;
        }

        public synchronized void release(TriggerPlugin plugin) {
            Integer count = refCountMap.get(plugin);
            if (count == null) return;
            if (count == 1) refCountMap.remove(plugin);
            else refCountMap.put(plugin, count - 1);
            totalRefCount--;
            if (totalRefCount == 0) instance.uninstall();
        }
    }

    public int order() {
        return PLUGIN_ORDER;
    }
}
