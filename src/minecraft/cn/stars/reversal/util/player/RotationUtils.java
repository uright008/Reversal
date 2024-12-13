package cn.stars.reversal.util.player;

import net.minecraft.util.MathHelper;

public class RotationUtils {
    public static Rotation rotation = new Rotation(0,0);
    public static Rotation prevRotation = new Rotation(0,0);

    public static void setRotation(Rotation rotation1) {
        prevRotation.setRotation(rotation);
        rotation.setRotation(rotation1);
    }

    public static float getAngleDifference(float a, float b) {
        return MathHelper.wrapAngleTo180_float(a - b);
    }
}
