package cn.stars.reversal.ui.splash.impl;

import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.splash.LoadingScreenRenderer;
import cn.stars.reversal.ui.splash.SplashScreen;
import cn.stars.reversal.ui.splash.utils.Image;
import cn.stars.reversal.ui.splash.utils.Rect;
import cn.stars.reversal.util.math.TimerUtil;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.video.VideoManager;
import cn.stars.reversal.util.render.video.VideoUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class VideoLoadingScreen extends LoadingScreenRenderer {

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void render(int width, int height) {
        Rect.draw(0, 0, width, height, ColorUtil.hexColor(0,0,0, 255), Rect.RectType.ABSOLUTE_POSITION);

        // splash.mp4
        VideoUtil.render(0,0,width,height);
    }

    @Override
    public boolean isLoadingScreenFinished() {
        return true;
    }
}
