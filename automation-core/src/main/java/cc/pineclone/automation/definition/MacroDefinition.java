package cc.pineclone.automation.definition;

import cc.pineclone.automation.Macro;

/**
 * 宏实例定义类，每一个宏都应该有一个具体对应的Definition，表述该宏应该如何被创建，需要哪些创建参数
 */
public interface MacroDefinition {

    String getType();  /* 宏类型 */

    MacroParams getDefaultParams();  /* 宏创建默认参数 */

    Macro createMacroInstance(MacroParams params);  /* 创建宏实例 */

}
