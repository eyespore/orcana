package cc.pineclone.workflow.api.action;

public record ExecutionProfile(
   String mutexGroup,  /* 所属互斥组 */
   int priority,  /* 优先级 */
   boolean preemptive  /* 是否允许抢占 */
) {
}
