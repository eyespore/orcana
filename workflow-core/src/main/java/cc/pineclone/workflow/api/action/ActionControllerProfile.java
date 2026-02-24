package cc.pineclone.workflow.api.action;

import java.util.Set;

public record ActionControllerProfile(
   Set<String> mutexResources,  /* 所属互斥组 */
   int priority,  /* 优先级 */
   PreeemptionPolicy policy  /* 是否允许抢占 */
) {

    public enum PreeemptionPolicy {
        /**
         * 永不让出执行权（除非自己结束或自己取消）
         */
        NEVER,
        /**
         * 允许被 PAUSE 让权（yield permit），之后可被 RESUME 恢复
         * 要求 handler 支持 PAUSE/RESUME
         */
        PAUSE_YIELD,
        /**
         * 允许被 CANCEL 终止让权
         * 要求 handler 支持 CANCEL
         */
        CANCEL,
        /**
         * 调度器可自行选择：优先尝试 PAUSE_YIELD，失败再尝试 CANCEL
         */
        PAUSE_OR_CANCEL
    }
}
