package cc.pineclone.automation.common;

import cc.pineclone.automation.common.SessionType;
import cc.pineclone.automation.common.TriggerMode;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.KeyCode;
import cc.pineclone.automation.input.MouseButton;
import cc.pineclone.automation.input.MouseWheelScroll;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Deprecated
public class DeprecatedMacroConfig {

    private UUID uuid;  /* 配置 uuid */
    private String name;  /* 配置名称 */
    private String version;  /* 配置版本号，对应宏内核版本 */
    private Instant createdAt;  /* 配置创建时间 */
    private Instant lastModifiedAt;  /* 上一次修改时间 */

//    public InGame inGame = new InGame();
    public SwapGlitch swapGlitch = new SwapGlitch();
    public RouletteSnake rouletteSnake = new RouletteSnake();  /* 轮盘零食 */
    public ADSwing adSwing = new ADSwing();
    public MeleeGlitch meleeGlitch = new MeleeGlitch();  /* 近战偷速 */
    public BetterMMenu betterMMenu = new BetterMMenu();  /* 更好的 M 菜单 */
    public BetterLButton betterLButton = new BetterLButton();  /* 更好的鼠标左键 */
    public QuickSwap quickSwap = new QuickSwap();  /* 快速切枪 */
    public DelayClimb delayClimb = new DelayClimb();  /* 延迟攀 */
    public BetterPMenu betterPMenu = new BetterPMenu();  /* 更好的 P 菜单 */
    public AutoFire autoFire = new AutoFire();  /* 连发 RPG */
    public String gameHome = "";  /* 游戏路径 */

    /* 游戏内配置项 */
//    public static class InGame {
//    }

    /* 切枪偷速 */
    @Data
    public static class SwapGlitch {
        public BaseSetting baseSetting = new BaseSetting();
        public SwapMeleeSetting swapMeleeSetting = new SwapMeleeSetting();
        public SwapRangedSetting swapRangedSetting = new SwapRangedSetting();

        public static class BaseSetting {
            public boolean enable = false;  /* 是否启用 */
            public TriggerMode activateMethod = TriggerMode.HOLD;  /* 激活方式 0: 按住激活; 1: 切换激活 */
            public double triggerInterval = 50.0;  /* 切枪间隔 */
            public Key activatekey = new Key(MouseButton.BACK);  /* 激活热键 */
            public Key targetWeaponWheelKey = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN));  /* 武器轮盘 */
        }

        public static class SwapMeleeSetting {
            public boolean enableSwapMelee = false;  /* 进入偷速切换近战 */
            public double postSwapMeleeDelay = 120.0;  /* 切换近战武器后等待时间 */
            public Key meleeWeaponKey = new Key(KeyCode.Q);  /* 近战武器 */
        }

        public static class SwapRangedSetting {
            public boolean enableSwapRanged = false;  /* 解除偷速切换远程 */
            public boolean swapDefaultRangedWeaponOnEmpty = false;  /* 切换默认远程武器如果没有指定武器 */
            public Key defaultRangedWeaponKey = new Key(KeyCode.KEY_1);  /* 远程武器 */

            public boolean enableMapping1 = false;  /* 启用映射1 */
            public Key mapping1SourceKey = new Key(KeyCode.KEY_1);  /* 映射1源键 */
            public Key mapping1TargetKey = new Key(KeyCode.KEY_6);  /* 映射1目标键 */

            public boolean enableMapping2 = false;  /* 启用映射2 */
            public Key mapping2SourceKey = new Key(KeyCode.KEY_2);
            public Key mapping2TargetKey = new Key(KeyCode.KEY_7);

            public boolean enableMapping3 = false;  /* 启用映射3 */
            public Key mapping3SourceKey = new Key(KeyCode.KEY_3);
            public Key mapping3TargetKey = new Key(KeyCode.KEY_8);

            public boolean enableMapping4 = false;  /* 启用映射4 */
            public Key mapping4SourceKey = new Key(KeyCode.KEY_4);
            public Key mapping4TargetKey = new Key(KeyCode.KEY_9);

            public boolean enableMapping5 = false;  /* 启用映射5 */
            public Key mapping5SourceKey = new Key(KeyCode.Q);
            public Key mapping5TargetKey = new Key(KeyCode.KEY_5);

            public boolean enableClearKey = false;  /* 启用屏蔽键 */
            public Key clearKey = new Key(KeyCode.TAB);  /* 屏蔽键 */
        }
    }

    /* 回血增强 */
    @Data
    public static class RouletteSnake {
        public BaseSetting baseSetting = new BaseSetting();
        public static class BaseSetting {
            public boolean enable = false;
            public double triggerInterval = 40.0;  /* 点按间隔 */
            public Key activatekey = new Key(KeyCode.TAB);  /* 激活热键 */
            public Key snakeKey = new Key(KeyCode.MINUS);  /* 零食键 */
            public Key weaponWheel = new Key(KeyCode.TAB);  /* 武器轮盘 */
        }
    }


    @Data
    public static class ADSwing {
        public BaseSetting baseSetting = new BaseSetting();
        public static class BaseSetting {
            public boolean enable = false;
            public TriggerMode activateMethod = TriggerMode.HOLD;  /* 激活方式 0: 按住激活; 1: 切换激活 */
            public double triggerInterval = 20.0;  /* AD点按间隔 */
            public Key activatekey = new Key(KeyCode.E);
            public Key moveLeftKey = new Key(KeyCode.A);
            public Key moveRightKey = new Key(KeyCode.D);
            public Key safetyKey = new Key(MouseButton.BACK);
            public boolean enableSafetyKey = true;
        }
    }

    @Data
    public static class MeleeGlitch {
        public BaseSetting baseSetting = new BaseSetting();
        public static class BaseSetting {
            public boolean enable = false;
            public TriggerMode activateMethod = TriggerMode.HOLD;  /* 激活方式 0: 按住激活; 1: 切换激活 */
            public double triggerInterval = 20.0;
            public Key meleeSnakeScrollKey = new Key(new MouseWheelScroll(MouseWheelScroll.Direction.UP));
            public Key activatekey = new Key(KeyCode.E);
            public Key safetyKey = new Key(MouseButton.BACK);
            public boolean enableSafetyKey = true;
        }
    }

    @Data
    public static class BetterMMenu {
        public BaseSetting baseSetting = new BaseSetting();
        public StartEngine startEngine = new StartEngine();
        public AutoSnake autoSnake = new AutoSnake();

        public static class BaseSetting {
            public boolean enable = false;  /* 是否启用 */
            public Key menuKey = new Key(KeyCode.M);
            public double mouseScrollInterval = 40.0;
            public double keyPressInterval = 40.0;
            public double timeUtilMMenuLoaded = 100.0;
        }

        /* 快速点火 */
        public static class StartEngine {
            public boolean enable = false;
            public Key activateKey = new Key(MouseButton.FORWARD);
            public boolean enableDoubleClickToOpenDoor = false;  /* 是否启用双击开门 */
            public double doubleClickInterval = 250;  /* 双击时间窗口 */
        }

        /* 自动零食 */
        public static class AutoSnake {
            public boolean enable = false;
            public Key activateKey = new Key(KeyCode.N);  /* 激活自动零食 */
            public boolean keepMMenu = false;  /* 是否保留菜单 */
            public boolean refillVest = false;  /* 是否使用防弹衣 */
        }
    }

    @Data
    public static class BetterLButton {
        public RapidlyClickLButtonSetting rapidlyClickLButtonSetting = new RapidlyClickLButtonSetting();
        public HoldLButtonSetting holdLButtonSetting = new HoldLButtonSetting();
        public RemapLButtonSetting remapLButtonSetting = new RemapLButtonSetting();
        public BaseSetting baseSetting = new BaseSetting();

        public static class BaseSetting {
            public boolean enable = false;
        }

        public static class HoldLButtonSetting {
            public boolean enable = false;
            public TriggerMode activateMethod = TriggerMode.TOGGLE;  /* 激活方式 0: 按住激活; 1: 切换激活 */
            public Key activateKey = new Key(KeyCode.C);
        }

        public static class RapidlyClickLButtonSetting {
            public boolean enable = false;
            public TriggerMode activateMethod = TriggerMode.TOGGLE;  /* 激活方式 0: 按住激活; 1: 切换激活 */
            public Key activateKey = new Key(KeyCode.V);
            public double triggerInterval = 20.0;
        }

        public static class RemapLButtonSetting {
            public boolean enable = false;
            public Key activateKey = new Key(KeyCode.C);
        }
    }

    /* 快速切枪 */
    @Data
    public static class QuickSwap {
        public BaseSetting baseSetting = new BaseSetting();

        public static class BaseSetting {
            public boolean enable = false;
            public boolean enableMapping1 = false;
            public boolean enableMapping2 = false;
            public boolean enableMapping3 = false;
            public boolean enableMapping4 = false;
            public boolean enableMapping5 = false;

            public boolean enableBlockKey = true;
            public Key blockKey = new Key(MouseButton.BACK);
            public double blockDuration = 500.0;
        }
    }

    /* 延迟攀 */
    @Data
    public static class DelayClimb {
        public BaseSetting baseSetting = new BaseSetting();
        public static class BaseSetting {
            @Setter public boolean enable = false;  /* 是否启用 */
            public Key toggleDelayClimbKey = new Key(KeyCode.KEY_1);  /* 启用/停止延迟攀 */
            public Key hideInCoverKey = new Key(KeyCode.Q);
            public Key usePhoneKey = new Key(KeyCode.UP);  /* 使用手机键 */
            public double triggerInterval = 3000.0;  /* 开启相机-停止相机之间的等待时间 */

            public double timeUtilCameraExited = 1300.0;  /* 等待相机退出的时间 */

            /* 之所以设置了两个等待时间，是因为第一次相机启动往往需要扫描磁盘，因此第一次启动时间要略慢于第二次，
            *  这里提供两个配置项以提供更好的灵活度 */
            public double timeUtilCameraLoaded1 = 3000.0;  /* 等待相机加载完成的时间1 */
            public double timeUtilCameraLoaded2 = 1500.0;  /* 等待相机加载完成的时间2 */

            public boolean hideInCoverOnExit = false;  /* 是否在阶段二结束时尝试躲入掩体 */
        }
    }

    /* 连发 RPG */
    @Data
    public static class AutoFire {
        public BaseSetting baseSetting = new BaseSetting();

        public static class BaseSetting {
            public boolean enable = false;  /* 是否启用 */
            public TriggerMode activateMethod = TriggerMode.HOLD;  /* 触发模式 */
            public Key activateKey = new Key(KeyCode.KEY_4);  /* 触发按键 */
            public Key heavyWeaponKey = new Key(KeyCode.KEY_2);  /* 重型武器按键 */
            public Key specialWeaponKey = new Key(KeyCode.KEY_3);  /* 特殊武器按键 */
            public double triggerInterval = 600.0;  /* 触发间隔 */
            public double mousePressInterval = 530.0;  /* 鼠标按住间隔 */
        }
    }

    /* 额外功能 */
    @Data
    public static class BetterPMenu {

        public BaseSetting baseSetting = new BaseSetting();
        public JoinANewSession joinANewSession = new JoinANewSession();
        public JoinABookmarkedJob joinABookmarkedJob = new JoinABookmarkedJob();

        public static class BaseSetting {
            public boolean enable = false;

            public double mouseScrollInterval = 10.0;
            public double enterKeyInterval = 300.0;
            public double timeUtilPMenuLoaded = 1000.0;
        }

        /* 加入新战局 */
        public static class JoinANewSession {
            public boolean enable = false;  /* 是否启用 */
            public Key activateKey = new Key(KeyCode.F6);  /* 寻找新的战局 */
            public SessionType sessionType = SessionType.INVITE_ONLY_FRIENDS_SESSION;  /* 寻找新战局类型 */
        }

        /* 加入已收藏差事 */
        public static class JoinABookmarkedJob {
            public boolean enable = false;
            public Key activateKey = new Key(KeyCode.F7);  /* 加入已收藏差事 */
            public double timeUtilJobsLoaded = 2500.0;  /* 等待差事列表加载 */
        }
    }
}

