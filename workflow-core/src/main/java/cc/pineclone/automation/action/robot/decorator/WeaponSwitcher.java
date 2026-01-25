package cc.pineclone.automation.action.robot.decorator;

import cc.pineclone.automation.action.robot.VCRobotAdapter;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeaponSwitcher extends VCRobotAdapter {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public WeaponSwitcher(VCRobotAdapter delegate) {
        super(delegate.getRobot());
    }

    public void switchTo(WeaponType type) {
        switch (type) {
            case SNIPER_RIFLE -> moveMouse(1000, 0);
            case ASSAULT_RIFLE -> moveMouse(1000, -200);
            case MELEE_WEAPON -> moveMouse(-1000, 1000);
            default -> {}
        }
    }

    private void moveMouse(int xOffset, int yOffset) {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point current = pointerInfo.getLocation();

        int steps = 5;
        int delayMs = 5;

        int xCurrent = current.x;
        int yCurrent = current.y;

        int xStepLength = xOffset / steps;
        int yStepLength = yOffset / steps;

        final int[] step = {1};

        executor.scheduleAtFixedRate(() -> {
            if (step[0] > steps) return;
            int xDist = xCurrent + xStepLength * step[0];
            int yDist = yCurrent + yStepLength * step[0];
            robot.mouseMove(xDist, yDist);
            step[0]++;
        }, 0, delayMs, TimeUnit.MILLISECONDS);
    }
}
