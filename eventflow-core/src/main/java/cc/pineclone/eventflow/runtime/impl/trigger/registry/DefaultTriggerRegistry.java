package cc.pineclone.eventflow.runtime.impl.trigger.registry;

import cc.pineclone.eventflow.plugin.api.ComponentTemplateProvider;
import cc.pineclone.eventflow.core.api.trigger.CompositeTrigger;
import cc.pineclone.eventflow.core.api.trigger.Trigger;
import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.trigger.TriggerLifecycle;
import cc.pineclone.eventflow.core.api.event.EventSink;
import cc.pineclone.eventflow.config.api.CompositeTriggerDefinition;
import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;
import cc.pineclone.eventflow.plugin.api.template.TriggerTemplate;
import cc.pineclone.eventflow.runtime.api.TriggerRegistry;
import cc.pineclone.eventflow.runtime.api.TriggerRegistryRuntimeAccess;
import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;

public class DefaultTriggerRegistry
        implements TriggerRegistry, TriggerRegistryRuntimeAccess {

    private final ComponentTemplateProvider<TriggerTemplate> triggerTemplateProvider;
    private final Map<ComponentId, TriggerTree> rootNodes = new HashMap<>();

    public DefaultTriggerRegistry(ComponentTemplateProvider<TriggerTemplate> triggerTemplateProvider) {
        this.triggerTemplateProvider = triggerTemplateProvider;
    }

    static final class TriggerTree {
        final Trigger root;
        final List<Trigger> postTriggerOrder;  /* 从底层到根节点的 Trigger 列表 */
        int refCount = 0;                  // 0表示未初始化

        TriggerTree(Trigger root, List<Trigger> postTriggerOrder) {
            this.root = root;
            this.postTriggerOrder = postTriggerOrder;
        }
    }

    static final class TriggerDefinitionFlattenContext {
        private final Set<ComponentId> identities = new HashSet<>();  /* 冲突检测 */
        @Getter private final List<TriggerDefinition> postTriggerDefinitionOrder = new ArrayList<>();

        public void handle(TriggerDefinition currentDefinition) {
            /* 对当前添加的 TriggerDefinition 在当前树中做冲突性检测 */
            ComponentId currentId = currentDefinition.identity();
            if (!identities.add(currentId)) {
                throw new IllegalStateException("Duplicate TriggerIdentity in same tree: " + currentId);
            }
            postTriggerDefinitionOrder.add(currentDefinition);
        }
    }

    private void flatten(TriggerDefinition def, TriggerDefinitionFlattenContext ctx) {
        ctx.handle(def);
        if (def instanceof CompositeTriggerDefinition c) {
            for (TriggerDefinition child : c.children()) {
                flatten(child, ctx);
            }
        }
    }

    private TriggerTree buildTriggerTree(TriggerDefinition rootDef, List<TriggerDefinition> postTriggerDefinitionOrder) {
        /* 建立从 Definition 到 Trigge 的临时映射 */
        Map<TriggerDefinition, Trigger> instances = new HashMap<>();

        /* 遍历树中的 Trigger，按照从低到高创建 Trigger */
        for (TriggerDefinition def : postTriggerDefinitionOrder) {
            Optional<TriggerTemplate> templateOpt = triggerTemplateProvider.findTemplate(def.templateType());
            if (templateOpt.isEmpty()) throw new IllegalStateException("No TriggerFactory registered for " + def.getClass());

            TriggerTemplate template = templateOpt.get();

            // TODO: 构建 Session

            Trigger trigger = template.createInstance(def);
            instances.put(def, trigger);
        }

        /* 基于 Definition 中定义的聚合结构，将子 Trigger 组装到父 Trigger */

        // TODO: 优化判断逻辑，改用 TriggerAggregator + TriggerTemplate.childPolicy 进行硬性判断
        for (TriggerDefinition def : postTriggerDefinitionOrder) {
            if (def instanceof CompositeTriggerDefinition c) {
                CompositeTrigger parent = (CompositeTrigger) instances.get(def);

                for (TriggerDefinition child : c.children()) {
                    parent.addChild(instances.get(child));
                }
            }
        }

        Trigger root = instances.get(rootDef);  /* 父节点 */
        List<Trigger> postTriggerOrder = postTriggerDefinitionOrder.stream()
                .map(instances::get)
                .toList();  /* 基于展平的 TriggerDefinition 重构一份 TriggerList 作为 Tree 的初始化、销毁顺序依据 */

        return new TriggerTree(root, postTriggerOrder);
    }

    @Override
    public synchronized ComponentId register(TriggerDefinition rootDef) {
        if (rootDef.identity() == null) {
            throw new IllegalArgumentException("Root TriggerDefinition must define domain and name");
        }

        ComponentId rootId = rootDef.identity();
        if (rootNodes.containsKey(rootId)) {
            throw new IllegalStateException("Trigger already registered: " + rootId);
        }

        TriggerDefinitionFlattenContext ctx = new TriggerDefinitionFlattenContext();
        flatten(rootDef, ctx);

        TriggerTree tree = buildTriggerTree(rootDef, ctx.getPostTriggerDefinitionOrder());
        rootNodes.put(rootId, tree);
        return rootId;
    }

    @Override
    public void unregister(ComponentId rootId) {
        TriggerTree tree = rootNodes.get(rootId);
        if (tree == null) return;

        if (tree.refCount > 0) {  /* 当前根 Trigger 仍然持有引用，拒绝注销 */
            throw new IllegalStateException(
                    "Cannot unregister TriggerTree " + rootId +
                            " because it is still retained (refCount=" + tree.refCount + ")"
            );
        }

        // 不可恢复销毁：对整棵树调用 destroy（leaf -> root）
        List<Trigger> postOrder = tree.postTriggerOrder;
        for (int i = postOrder.size() - 1; i >= 0; i--) {
            Trigger t = postOrder.get(i);
            if (t instanceof TriggerLifecycle lifecycle) lifecycle.close();
        }

        rootNodes.remove(rootId);
    }

    @Override
    public synchronized void retain(ComponentId rootId) {
        TriggerTree tree = rootNodes.get(rootId);
        if (tree == null) {
            throw new IllegalArgumentException("Unknown root identity " + rootId);
        }

        if (++tree.refCount == 1) {  /* 首次调用时，触发 Trigger 的初始化 */
            for (Trigger t : tree.postTriggerOrder) {
                if (t instanceof TriggerLifecycle lifecycle) lifecycle.init();
            }
        }
    }

    @Override
    public synchronized void release(ComponentId rootId) {
        TriggerTree tree = rootNodes.get(rootId);
        if (tree == null) return;

        tree.refCount--;
        if (tree.refCount > 0) return;

        // refCount == 0, 停止整棵树（可恢复）
        List<Trigger> postOrder = tree.postTriggerOrder;
        for (int i = postOrder.size() - 1; i >= 0; i--) {
            Trigger t = postOrder.get(i);
            if (t instanceof TriggerLifecycle lifecycle) lifecycle.stop();
        }
    }

    @Deprecated
    @Override
    public List<Trigger> getRootTriggers() {
        return rootNodes.values().stream().map(t -> t.root).toList();
    }

    @Override
    public synchronized List<ComponentId> listRootIdentities() {
        return rootNodes.keySet().stream().toList();
    }

    @Override
    public synchronized boolean contains(ComponentId rootId) {
        Objects.requireNonNull(rootId, "rootId");
        return rootNodes.containsKey(rootId);
    }

    @Override
    public synchronized void withRootTrigger(ComponentId rootId, Consumer<RootTriggerHandle> consumer) {
        Objects.requireNonNull(rootId, "rootId");
        Objects.requireNonNull(consumer, "consumer");

        TriggerTree tree = rootNodes.get(rootId);

        /* 树不存在 */
        if (tree == null) {
            throw new IllegalArgumentException("Unknown root identity " + rootId);
        }

        /* 引用计数为0时可以表示未初始化、或已经销毁，不论哪一种状态都不允许继续操作该Trigger */
        if (tree.refCount <= 0) {
            throw new IllegalStateException("Root trigger tree not retained: " + rootId);
        }

        consumer.accept(new RootTriggerHandle() {
            @Override
            public void attach(EventSink sink) {
                tree.root.bind(sink);
            }

            @Override
            public void detach() {
                tree.root.unbind();
            }

            @Override
            public ComponentId identity() {
                return tree.root.id();
            }
        });
    }
}
