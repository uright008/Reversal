package cn.stars.reversal.util.player;

import org.lwjgl.Sys;

public class SmoothSneakUtil {
    private boolean lastState;
    private boolean isAnimationDone;
    private float lastOperationTime;
    private float lastX;

    private static float getUpY(float x) {
        // quadratic function
        return -0.2F * x * x;
    }

    private static float getDownY(float x) {
        // quadratic function
        x--;
        return 0.2F * x * x - 0.2F;
    }

    public float getSneakingHeightOffset(boolean isSneaking) {
        if (lastState == isSneaking) {
            if (isAnimationDone) {
                return isSneaking ? -0.2F : 0F;
            }
        } else {
            lastState = isSneaking;
            isAnimationDone = false;
        }
        float now = ((float) (Sys.getTime() << 3)) / Sys.getTimerResolution();
        float timeDiff = now - lastOperationTime;
        if (lastOperationTime == 0F) timeDiff = 0F;
        lastOperationTime = now;
        if (isSneaking) {
            if (lastX < 1F) {
                lastX += timeDiff;
                if (lastX > 1F) lastX = 1F;
                return getDownY(lastX);
            } else {
                lastX = 1F;
                isAnimationDone = true;
                lastOperationTime = 0F;
                return -0.2F;
            }
        } else {
            if (lastX > 0) {
                lastX -= timeDiff;
                if (lastX < 0F) lastX = 0F;
                return getUpY(lastX);
            } else {
                lastX = 0F;
                isAnimationDone = true;
                lastOperationTime = 0F;
                return 0F;
            }
        }
    }
}