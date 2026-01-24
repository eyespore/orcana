package cc.pineclone.automation.trigger;

import java.util.*;

/* 组合按键触发器 */

/**
 * “与”Trigger触发逻辑，组合多个Trigger，仅在每一个Trigger都触发assert时才会触发TriggerEvent COMPOSITE_ON
 * 仅在每一个Trigger都出发revoke时才会触发TriggerEvent COMPOSITE_OFF
 */
public class CompositeTrigger extends DelegateTrigger {

    private final Set<Trigger> activeSet = new HashSet<>();
    private boolean isActive = false;

//    private final Map<Trigger, Long> lastActiveTime = new HashMap<>();
//    private static final long TOLERANCE_MS = 100;

    public CompositeTrigger(final Set<Trigger> triggers) {
        super(triggers);
    }

    @Override
    public void onTriggerEvent(TriggerEvent event) {
        Trigger source = event.getSource();
        TriggerStatus status = event.getTriggerStatus();
        if (status.isAssert()) {
            /* 触发事件 */
            synchronized (activeSet) {
                activeSet.add(source);
//            lastActiveTime.put(source, System.currentTimeMillis());
                if (activeSet.size() == triggers.size() && !isActive) {
                    isActive = true;
                    fire(TriggerEvent.of(this, TriggerStatus.COMPOSITE_ON, event.getInputSourceEvent()));
                }
            }
        } else if (status.isRevoke()) {
            /* 撤销事件 */
            synchronized (activeSet) {
//            long now = System.currentTimeMillis();
//            long lastActive = lastActiveTime.getOrDefault(source, 0L);
//            if (now - lastActive < TOLERANCE_MS) return;
                activeSet.remove(source);
                if (isActive && activeSet.size() < triggers.size()) {
                    isActive = false;
                    fire(TriggerEvent.of(this, TriggerStatus.COMPOSITE_OFF, event.getInputSourceEvent()));
                }
            }
        }
    }
}
