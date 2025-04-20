package cn.stars.reversal.ui.modern;

import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.RenderUtil;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

import java.awt.*;

@NativeObfuscation
public class TextButton extends MenuButton {

    private MFont FONT_RENDERER = FontManager.getRegular(24);
    private final MFont ICON_RENDERER = FontManager.getCur(32);

    public boolean left = false;
    public String name;
    public String icon;
    public int iconX;
    public int textY;
    public int textX;

    public TextButton(double x, double y, double width, double height, Runnable runnable, String name, String icon) {
        super(x, y, width, height, runnable);
        this.name = name;
        this.icon = icon;
    }

    public TextButton(double x, double y, double width, double height, Runnable runnable, String name, String icon, boolean left, int iconX, int textX, int textY) {
        super(x, y, width, height, runnable);
        this.name = name;
        this.icon = icon;
        this.left = left;
        this.iconX = iconX;
        this.textX = textX;
        this.textY = textY;
        this.FONT_RENDERER = FontManager.getRegular(24);
    }

    public TextButton(double x, double y, double width, double height, Runnable runnable, String name, String icon, boolean left, int iconX, int textX, int textY, int fontSize) {
        super(x, y, width, height, runnable);
        this.name = name;
        this.icon = icon;
        this.left = left;
        this.iconX = iconX;
        this.textX = textX;
        this.textY = textY;
        this.FONT_RENDERER = FontManager.getRegular(fontSize);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        // Runs the animation update - keep this
        super.draw(mouseX, mouseY, partialTicks);
        // Colors for rendering
        final double value = getY();
        final Color fontColor = enabled ? ColorUtil.reAlpha(new Color(250, 250, 250, 250), (int) this.getCuriosityFontAnimation().getValue()) : new Color(200, 200, 200, 100);

        // Renders the button text
        UI_BLOOM_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(this.getX(), value, this.getWidth(), this.getHeight(), 4,
                    ColorUtil.reAlpha(new Color(30, 30, 30, 230), (int) this.getCuriosityAnimation().getValue()));
//            RenderUtil.roundedOutlineRectangle(this.getX(), value, this.getWidth(), this.getHeight(), 5, 0.5f, ColorUtil.reAlpha(Color.WHITE, (int) ((int) this.getHoverAnimation().getValue() / 1.7f)));
            RenderUtil.roundedOutlineRectangle(this.getX(), value, this.getWidth(), this.getHeight(), 3, 1,
                    ColorUtil.reAlpha(new Color(250, 250, 250, 250), (int) (getCuriosityBorderAnimation().getValue())));
            if (this.left) {
                ICON_RENDERER.drawString(this.icon, (float) (this.getX() + iconX), (float) (value + textY + 1), fontColor.getRGB());
                FONT_RENDERER.drawString(this.name, (float) (this.getX() + textX), (float) (value + textY + 2), fontColor.getRGB());
            } else {
                ICON_RENDERER.drawCenteredString(this.icon, (float) (this.getX() + this.getWidth() / 2.0F), (float) (value + this.getHeight() / 2.0F - 15), fontColor.getRGB());
                FONT_RENDERER.drawCenteredString(this.name, (float) (this.getX() + this.getWidth() / 2.0F), (float) (value + this.getHeight() / 2.0F + 5), fontColor.getRGB());
            }
        });
    }
}
