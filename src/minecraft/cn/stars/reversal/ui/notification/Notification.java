package cn.stars.reversal.ui.notification;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public final class Notification implements GameInstance {

    private final String description;
    private final String title;
    private final NotificationType type;
    private final TimeUtil timer = new TimeUtil();
    MFont icon = FontManager.getCheck(24);
    MFont psb = FontManager.getRegularBold(24);
    MFont psm = FontManager.getRegular(20);
    private long delay, start, end;
    private ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    public float yVisual = sr.getScaledHeight() - 50;
    public float y = sr.getScaledHeight() - 50;
    private float xVisual = sr.getScaledWidth();

    public Notification(final String description, final String title, final long delay, final NotificationType type) {
        this.description = description;
        this.title = title;
        this.delay = delay;
        this.type = type;

        start = System.currentTimeMillis();
        end = start + delay;
    }

    public long getStart() {
        return start;
    }

    public void setStart(final long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(final long end) {
        this.end = end;
    }

    public String getDescription() {
        return description;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(final long delay) {
        this.delay = delay;
    }

    public void render() {
        sr = new ScaledResolution(Minecraft.getMinecraft());

        final String name = StringUtils.capitalize(type.name().toLowerCase());
        Color sideColor = new Color(-1);
        final float screenWidth = sr.getScaledWidth();
        float x = (screenWidth) - (Math.max(psm.getWidth(description), psb.getWidth(name))) - 6;
        String iconString = "b";

        final float curr = System.currentTimeMillis() - getStart();
        final float percentageLeft = curr / getDelay();

        if (percentageLeft > 0.9) x = screenWidth;

        if (timer.hasReached(1000 / 60)) {
            xVisual = lerp(xVisual, x, 0.2f);
            yVisual = lerp(yVisual, y, 0.2f);
            timer.reset();
        }

        switch (type) {
            case NOTIFICATION:
                sideColor = new Color(210, 210, 210, 200);
                iconString = "m";
                break;
            case WARNING:
                sideColor = new Color(255, 255, 120, 200);
                iconString = "r";
                break;
            case ERROR:
                sideColor = new Color(255, 50, 50, 200);
                iconString = "p";
                break;
            case SUCCESS:
                sideColor = new Color(50, 255, 50, 200);
                iconString = "o";
                break;
        }

        //    RenderUtil.roundedRectangle(xVisual - 1, yVisual + 6, 2, 8, 2, sideColor);

        Color finalSideColor = sideColor;
        String finalIconString = iconString;

        int offset = 0;

        if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
            MODERN_BLUR_RUNNABLES.add(() -> {
                RenderUtil.roundedRectangle(xVisual, yVisual - 3 - offset, sr.getScaledWidth() - xVisual, 25, 2, Color.BLACK);
            });

        }
        if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
            MODERN_BLOOM_RUNNABLES.add(() -> {
                RenderUtil.roundedRectangle(xVisual, yVisual - 3 - offset, sr.getScaledWidth() - xVisual, 25, 2, Color.BLACK);
            });
        }

        RenderUtil.roundedRectangle(xVisual, yVisual - 3, sr.getScaledWidth() - xVisual, 25, 2, new Color(0, 0, 0, 100));

        RenderUtil.roundedRectangle(xVisual + (percentageLeft * (gs.getWidth(description)) + 8), yVisual + 21, screenWidth + 1, 1, 2, sideColor);
        icon.drawString(finalIconString, xVisual + 4, yVisual + 1, finalSideColor.getRGB());
        psb.drawString(title, xVisual + 6 + icon.getWidth(finalIconString), yVisual, new Color(255, 255, 255, 220).getRGB());
        psm.drawString(description, xVisual + 4, yVisual + 12.5, new Color(255, 255, 255, 220).getRGB());

        MODERN_BLOOM_RUNNABLES.add(() -> {
            //    RenderUtil.roundedRectCustom(xVisual, yVisual - 3, sr.getScaledWidth() - xVisual, 25, 2, new Color(0, 0, 0, 100), true, false, true, false);
            RenderUtil.roundedRectangle(xVisual + (percentageLeft * (gs.getWidth(description)) + 8), yVisual + 21 - offset, screenWidth + 1, 1, 2, finalSideColor);
        });
    }

    public float lerp(final float a, final float b, final float c) {
        return a + c * (b - a);
    }
}
