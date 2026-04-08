package cc.pineclone.eventflow.plugin.api.template;

import cc.pineclone.eventflow.config.api.definition.TriggerDefinition;
import cc.pineclone.eventflow.core.api.Trigger;

public interface TriggerTemplate extends ComponentTemplate<TriggerDefinition, Trigger> {

    /**
     * 是否支持子 Trigger
     */
    default ChildrenPolicy childPolicy() {
        return ChildrenPolicy.none();
    }

    record ChildrenPolicy(
        boolean enabled,
        int minChildren,
        int maxChildren
    ) {
        public ChildrenPolicy {
            if (!enabled) {  /* 不支持子 Trigger */
                if (minChildren != 0 || maxChildren != 0) {  /* 如果最大最小值不为 0，那么抛出异常 */
                    throw new IllegalArgumentException("When children are disabled, minChildren/maxChildren must be 0");
                }
            } else {  /* 参数有效性检查 */
                if (minChildren < 0) throw new IllegalArgumentException("minChildren must be >= 0");
                if (maxChildren < minChildren) throw new IllegalArgumentException("maxChildren must be >= minChildren");
            }
        }

        public static ChildrenPolicy none() {
            return new ChildrenPolicy(false, 0, 0);
        }

        public static ChildrenPolicy range(int minChildren, int maxChildren) {
            return new ChildrenPolicy(true, minChildren, maxChildren);
        }

        public static ChildrenPolicy atLeast(int minChildren) {
            return new ChildrenPolicy(true, minChildren, Integer.MAX_VALUE);
        }

        public static ChildrenPolicy unlimited() {
            return new ChildrenPolicy(true, 0, Integer.MAX_VALUE);
        }
    }
}
