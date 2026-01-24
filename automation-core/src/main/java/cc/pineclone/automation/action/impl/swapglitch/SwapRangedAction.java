package cc.pineclone.automation.action.impl.swapglitch;

import cc.pineclone.automation.Macro;
import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.ActionDecorator;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.MouseButton;

import java.util.Map;

// TODO: 引入优先级
public class SwapRangedAction extends ActionDecorator {

    private final Key leftButtonKey = new Key(MouseButton.PRIMARY);

    private final VCRobotAdapter robot;  /* 机器人 */
    private final Key defaultRangedWeaponKey;  /* 默认远程武器 */
    private final boolean swapDefaultRangedWeaponOnEmpty;  /* 当目标远程武器为空值时切换到默认 */

    private Key targetRangedWeaponKey;
    private final Map<Key, Key> sourceToTargetMap;  /* 监听源到目标的映射 */

    public SwapRangedAction(Action delegate,
                             Key defaultRangedWeaponKey,
                             boolean swapDefaultRangedWeaponOnEmpty,
                             Map<Key, Key> sourceToTargetMap) {
        super(delegate);
        this.robot = RobotFactory.getRobot(delegate.getActionId());

        this.defaultRangedWeaponKey = defaultRangedWeaponKey;
        this.swapDefaultRangedWeaponOnEmpty = swapDefaultRangedWeaponOnEmpty;
        this.sourceToTargetMap = sourceToTargetMap;  /* 映射表 */
    }

    /* 若用户启用了在没有选中武器时自动切换默认远程武器，那么在触发之前，设置默认远程武器为目标武器 */
    @Override
    public boolean beforeActivate(MacroEvent event) {
        if (!delegate.beforeActivate(event)) return false;
        if (swapDefaultRangedWeaponOnEmpty) {
            targetRangedWeaponKey = defaultRangedWeaponKey;
        }
        return true;
    }

    @Override
    public void activate(MacroEvent event) {
        Key targetKey = sourceToTargetMap.get(event.getTriggerEvent().getInputSourceEvent().getKey());
        if (targetKey != null) {
            log.debug("Target key is {}", targetKey);
            targetRangedWeaponKey = targetKey;
        } else {
            super.activate(event);  /* 在非切换键时，触发父动作执行 */
        }
    }

    /* 在宏(例如切枪偷速、近战偷速)执行结束之后，尝试切换远程武器 */
    @Override
    public void afterDeactivate(MacroEvent event) {
        if (event.getMacroContext().getExecutionStatus().equals(Macro.MacroExecutionStatus.SUSPENDED)) return;
        if (targetRangedWeaponKey != null) {
            try {
                Thread.sleep(20);
                robot.simulate(targetRangedWeaponKey);  /* 切换到枪 */
                Thread.sleep(20);
                robot.simulate(leftButtonKey);  /* 点左键选择 */
                targetRangedWeaponKey = null;
            } catch (InterruptedException ignored) {
            }
        }
        delegate.afterDeactivate(event);
    }
}
