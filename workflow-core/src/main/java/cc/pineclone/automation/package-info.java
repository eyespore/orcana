/**
 * <p>
 *  宏功能实现包，实际的宏执行使用{@link cc.pineclone.server.core.macro.SimpleMacro}
 *  这是一个绑定器，它通过绑定一个“触发器”以及一个“动作”，来描述这个动作如何触发，这套宏系统采用双桥接
 *  设计模式，配合策略、工厂、享元实现了一个高拓展性的宏功能构建模式
 * </p>
 * <p>
 *  有关双桥接，{@link cc.pineclone.automation.trigger.Trigger}触发器作为第一道桥，桥接其
 *  触发源(按键、鼠标、滚轮...)与触发行为(长按、点按、按住...)的联系，将触发器作为桥的一侧，宏行为
 * {@link cc.pineclone.automation.action.Action}作为桥的另一侧，这是第二个桥接，{@link cc.pineclone.server.core.macro.SimpleMacro}
 * 作为两者的桥，构建其触发器Trigger和行为Action的联系
 * </p>
 *
 * @see cc.pineclone.server.macro.action 描述宏执行的具体动作
 * @see cc.pineclone.server.macro.trigger 描述宏如何触发
 */
package cc.pineclone.automation;