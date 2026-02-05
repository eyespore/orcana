package cc.pineclone.workflow.trigger;

import cc.pineclone.workflow.api.trigger.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

public class DefaultTriggerRegistry implements TriggerRegistry {
    private final TriggerFactoryProvider triggerFactoryProvider;
    private final Map<TriggerIdentity, TriggerTree> rootNodes = new HashMap<>();

    public DefaultTriggerRegistry(TriggerFactoryProvider triggerFactoryProvider) {
        this.triggerFactoryProvider = triggerFactoryProvider;
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
        private final Set<TriggerIdentity> identities = new HashSet<>();  /* 冲突检测 */
        @Getter private final List<TriggerDefinition> postTriggerDefinitionOrder = new ArrayList<>();

        public void handle(TriggerDefinition currentDefinition) {
            /* 对当前添加的 TriggerDefinition 在当前树中做冲突性检测 */
            TriggerIdentity currentId = currentDefinition.getIdentity();
            if (!identities.add(currentId)) {
                throw new IllegalStateException("Duplicate TriggerIdentity in same tree: " + currentId);
            }
            postTriggerDefinitionOrder.add(currentDefinition);
        }
    }

    private void flatten(TriggerDefinition def, TriggerDefinitionFlattenContext ctx) {
        ctx.handle(def);
        if (def instanceof CompositeTriggerDefinition c) {
            for (TriggerDefinition child : c.getChildDefinitions()) {
                flatten(child, ctx);
            }
        }
    }

    private TriggerTree buildTriggerTree(TriggerDefinition rootDef, List<TriggerDefinition> postTriggerDefinitionOrder) {
        /* 建立从 Definition 到 Trigge 的临时映射 */
        Map<TriggerDefinition, Trigger> instances = new HashMap<>();

        /* 遍历树中的 Trigger，按照从低到高创建 Trigger */
        for (TriggerDefinition def : postTriggerDefinitionOrder) {
            TriggerFactory<?> factory = triggerFactoryProvider.getTriggerFactory(def.getClass());
            if (factory == null) throw new IllegalStateException("No TriggerFactory registered for " + def.getClass());

            @SuppressWarnings("unchecked")
            Trigger trigger = ((TriggerFactory<TriggerDefinition>) factory).createTrigger(def);
            instances.put(def, trigger);
        }

        /* 基于 Definition 中定义的聚合结构，将子 Trigger 组装到父 Trigger */
        for (TriggerDefinition def : postTriggerDefinitionOrder) {
            if (def instanceof CompositeTriggerDefinition c) {
                CompositeTrigger parent = (CompositeTrigger) instances.get(def);

                for (TriggerDefinition child : c.getChildDefinitions()) {
                    parent.addChildren(instances.get(child));
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
    public synchronized TriggerIdentity register(TriggerDefinition rootDef) {
        if (rootDef.getIdentity() == null) {
            throw new IllegalArgumentException("Root TriggerDefinition must define domain and name");
        }

        TriggerIdentity rootId = rootDef.getIdentity();
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
    public void unregister(TriggerIdentity rootId) {
        TriggerTree tree = rootNodes.get(rootId);
        if (tree == null) return;

        if (tree.refCount > 0) {  /* 当前根 Trigger 仍然持有引用，拒绝注销 */
            throw new IllegalStateException(
                    "Cannot unregister TriggerTree " + rootId +
                            " because it is still retained (refCount=" + tree.refCount + ")"
            );
        }

        rootNodes.remove(rootId);
    }

    @Override
    public synchronized void retain(TriggerIdentity rootId) {
        TriggerTree tree = rootNodes.get(rootId);
        if (tree == null) {
            throw new IllegalArgumentException("Unknown root identity " + rootId);
        }

        if (++tree.refCount == 1) {  /* 首次调用时，触发 Trigger 的初始化 */
            for (Trigger t : tree.postTriggerOrder) {
                if (t instanceof TriggerLifecycleAware aware) aware.init();
            }
        }
    }

    @Override
    public synchronized void release(TriggerIdentity rootId) {
        TriggerTree tree = rootNodes.get(rootId);
        if (tree == null) return;

        tree.refCount--;
        if (tree.refCount > 0) return;

        // refCount == 0, 销毁整棵树
        List<Trigger> postOrder = tree.postTriggerOrder;
        for (int i = postOrder.size() - 1; i >= 0; i--) {
            Trigger t = postOrder.get(i);
            if (t instanceof TriggerLifecycleAware aware) aware.destroy();
        }
    }

    @Override
    public List<Trigger> getRootTriggers() {
        return rootNodes.values().stream().map(t -> t.root).toList();
    }
}
