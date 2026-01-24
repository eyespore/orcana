package cc.pineclone.automation.action.impl.actionext;

import cc.pineclone.automation.action.Action;
import cc.pineclone.automation.MacroEvent;

/**
 *  该动作用于作为封锁计时器，当激活该Action时，会进入时长为blockDuration的倒计时
 *
 *  <li>倒计时结束之前该Action的isBlocked()方法将会返回true，倒计时结束之后，isBlocked()将会返回false
 *
 *  因此外部可以结合BlockAction作为子Action来实现一些屏蔽Action的效果，例如按下按键A之后的50ms内，按键B对应的
 *  Action将不再响应
 */
public class BlockAction extends Action {
    private final static String ACTION_ID = "action-ext";

    private final long blockDuration;
    private long blockStartTime;
    private boolean blocked = false;

    public BlockAction(long blockDuration) {
        super(ACTION_ID);
        this.blockDuration = blockDuration;
    }

    @Override
    public void activate(MacroEvent event) {
        this.blocked = true;  /* 触发封锁 */
    }

    @Override
    public void deactivate(MacroEvent event) {
        this.blocked = false;
        blockStartTime = System.currentTimeMillis();
    }

    public boolean isBlocked() {
        if (this.blocked) return true;
        return (System.currentTimeMillis() - blockStartTime) < blockDuration;
    }
}
