package cn.stars.reversal.ui.atmoic.mainmenu.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.RenderUtil;
import lombok.SneakyThrows;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class BocchiMainGui extends AtomicGui {
    private final MFont atomic32 = FontManager.getAtomic(32);
    public static boolean isPulledUp, loaded = false;
    private final TimeUtil posTimer = new TimeUtil();
    private final Animation scaleAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation posAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation bgAnimation = new Animation(Easing.EASE_OUT_EXPO, 1200);
    private final Animation posAnimation1 = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation posAnimation2 = new Animation(Easing.EASE_OUT_EXPO, 800);
    private final Animation posAnimation3 = new Animation(Easing.EASE_OUT_EXPO, 600);
    private final Animation posAnimation4 = new Animation(Easing.EASE_OUT_EXPO, 600);
    private final Animation colorAnimation1 = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation colorAnimation2 = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation colorAnimation3 = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation colorAnimation4 = new Animation(Easing.EASE_OUT_EXPO, 1000);

    public BocchiMainGui() {
        super("NewMain", "", "a");
    }

    @Override
    public void drawIcon(float posX, float posY, int color) {
        atomic32.drawString(icon, posX - 0.2, posY - 0.5, color);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovered(width / 2f - 75, height - posAnimation1.getValue(), 150, 34, mouseX, mouseY)) {
            AtomicMenu.switchGui(1);
        } else if (RenderUtil.isHovered(width / 2f - 75, height - posAnimation2.getValue(), 150, 34, mouseX, mouseY)) {
            AtomicMenu.switchGui(2);
        } else if (RenderUtil.isHovered(width / 2f - 75, height - posAnimation3.getValue(), 150, 34, mouseX, mouseY)) {
            AtomicMenu.switchGui(3);
        } else if (RenderUtil.isHovered(width / 2f - 75, height - posAnimation4.getValue(), 150, 34, mouseX, mouseY)) {
            mc.shutdown();
        } else {
            isPulledUp = !isPulledUp;
        }
        posTimer.reset();
    }

    @SneakyThrows
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!loaded) {
            scaleAnimation.run(1);
            scaleAnimation.finishNow();
            loaded = true;
        }

        if (isPulledUp) {
            scaleAnimation.run(0.5);
            posAnimation.run(100);
            bgAnimation.run(height / 2f + 180);
            posAnimation1.run(height / 2f + 100);
            if (posTimer.hasReached(100L)) posAnimation2.run(height / 2f + 50);
            else posAnimation2.run(0);
            if (posTimer.hasReached(200L)) posAnimation3.run(height / 2f);
            else posAnimation3.run(0);
            if (posTimer.hasReached(300L)) posAnimation4.run(height / 2f - 50);
            else posAnimation4.run(0);
        } else {
            scaleAnimation.run(1);
            posAnimation.run(0);
            bgAnimation.run(0);
            posAnimation1.run(0);
            posAnimation2.run(0);
            posAnimation3.run(0);
            posAnimation4.run(0);
        }

        RenderUtil.roundedRectangle(width / 2f - 120, height - bgAnimation.getValue(), 240, 320, 2, ColorUtil.WHITE);
        RenderUtil.triangle(width / 2f - 15, height - bgAnimation.getValue() + 320, 30, 25, true, ColorUtil.WHITE);
        RenderUtil.rect(width / 2f - 80, height - bgAnimation.getValue() + 50, 160, 0.5, Color.LIGHT_GRAY);

        GlStateManager.pushMatrix();
        GlStateManager.scale(scaleAnimation.getValue(), scaleAnimation.getValue(), scaleAnimation.getValue());
        FontManager.getHandwrite(128).drawCenteredString("リバーサル", (width / 2f) * (1 / scaleAnimation.getValue()), (height / 2f - 60 - posAnimation.getValue()) * (1 / scaleAnimation.getValue()), new Color(250 - (int)(posAnimation.getValue() * 1.8), 250 - (int)(posAnimation.getValue() * 1.8),250 - (int)(posAnimation.getValue() * 1.8), 255).getRGB());
        GlStateManager.popMatrix();

        regular16.drawCenteredString(Reversal.VERSION, width / 2f, height - bgAnimation.getValue() + 60, ColorUtil.BLACK.getRGB());

        if (isPulledUp) {
            FontManager.getHandwrite(32).drawCenteredString("- Click anywhere -", width / 2f, height / 2f + 40 - posAnimation.getValue(), ColorUtil.reAlpha(ColorUtil.whiteAnimation.getOutput(), Math.max(0, 150 - (int)posAnimation.getValue() * 2)).getRGB());
        } else {
            FontManager.getHandwrite(32).drawCenteredString("- Click anywhere -", width / 2f, height / 2f + 40 - posAnimation.getValue(), ColorUtil.whiteAnimation.getOutput().getRGB());
        }

        colorAnimation1.run(RenderUtil.isHovered(width / 2f - 80, height - posAnimation1.getValue(), 160, 34, mouseX, mouseY) ? 1 : 0);
        RenderUtil.roundedRectangle(width / 2f - 80, height - posAnimation1.getValue(), 160, 34, 6, ColorUtil.colorToColor(ColorUtil.BLACK, ColorUtil.PINK, colorAnimation1));
        RenderUtil.roundedRectangle(width / 2f - 70, height - posAnimation1.getValue() + 5, 24, 24, 12, ColorUtil.colorToColor(new Color(80, 80, 80, 255), ColorUtil.WHITE, colorAnimation1));
        FontManager.getAtomic(24).drawString("b", width / 2f - 64, height - posAnimation1.getValue() + 14, ColorUtil.colorToColor(ColorUtil.WHITE, ColorUtil.PINK, colorAnimation1).getRGB());
        FontManager.getSF(24).drawStringWithShadow("SinglePlayer", width / 2f - 25, height - posAnimation1.getValue() + 13, ColorUtil.WHITE.getRGB());
        FontManager.getSF(24).drawStringWithShadow(">                      <", width / 2f - 35, height - posAnimation1.getValue() + 13, ColorUtil.colorToColor(ColorUtil.transparent, ColorUtil.WHITE, colorAnimation1).getRGB());

        colorAnimation2.run(RenderUtil.isHovered(width / 2f - 80, height - posAnimation2.getValue(), 160, 34, mouseX, mouseY) ? 1 : 0);
        RenderUtil.roundedRectangle(width / 2f - 80, height - posAnimation2.getValue(), 160, 34, 6, ColorUtil.colorToColor(ColorUtil.BLACK, ColorUtil.PINK, colorAnimation2));
        RenderUtil.roundedRectangle(width / 2f - 70, height - posAnimation2.getValue() + 5, 24, 24, 12, ColorUtil.colorToColor(new Color(80, 80, 80, 255), ColorUtil.WHITE, colorAnimation2));
        FontManager.getAtomic(24).drawString("c", width / 2f - 65, height - posAnimation2.getValue() + 14, ColorUtil.colorToColor(ColorUtil.WHITE, ColorUtil.PINK, colorAnimation2).getRGB());
        FontManager.getSF(24).drawStringWithShadow("MultiPlayer", width / 2f - 22, height - posAnimation2.getValue() + 13, ColorUtil.WHITE.getRGB());
        FontManager.getSF(24).drawStringWithShadow(">                    <", width / 2f - 32, height - posAnimation2.getValue() + 13, ColorUtil.colorToColor(ColorUtil.transparent, ColorUtil.WHITE, colorAnimation2).getRGB());

        colorAnimation3.run(RenderUtil.isHovered(width / 2f - 80, height - posAnimation3.getValue(), 160, 34, mouseX, mouseY) ? 1 : 0);
        RenderUtil.roundedRectangle(width / 2f - 80, height - posAnimation3.getValue(), 160, 34, 6, ColorUtil.colorToColor(ColorUtil.BLACK, ColorUtil.PINK, colorAnimation3));
        RenderUtil.roundedRectangle(width / 2f - 70, height - posAnimation3.getValue() + 5, 24, 24, 12, ColorUtil.colorToColor(new Color(80, 80, 80, 255), ColorUtil.WHITE, colorAnimation3));
        FontManager.getAtomic(24).drawString("d", width / 2f - 63, height - posAnimation3.getValue() + 14, ColorUtil.colorToColor(ColorUtil.WHITE, ColorUtil.PINK, colorAnimation3).getRGB());
        FontManager.getSF(24).drawStringWithShadow("Options", width / 2f - 15, height - posAnimation3.getValue() + 13, ColorUtil.WHITE.getRGB());
        FontManager.getSF(24).drawStringWithShadow(">               <", width / 2f - 25, height - posAnimation3.getValue() + 13, ColorUtil.colorToColor(ColorUtil.transparent, ColorUtil.WHITE, colorAnimation3).getRGB());

        colorAnimation4.run(RenderUtil.isHovered(width / 2f - 80, height - posAnimation4.getValue(), 160, 34, mouseX, mouseY) ? 1 : 0);
        RenderUtil.roundedRectangle(width / 2f - 80, height - posAnimation4.getValue(), 160, 34, 6, ColorUtil.colorToColor(ColorUtil.BLACK, ColorUtil.PINK, colorAnimation4));
        RenderUtil.roundedRectangle(width / 2f - 70, height - posAnimation4.getValue() + 5, 24, 24, 12, ColorUtil.colorToColor(new Color(80, 80, 80, 255), ColorUtil.WHITE, colorAnimation4));
        FontManager.getAtomic(24).drawString("5", width / 2f - 63, height - posAnimation4.getValue() + 14, ColorUtil.colorToColor(ColorUtil.WHITE, ColorUtil.PINK, colorAnimation4).getRGB());
        FontManager.getSF(24).drawStringWithShadow("Exit", width / 2f - 5, height - posAnimation4.getValue() + 13, ColorUtil.WHITE.getRGB());
        FontManager.getSF(24).drawStringWithShadow(">        <", width / 2f - 15, height - posAnimation4.getValue() + 13, ColorUtil.colorToColor(ColorUtil.transparent, ColorUtil.WHITE, colorAnimation4).getRGB());

        regular16.drawString(Reversal.NAME + " " + Reversal.VERSION, 2, height - 30, Color.WHITE.getRGB());
        regular16.drawString("Minecraft " + Reversal.MINECRAFT_VERSION, 2, height - 20, Color.WHITE.getRGB());
        regular16.drawString("OptiFine_1.8.9_HD_U_M6_pre2", 2, height - 10, Color.WHITE.getRGB());

        String license = "© 2025 Aerolite Society. 保留部分权利. Co-developed by Stars.";
        regular16.drawString(license, width - regular16.width(license), height - 10, Color.WHITE.getRGB());
    }
}
