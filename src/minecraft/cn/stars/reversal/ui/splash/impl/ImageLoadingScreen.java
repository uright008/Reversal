package cn.stars.reversal.ui.splash.impl;

import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.splash.LoadingScreenRenderer;
import cn.stars.reversal.ui.splash.SplashScreen;
import cn.stars.reversal.ui.splash.util.Image;
import cn.stars.reversal.ui.splash.util.Rect;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.math.TimerUtil;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ImageLoadingScreen extends LoadingScreenRenderer {

    TimerUtil startTimer = new TimerUtil();
    boolean firstFrame = false;

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void render(int width, int height) {
        Rect.draw(0, 0, width, height, ColorUtil.hexColor(0,0,0, 255), Rect.RectType.ABSOLUTE_POSITION);
        if (!firstFrame) {
            firstFrame = true;
            startTimer.reset();
        }
        if (startTimer.hasTimeElapsed(200L)) {
        //    Image.draw(new ResourceLocation("reversal/images/splash.png"), 0, 0, width, height, Image.Type.NoColor);
            GlStateManager.color(1f,1f,1f,1f);
            RenderUtil.image(new ResourceLocation("reversal/images/splash.png"), 0,0, width, height);
            FontManager.getHandwrite(256).drawCenteredStringWithShadow("リバーサル", width / 2f, height / 2f - 100, Color.WHITE.getRGB());

        }
    }

    @Override
    public boolean isLoadingScreenFinished() {
        return SplashScreen.animation2.isFinished();
    }
}
