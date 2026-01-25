package cc.pineclone.automation.action.impl.actionext;

import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.input.Key;
import lombok.Getter;

import java.util.Map;

/**
 * 记录器动作，启动后会持续记录用户的动作，通常配合CLICK Mode使用
 */
public class RecordAction extends Action {
    private final static String ACTION_ID = "action-ext";
    @Getter
    private volatile Key targetKey;
    private final Map<Key, Key> sourceToTargetMap;

    public RecordAction(Map<Key, Key> sourceToTargetMap) {
        super(ACTION_ID);
        this.sourceToTargetMap = sourceToTargetMap;
    }

    @Override
    public void activate(MacroEvent event) {
        /* 触发记录，覆写原始targetRangedWeaponKey */
        targetKey = sourceToTargetMap.get(event.getTriggerEvent().getInputSourceEvent().getKey());
    }
}
