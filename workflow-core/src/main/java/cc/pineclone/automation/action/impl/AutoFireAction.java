package cc.pineclone.automation.action.impl;

import cc.pineclone.automation.MacroEvent;
import cc.pineclone.automation.action.ScheduledAction;
import cc.pineclone.automation.action.robot.RobotFactory;
import cc.pineclone.automation.action.robot.VCRobotAdapter;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.MouseButton;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/* 连发 RPG */
public class AutoFireAction extends ScheduledAction {

    private final VCRobotAdapter robot;
    private final long mousePressInterval;

    private final Key specialWeaponKey;  /* 特殊武器键 */
    private final Key neutralWeaponKey;  /* 中立武器键 */

    private final Key primaryMouseButton = new Key(MouseButton.PRIMARY);

    protected static final String ACTION_ID = "auto-fire";

    private final Map<Key, AutoFireTarget> sourceToTargetMap = new HashMap<>();  /*  */

    /* 武器映射目标 */
    @AllArgsConstructor
    @Getter
    public static class AutoFireTarget {
        public int priority;  /* 武器位于轮盘当中的优先级 */
        public Key targetKey;  /* 目标武器按键 */
    }

    /**
     * 武器连发宏，通过中立武器键 + 轮盘武器优先级，实现指定优先级武器连发
     * @param triggerInterval 连发间隔
     * @param mousePressInterval 鼠标按住间隔
     */
    public AutoFireAction(Map<Key, AutoFireTarget> sourceToTargetMap,
                          Key specialWeaponKey,
                          Key neutralWeaponKey,
                          long triggerInterval,
                          long mousePressInterval) {
        super(ACTION_ID, triggerInterval);
        this.sourceToTargetMap.putAll(sourceToTargetMap);
        this.specialWeaponKey = specialWeaponKey;
        this.neutralWeaponKey = neutralWeaponKey;
        this.mousePressInterval = mousePressInterval;
        robot = RobotFactory.getRobot(ACTION_ID);
    }

    @Override
    public boolean beforeActivate(MacroEvent event) {
        /* 首先获取连发映射目标 */
        Key sourceKey = event.getTriggerEvent().getInputSourceEvent().getKey();
        AutoFireTarget target = sourceToTargetMap.get(sourceKey);


        try {

            /* 切换到中立武器，确保优先级逻辑生效 */
            robot.simulate(neutralWeaponKey);  /* 经过测试此处不需要鼠标左键确认，只需要选择到武器即可 */

            /* TODO: 补充自动开火完整逻辑 */

            /* 如果存在优先级则需要执行优先级切换 */
            for (int i = 0; i < target.getPriority(); i++) {
                robot.simulate(target.getTargetKey());
            }

        } catch (InterruptedException ignored) {
        }

        return true;
    }

    /* 连发 RPG 循环 */
    @Override
    public void schedule(MacroEvent event) {
        /* 首先获取连发映射目标 */
        Key sourceKey = event.getTriggerEvent().getInputSourceEvent().getKey();
        AutoFireTarget target = sourceToTargetMap.get(sourceKey);

        try {
            robot.simulate(target.getTargetKey());

            try {
                robot.mousePress(primaryMouseButton);
                Thread.sleep(mousePressInterval);
            } finally {
                robot.mouseRelease(primaryMouseButton);
            }

            robot.simulate(this.specialWeaponKey);
        } catch (InterruptedException ignored) {
        }
    }
}
