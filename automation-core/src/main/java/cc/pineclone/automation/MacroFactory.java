package cc.pineclone.automation;

import cc.pineclone.automation.action.impl.*;
import cc.pineclone.automation.definition.MacroDefinition;
import cc.pineclone.automation.definition.MacroParams;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.action.impl.betterlbutton.HoldLButtonAction;
import cc.pineclone.automation.action.impl.betterlbutton.RapidlyClickLButtonAction;
import cc.pineclone.automation.action.impl.betterlbutton.RemapLButtonAction;
import cc.pineclone.automation.action.impl.bettermmenu.AutoSnakeAction;
import cc.pineclone.automation.action.impl.bettermmenu.StartEngineAction;
import cc.pineclone.automation.action.impl.betterpmenu.JoinABookmarkedJobAction;
import cc.pineclone.automation.action.impl.betterpmenu.JoinANewSessionAction;
import cc.pineclone.automation.action.impl.swapglitch.SwapGlitchAction;
import cc.pineclone.automation.action.impl.swapglitch.SwapMeleeAction;
import cc.pineclone.automation.action.impl.swapglitch.SwapRangedAction;
import cc.pineclone.automation.trigger.Trigger;
import cc.pineclone.automation.trigger.TriggerFactory;
import cc.pineclone.automation.trigger.TriggerIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MacroFactory {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, MacroCreationUnit<?>> types = new HashMap<>();

    private final Map<String, MacroDefinition> registry = new HashMap<>();

    /* 注册宏定义 */
    private void register(MacroDefinition definition) {
        if (registry.containsKey(definition.getType())) {
            throw new IllegalArgumentException("Macro already registered: " + definition.getType());
        }
        registry.put(definition.getType(), definition);
    }

    // 使用默认参数创建宏实例
    public Macro create(String macroType) {
        return create(macroType, null);
    }

    public Macro create(String macroType, MacroParams params) {
        MacroDefinition definition = registry.get(macroType);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown macro: " + macroType);
        }

        MacroParams effectiveParams = definition.getDefaultParams();  /* 获取默认参数 */
        if (params != null) {  /* 使用用户参数覆盖默认参数 */
            for (String key : params.keySet()) {
                effectiveParams.put(key, params.get(key));
            }
        }

        return definition.createMacroInstance(effectiveParams);
    }

//    public MacroFactory() {
//
//        /* 切枪偷速宏 */
//        register("SWAP_GLITCH", SwapGlitchDTO.class, dto -> {
//            TriggerIdentity defaultIdentity = TriggerIdentity.of(dto.activateMethod(), dto.activateKey());
//            Trigger trigger = TriggerFactory.simple(defaultIdentity);  /* 触发器 */
//
//            Action action = new SwapGlitchAction(
//                    dto.weaponWheelKey(), dto.triggerInterval().parseLong());  /* 基础执行器 */
//
//            if (dto.enableSwapMeleeWeapon()) {  /* 进入偷速切换近战武器 */
//                action = new SwapMeleeAction(action,
//                        dto.meleeWeaponKey(), dto.postSwapMeleeWeaponDelay().parseLong());
//            }
//
//            if (dto.enableSwapRangedWeapon() && !dto.keyMapping().isEmpty()) {  /* 结束偷速切换远程武器 */
//                Map<Key, Key> sourceToTargetMap = new HashMap<>();
//                action = new SwapRangedAction(action,
//                        dto.defaultRangedWeaponKey(),
//                        dto.swapDefaultRangedWeaponOnEmpty(),
//                        dto.keyMapping());
//
//                /* 映射表不为空，基于子动作实现武器切换 */
//                log.debug("Register union trigger for swap glitch macro");
//                trigger = TriggerFactory.union(
//                        TriggerIdentity.of(TriggerMode.CLICK, sourceToTargetMap.keySet()),
//                        defaultIdentity);
//            }
////                /* 启用映射1 */
////                if (swapRangedSetting.enableMapping1)
////                    sourceToTargetMap.put(swapRangedSetting.mapping1SourceKey, swapRangedSetting.mapping1TargetKey);
////                /* 启用映射2 */
////                if (swapRangedSetting.enableMapping2)
////                    sourceToTargetMap.put(swapRangedSetting.mapping2SourceKey, swapRangedSetting.mapping2TargetKey);
////                /* 启用映射3 */
////                if (swapRangedSetting.enableMapping3)
////                    sourceToTargetMap.put(swapRangedSetting.mapping3SourceKey, swapRangedSetting.mapping3TargetKey);
////                /* 启用映射4 */
////                if (swapRangedSetting.enableMapping4)
////                    sourceToTargetMap.put(swapRangedSetting.mapping4SourceKey, swapRangedSetting.mapping4TargetKey);
////                /* 启用映射5 */
////                if (swapRangedSetting.enableMapping5)
////                    sourceToTargetMap.put(swapRangedSetting.mapping5SourceKey, swapRangedSetting.mapping5TargetKey);
////
////                /* 空值映射 */
////                if (swapRangedSetting.enableClearKey)
////                    sourceToTargetMap.put(swapRangedSetting.clearKey, null);
//            return new SimpleMacro(trigger, action);
//        });
//
//        /* 轮盘零食宏 */
//        register("ROULETTE_SNAKE", RouletteSnakeDTO.class, dto -> {
//            Trigger trigger = TriggerFactory.composite(
//                    TriggerIdentity.of(TriggerMode.HOLD, dto.activateKey()),
//                    TriggerIdentity.of(TriggerMode.HOLD, dto.weaponWheelKey()));
//
//            Action action = new RouletteSnakeAction(dto.triggerInterval().parseLong(), dto.snakeKey());
//            return new SimpleMacro(trigger, action);
//        });
//
//        /* AD 摇宏 */
//        register("AD_SWING", ADSwingDTO.class, dto -> {
//            TriggerMode mode = dto.activateMethod();  /* 激活模式 */
//            Key activatekey = dto.activateKey();  /* 激活热键 */
//
//            Trigger trigger;
//            TriggerIdentity defaultIdentity = TriggerIdentity.of(mode, activatekey);
//            if (dto.enableSafetyKey()) {  /* 启用保险键 */
//                trigger = TriggerFactory.composite(defaultIdentity, TriggerIdentity.of(mode, dto.safetyKey()));
//            } else trigger = TriggerFactory.simple(defaultIdentity);  /* 触发器 */
//
//            Action action = new ADSwingAction(dto.triggerInterval().parseLong(),
//                    dto.moveLeftKey(), dto.moveRightKey());
//
//            return new SimpleMacro(trigger, action);
//        });
//
//        /* 近战偷速宏 */
//        register("MELEE_GLITCH", MeleeGlitchDTO.class, dto -> {
//            TriggerMode mode = dto.activateMethod();  /* 激活模式 切换执行 or 按住执行 */
//            Key activatekey = dto.activateKey();  /* 激活热键 */
//
//            Trigger trigger;
//            TriggerIdentity defaultIdentity = TriggerIdentity.of(mode, activatekey);
//            if (dto.enableSafetyKey()) {  /* 启用保险键 */
//                trigger = TriggerFactory.composite(defaultIdentity, TriggerIdentity.of(mode, dto.safetyKey()));
//            } else trigger = TriggerFactory.simple(defaultIdentity);  /* 触发器 */
//
//            long triggerInterval = dto.triggerInterval().parseLong();
//            Action action = new MeleeGlitchAction(triggerInterval, dto.meleeSnakeScrollKey());
//
//            return new SimpleMacro(trigger, action);
//        });
//
//        /* 快速点火宏 */
//        register("START_ENGINE", StartEngineDTO.class, dto -> {
//            Key activateKey = dto.getActivateKey();
//            boolean enableDoubleClickToOpenDoor = dto.getEnableDoubleClickToOpenDoor();
//            long doubleClickInterval = dto.getDoubleClickDetectInterval().parseLong();
//
//            Trigger trigger;
//            if (enableDoubleClickToOpenDoor)
//                trigger = TriggerFactory.simple(TriggerIdentity.ofDoubleClick(doubleClickInterval, activateKey));  // 启用双击触发
//            else trigger = TriggerFactory.simple(TriggerIdentity.ofClick(activateKey));  // 仅启用单击触发
//
//            Action action = new StartEngineAction(
//                    dto.getMenuKey(),
//                    dto.getMouseScrollInterval().parseLong(),
//                    dto.getKeyPressInterval().parseLong(),
//                    dto.getTimeUtilMMenuLoaded().parseLong(),
//                    enableDoubleClickToOpenDoor);
//
//            return new SimpleMacro(trigger, action);
//        });
//
//        /* 自动零食宏 */
//        register("AUTO_SNAKE", AutoSnakeDTO.class, dto -> {
//            Trigger trigger = TriggerFactory.simple(TriggerIdentity.ofHold(dto.getActivateKey()));
//            Action action = new AutoSnakeAction(
//                    dto.getMenuKey(),
//                    dto.getMouseScrollInterval().parseLong(),
//                    dto.getKeyPressInterval().parseLong(),
//                    dto.getTimeUtilMMenuLoaded().parseLong(),
//                    dto.getEnableRefillVest(),
//                    dto.getEnableKeepMMenu());
//
//            return new SimpleMacro(trigger, action);
//        });
//
//        /* 辅助按住鼠标左键宏 */
//        register("HOLD_LEFT_BUTTON", HoldLeftButtonDTO.class, dto ->
//                new SimpleMacro(TriggerFactory.simple(TriggerIdentity.of(dto.activateMethod(), dto.activateKey())),
//                        new HoldLButtonAction()));
//
//        /* 辅助点按鼠标左键宏 */
//        register("RAPIDLY_CLICK_LEFT_BUTTON", RapidlyClickLeftButtonDTO.class, dto ->
//                new SimpleMacro(TriggerFactory.simple(TriggerIdentity.of(dto.activateMethod(), dto.activateKey())),
//                        new RapidlyClickLButtonAction(dto.triggerInterval().parseLong())));
//
//        /* 鼠标左键重映射宏 */
//        register("REMAP_LEFT_BUTTON", RemapLeftButtonDTO.class, dto ->
//                new SimpleMacro(TriggerFactory.simple(TriggerIdentity.of(TriggerMode.HOLD, dto.mapKey())),
//                        new RemapLButtonAction()));
//
//        /* 切枪自动确认宏 */
//        register("SWITCH_AUTO_CONFIRM", SwitchAutoConfirmDTO.class, dto -> {
//            // TODO: 对Map判空处理，避免NPE
//            Action action = new QuickSwapAction(dto.keyMapping(), dto.blockKey(), dto.blockDuration().parseLong());
//
//            Trigger trigger;
//            TriggerIdentity defaultIdentify = TriggerIdentity.of(TriggerMode.CLICK, dto.keyMapping().keySet());
//
//            if (!dto.enableBlockKey()) {  /* 未启用屏蔽键 */
//                trigger = TriggerFactory.simple(defaultIdentify);
//            } else {  /* 启用屏蔽键 */
//                trigger = TriggerFactory.union(
//                        defaultIdentify,
//                        TriggerIdentity.of(TriggerMode.HOLD, dto.blockKey()));
//            }
//
//            return new SimpleMacro(trigger, action);
//        });
//
//        /* 延迟攀宏 */
//        register("DELAY_CLIMB", DelayClimbDTO.class, dto -> {
//            Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(TriggerMode.CLICK, dto.toggleDelayClimbKey()));
//            Action action = new DelayClimbAction(
//                    dto.usePhoneKey(),
//                    dto.hideInCoverKey(),
//                    dto.triggerInterval().parseLong(),
//                    dto.timeUtilCameraExited().parseLong(),
//                    dto.timeUtilCameraLoaded1().parseLong(),
//                    dto.timeUtilCameraLoaded2().parseLong(),
//                    dto.hideInCoverOnExit());
//
//            return new SimpleMacro(trigger, action);
//        });
//
//        /* 快速加入新战局宏 */
//        register("JONI_A_NEW_SESSION", JoinANewSessionDTO.class, dto -> {
//            Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(TriggerMode.CLICK, dto.getActivateKey()));
//            Action action = new JoinANewSessionAction(
//                    dto.getSessionType(),
//                    dto.getMouseScrollInterval().parseLong(),
//                    dto.getKeyPressInterval().parseLong(),
//                    dto.getTimeUtilPMenuLoaded().parseLong());
//            return new SimpleMacro(trigger, action);
//        });
//
//        /* 加入已收藏差事宏 */
//        register("JOIN_A_BOOKMARKED_JOB", JoinABookmarkedJobDTO.class, dto -> {
//            Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(TriggerMode.CLICK, dto.getActivateKey()));
//            Action action = new JoinABookmarkedJobAction(
//                    dto.getMouseScrollInterval().parseLong(),
//                    dto.getKeyPressInterval().parseLong(),
//                    dto.getTimeUtilPMenuLoaded().parseLong(),
//                    dto.getTimeUtilPMenuLoaded().parseLong());
//            return new SimpleMacro(trigger, action);
//        });
//
//        /* 自动开火宏 */
//        register("AUTO_FIRE", AutoFireDTO.class, dto -> {
//            Trigger trigger = TriggerFactory.simple(TriggerIdentity.of(dto.activateMethod(), dto.activateKey()));
//            //            Action action = new AutoFireAction(heavyWeaponKey, specialWeaponKey, triggerInterval, mousePressInterval);
//            //            return createSimpleMacro(trigger, action);
//            return null;
//        });
//    }

    /**
     * 注册新的宏创建策略
     */
    private <T> void register(String type, Class<T> dtoClass, Strategy<T> strategy) {
        types.put(type, new MacroCreationUnit<>(dtoClass, strategy));
    }

    /**
     * 创建宏实例
     */
//    public Macro createMacro(MacroEntryDTO dto) {
//        return types.get(dto.getType()).create(dto.getConfig());
//    }

    /**
     * 返回指定宏类型对应的 Dto 类，用于反序列化
     */
    public Class<?> getDtoClass(String type) {
        return types.get(type).dtoClass();
    }

    /**
     * 宏创建策略
     */
    public interface Strategy<T> extends Function<T, Macro> { }

    public record MacroCreationUnit<T>(Class<T> dtoClass, Strategy<T> strategy) {
        public Macro create(Object dto) {
            return strategy.apply(dtoClass.cast(dto));
        }
    }
}
