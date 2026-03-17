package cc.pineclone.automation.trigger;

public enum TriggerStatus {
    CLICK(true, false),  /* 单次点击 */
    DOUBLE_CLICK(true, false),  /* 双击 */
    HOLD_START(true, false),  /* 按住开始 */
    HOLD_STOP(false, true),  /* 按住停止 */
    TOGGLE_ON(true, false),  /* 切换启动 */
    TOGGLE_OFF(false, true),  /* 切换停止 */
    COMPOSITE_ON(true, false),  /* CompositeTrigger 触发 */
    COMPOSITE_OFF(false, true);  /* CompositeTrigger 停止 */

    final boolean asserted;
    final boolean revoked;

    TriggerStatus(boolean asserted, boolean revoked) {
        this.asserted = asserted;
        this.revoked = revoked;
    }

    /* 由 TriggerListener 负责判断触发的事件归因，判断是否触发不再由Trigger负责，Trigger仅负责传递事件 */
    public boolean isAssert() {
        return this.asserted;
    }

    public boolean isRevoke() {
        return this.revoked;
    }
}
