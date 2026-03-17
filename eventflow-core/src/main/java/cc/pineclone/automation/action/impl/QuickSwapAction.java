package cc.pineclone.automation.action.impl;

import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.MouseButton;
import cc.pineclone.automation.trigger.TriggerEvent;
import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.impl.actionext.BlockAction;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/* 快速切枪 */
// TODO: 引入优先级
public class QuickSwapAction extends Action {

    private final VCRobotAdapter robot;
    private final Map<Key, Key> sourceToTargetMap = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected static final String ACTION_ID = "quick-swap";
    private final Key leftButtonKey = new Key(MouseButton.PRIMARY);

    private final Key blockKey;
    private final BlockAction blockAction;

    public QuickSwapAction(Map<Key, Key> sourceToTargetMap, Key blockKey ,long blockDuration) {
        super(ACTION_ID);
        this.sourceToTargetMap.putAll(sourceToTargetMap);
        this.robot = RobotFactory.getRobot(ACTION_ID);

        this.blockAction = new BlockAction(blockDuration);
        this.blockKey = blockKey;
    }

    @Override
    public void activate(MacroEvent event) {
        /* 触发屏蔽 */
        if (blockKey != null && blockKey.equals(event.getTriggerEvent().getInputSourceEvent().getKey())) {
            blockAction.activate(event);
            return;
        }

        if (blockAction.isBlocked()) return;

        try {
            Thread.sleep(20);
            Key key = event.getTriggerEvent().getInputSourceEvent().getKey();
            robot.simulate(sourceToTargetMap.get(key));  /* 切换到枪 */
            Thread.sleep(20);
            robot.simulate(leftButtonKey);  /* 点左键选择 */
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void deactivate(MacroEvent event) {
        TriggerEvent triggerEvent = event.getTriggerEvent();
        if (triggerEvent != null && blockKey != null) {
            if (blockKey.equals(triggerEvent.getInputSourceEvent().getKey())) {
                blockAction.deactivate(event);
            }
        }
    }
}
