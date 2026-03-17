package cc.pineclone.eventflow.runtime.api;

import java.util.Objects;

@Deprecated
public record MutexProfile(
   String group,  /* 所属互斥组 */
   int priority,  /* 优先级 */
   PreemptionPolicy policy  /* 让权策略 */
) {

    public MutexProfile {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(policy, "policy");
    }

}
