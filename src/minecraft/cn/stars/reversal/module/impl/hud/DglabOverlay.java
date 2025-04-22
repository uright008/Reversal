package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.event.impl.Shader3DEvent;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.ColorUtils;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.ModeValue;

import java.awt.*;

@ModuleInfo(name = "DglabOverlay", localizedName = "module.DglabOverlay.name", description = "Display dglab device information", localizedDescription = "module.DglabOverlay.desc", category = Category.HUD)
public class DglabOverlay extends Module {
    private final ModeValue mode = new ModeValue("Mode", this, "Simple", "Simple", "Modern", "ThunderHack", "Empathy");
    public final ColorValue colorValue = new ColorValue("Color", this);
    MFont psb = FontManager.getPSB(20);

    public DglabOverlay() {
        setX(100);
        setY(100);
        setWidth(50);
        setHeight(100);
        setCanBeEdited(true);
    }

    @Override
    public void onShader3D(Shader3DEvent event) {
        int x = getX() + 1;
        int y = getY() + 1;
        Color color = colorValue.getColor();

        if (mode.getMode().equals("Modern")) {
            if (event.isBloom()) RoundedUtil.drawRound(x - 2, y - 4, 48, 64, 4, color);
            else RoundedUtil.drawRound(x - 2, y - 4, 48, 64, 4, Color.BLACK);
        } else if (mode.getMode().equals("ThunderHack")) {
            RoundedUtil.drawGradientRound(x - 3.5f, y - 5.5f, 51, 67, 4,
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
        } else if (mode.getMode().equals("Simple")) {
            RenderUtil.rect(x - 2, y - 4, 48, 64, Color.BLACK);
        } else if (mode.getMode().equals("Empathy")) {
            RenderUtil.roundedRectangle(x - 4, y - 4, 50, 64, 3f, ColorUtil.empathyGlowColor());
            RenderUtil.roundedRectangle(x - 4.5, y - 1.5, 1.5, psb.height() - 2.5, 3f, color);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int x = getX() + 1;
        int y = getY() + 1;
        Color color = colorValue.getColor();

        if (mode.getMode().equals("Modern")) {
            RoundedUtil.drawRound(x - 2, y - 4, 48, 64, 4, new Color(0, 0, 0, 80));
            RenderUtil.roundedOutlineRectangle(x - 3, y - 5, 50, 66, 3, 1, color);
        } else if (mode.getMode().equals("ThunderHack")) {
            RoundedUtil.drawGradientRound(x - 3.5f, y - 5.5f, 51, 67, 4,
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
            RoundedUtil.drawRound(x - 3, y - 5, 50, 66, 4, new Color(0, 0, 0, 220));
        } else if (mode.getMode().equals("Simple")) {
            RenderUtil.rect(x - 2, y - 4, 48, 64, new Color(0, 0, 0, 80));
        } else if (mode.getMode().equals("Empathy")) {
            RenderUtil.roundedRectangle(x - 4, y - 4, 50, 64, 3f, ColorUtil.empathyColor());
            RenderUtil.roundedRectangle(x - 4.5, y - 1.5, 1.5, psb.height() - 2.5, 1f, color);
        }

        psb.drawString("Info", x + 2, y - 0.7f, new Color(250, 250, 250, 200).getRGB());

        psm18.drawString("Connected", x + 1, y + 10, new Color(250, 250, 250, 200).getRGB());
        psm18.drawString("A:" , x + 1, y + 10, new Color(250, 250, 250, 200).getRGB());
        psm18.drawString("Connected", x + 1, y + 10, new Color(250, 250, 250, 200).getRGB());
    }
}
