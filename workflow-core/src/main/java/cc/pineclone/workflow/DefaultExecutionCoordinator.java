package cc.pineclone.workflow;

import cc.pineclone.workflow.api.action.ExecutionCoordinator;
import cc.pineclone.workflow.api.action.ActionFacade;
import cc.pineclone.workflow.api.action.ExecutionProfile;

import java.util.*;

public class DefaultExecutionCoordinator implements ExecutionCoordinator {

    /** mutex group -> 当前运行 Handler */
    private final Map<String, ActionFacade> running = new HashMap<>();

    /** mutex group -> 等待队列（优先级降序） */
    private final Map<String, PriorityQueue<ActionFacade>> deferred = new HashMap<>();

    @Override
    public synchronized void decide(ActionFacade handler) {
        ExecutionProfile profile = handler.profile();
        String mutexGroup = profile.mutexGroup();
        int priority = profile.priority();

        ActionFacade current = running.get(mutexGroup);

        if (current == null) {
            running.put(mutexGroup, handler);
            handler.cont();  /* 当前互斥组没有任何其他执行器存在，即没有冲突，可以直接运行 */

        } else {
            /* 互斥组存在其他执行器，需要计算冲突 */
            int currentPriority = current.profile().priority();
            if (priority > currentPriority && profile.preemptive()) {  // 新 Handler 抢占旧 Handler
                running.put(mutexGroup, handler);
                handler.cont();
            } else {
                /* 旧执行器优先级更高，新 Handler 需要等待 */
                deferred.computeIfAbsent(
                        mutexGroup, k -> new PriorityQueue<>(Comparator.comparingInt(h -> -h.profile().priority())))
                        .offer(handler);
                handler.halt();
            }
        }
    }

    @Override
    public synchronized void onCompleted(ActionFacade handler) {  /* 执行器句柄通过回调触发 */
        String mutexGroup = handler.profile().mutexGroup();
        if (running.get(mutexGroup) == handler) {  // 移除运行的 Handler
            running.remove(mutexGroup);

            Queue<ActionFacade> queue = deferred.get(mutexGroup);  // 检查 deferred 队列
            if (queue != null && !queue.isEmpty()) {
                ActionFacade next = queue.poll();  /* 获取下一个挂起的执行器并恢复 */
                running.put(mutexGroup, next);
                next.cont();  /* 恢复队列中的执行 */
            }
        }
    }
}
