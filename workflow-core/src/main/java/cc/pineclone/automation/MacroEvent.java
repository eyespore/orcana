package cc.pineclone.automation;

import cc.pineclone.automation.trigger.TriggerEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MacroEvent {

    private final TriggerEvent triggerEvent;
    private final MacroContext macroContext;  /* 宏上下文 */

    public MacroEvent(TriggerEvent triggerEvent, MacroContext macroContext) {
        this.triggerEvent = triggerEvent;
        this.macroContext = macroContext;
    }

    /* TriggerEvent 派生 ActionEvent */
    public static MacroEvent of(TriggerEvent event, Macro macro) {
        MacroContext context = new MacroContext(macro.getStatus(), macro.getExecutionStatus());
        return new MacroEvent(event, context);
    }

    /* 宏上下文 */
    @AllArgsConstructor
    @Getter
    public static class MacroContext {
        private Macro.MacroStatus status;  /* 是否被停止 */
        private Macro.MacroExecutionStatus executionStatus;
    }
}
