package cn.stars.reversal.ui.splash.impl;

import cn.stars.reversal.ui.splash.LoadingScreenRenderer;
import cn.stars.reversal.ui.splash.SplashScreen;
import cn.stars.reversal.ui.splash.util.Image;
import cn.stars.reversal.ui.splash.util.Rect;
import cn.stars.reversal.util.math.TimerUtil;
import cn.stars.reversal.util.render.ColorUtil;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FadeInOutLoadingScreen extends LoadingScreenRenderer {


    FadeInOutImage gs1;

    TimerUtil startTimer = new TimerUtil();
    boolean firstFrame = false;

    @Override
    public void init() {
        super.init();
        gs1 = new FadeInOutImage(new ResourceLocation("reversal/images/splash.png"));
    }

    @Override
    public void render(int width, int height) {
        Rect.draw(0, 0, width, height, ColorUtil.hexColor(0,0,0, 255), Rect.RectType.ABSOLUTE_POSITION);

        if (!firstFrame) {
            firstFrame = true;
            startTimer.reset();
        }

        if (!startTimer.hasTimeElapsed(1000))
            return;

        if (!gs1.isFinished())
            gs1.render(width, height);
    }

    @Override
    public boolean isLoadingScreenFinished() {
        return gs1.isFinished();
    }

    private static class FadeInOutImage {

        @Getter
        private final ResourceLocation img;

        float screenMaskAlpha = 0;
        boolean increasing = true;

        @Getter
        boolean finished = false;

        boolean firstFrame = false;

        TimerUtil timer = new TimerUtil();

        public FadeInOutImage(ResourceLocation loc) {
            img = loc;
        }

        public void render(int width, int height) {

            if (!firstFrame) {
                firstFrame = true;
                timer.reset();
            }

            if (increasing || SplashScreen.waiting) {
                screenMaskAlpha += increasing ? 1 * 0.003921568627451F : -1 * 0.003921568627451F;
            }

            if ((!increasing && screenMaskAlpha < 0.01))
                finished = true;

            if (increasing && screenMaskAlpha > 0.99) {
                increasing = false;
                timer.reset();
            }

            GL11.glColor4f(1,1,1, screenMaskAlpha);
            Image.draw(img, 0, 0, width, height, Image.Type.NoColor);
        }

    }
}
