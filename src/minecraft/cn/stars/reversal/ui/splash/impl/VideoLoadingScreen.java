package cn.stars.reversal.ui.splash.impl;

import cn.stars.reversal.ui.splash.LoadingScreenRenderer;
import cn.stars.reversal.ui.splash.util.Rect;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.video.VideoUtil;

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
