package cc.pineclone.automation.definition;

import cc.pineclone.automation.common.TriggerMode;
import cc.pineclone.automation.Macro;
import cc.pineclone.automation.input.Key;
import cc.pineclone.automation.input.MouseButton;
import cc.pineclone.automation.input.MouseWheelScroll;

/* 切枪偷速宏定义 */
public class SwitchGlitchMacroDefinition implements MacroDefinition {

    @Override
    public String getType() {
        return "SWAP_GLITCH";
    }

    @Override
    public MacroParams getDefaultParams() {
        MacroParams params = new MacroParams();
        params.put("base.triggerMethod", TriggerMode.HOLD);  /* 激活方式 0: 按住激活; 1: 切换激活 */
        params.put("base.triggerInterval", 50.0);  /* 切枪间隔 */
        params.put("base.triggerKey", new Key(MouseButton.BACK));  /* 激活热键 */
        params.put("base.weaponWheelKey", new Key(new MouseWheelScroll(MouseWheelScroll.Direction.DOWN)));  /* 武器轮盘键 */

        params.put("swapMelee.enable", false);  /* 是否启用进入偷速时切换近战武器 */

        return null;
    }

    @Override
    public Macro createMacroInstance(MacroParams params) {
        return null;
    }
}
