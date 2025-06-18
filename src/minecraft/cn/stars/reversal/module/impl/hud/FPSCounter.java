package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.event.impl.Shader3DEvent;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.ColorUtils;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.ModeValue;
import net.minecraft.client.Minecraft;

import java.awt.*;

@ModuleInfo(name = "FPSCounter", localizedName = "module.FPSCounter.name", description = "Show your fps on screen",
        localizedDescription = "module.FPSCounter.desc", category = Category.HUD)
public class FPSCounter extends Module {
    private final ModeValue mode = new ModeValue("Mode", this, "Simple", "Minecraft", "Modern", "Simple", "Empathy", "ThunderHack", "Shader");
    public final ColorValue colorValue = new ColorValue("Color", this);
    private final BoolValue background = new BoolValue("Background", this, true);
    public FPSCounter() {
        setCanBeEdited(true);
        setWidth(100);
        setHeight(20);
    }
    private final Animation rectAnimation = new Animation(Easing.EASE_OUT_EXPO, 500);

    @Override
    public void onShader3D(Shader3DEvent event) {
        if (background.isEnabled()) {
            switch (mode.getMode()) {
                case "Modern":
                    if (event.isBloom())
                        RenderUtil.roundedRectangle(getX() + 2, getY() - 1, 20 + rectAnimation.getValue(), regular18.getHeight() + 3, roundStrength, colorValue.getColor());
                    else
                        RenderUtil.roundedRectangle(getX() + 2, getY() - 1, 20 + rectAnimation.getValue(), regular18.getHeight() + 3, roundStrength, Color.BLACK);
                    break;
                case "ThunderHack":
                    RoundedUtil.drawGradientRound(getX() + 0.5f, getY() - 2.5f, 22 + (float)rectAnimation.getValue(), regular18.getHeight() + 6, roundStrength,
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    break;
                case "Simple":
                    RenderUtil.rect(getX() + 2, getY() - 1, 19 + rectAnimation.getValue(), regular18.getHeight() + 3, Color.BLACK);
                    break;
                case "Shader":
                    if (event.isBloom())
                        RenderUtil.rectForShaderTheme(getX() + 2, getY() - 1, 21 + rectAnimation.getValue(), regular18.getHeight() + 1.5, colorValue, true);
                    else
                        RenderUtil.roundedRectangle(getX() + 2, getY() - 1, 21 + rectAnimation.getValue(), regular18.getHeight() + 1.5, roundStrength, Color.BLACK);
                    break;
                case "Empathy":
                    RenderUtil.roundedRectangle(getX(), getY() - 1, 21 + rectAnimation.getValue(), regular18.getHeight() + 3, 3f, ColorUtil.empathyGlowColor());
                    RenderUtil.roundedRectangle(getX() - 0.5, getY() + 1.5, 1.5, regular18.getHeight() - 2.5, 1f, colorValue.getColor());
                    break;
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String fpsString = Minecraft.getDebugFPS() + " FPS";
        if (ModuleInstance.getModule(ClientSettings.class).hudTextWithBracket.enabled) fpsString = "[" + Minecraft.getDebugFPS() + " FPS]";
        Color color = colorValue.getColor();

        int offsetX = 0;
        int offsetY = 0;

        if (background.isEnabled()) {
            rectAnimation.run(regular18.getWidth(fpsString));
            switch (mode.getMode()) {
                case "Modern":
                    RenderUtil.roundedRectangle(getX() + 2, getY() - 1, 20 + rectAnimation.getValue(), regular18.getHeight() + 3, roundStrength, new Color(0, 0, 0, 80));
                    RenderUtil.roundedOutlineRectangle(getX() + 1, getY() - 2, 22 + rectAnimation.getValue(), regular18.getHeight() + 5, roundStrength, 1, color);
                    break;
                case "ThunderHack":
                    RoundedUtil.drawGradientRound(getX() + 0.5f, getY() - 2.5f, 22 + (float)rectAnimation.getValue(), regular18.getHeight() + 6, roundStrength,
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    RoundedUtil.drawRound(getX() + 1, getY() - 2, 21 + (float)rectAnimation.getValue(), regular18.getHeight() + 5, roundStrength, new Color(0, 0, 0, 220));
                    break;
                case "Simple":
                    RenderUtil.rect(getX() + 2, getY() - 1, 19 + rectAnimation.getValue(), regular18.getHeight() + 3, new Color(0, 0, 0, 80));
                    break;
                case "Shader":
                    RenderUtil.rectForShaderTheme(getX() + 2, getY() - 1, 21 + rectAnimation.getValue(), regular18.getHeight() + 1.5, colorValue, false);
                    offsetY = -1;
                    break;
                case "Empathy":
                    RenderUtil.roundedRectangle(getX(), getY() - 1, 21 + rectAnimation.getValue(), regular18.getHeight() + 3, 3f, ColorUtil.empathyColor());
                    RenderUtil.roundedRectangle(getX() - 0.5, getY() + 1.5, 1.5, regular18.getHeight() - 2.5, 1f, color);
                    offsetX = -1;
                    break;
                case "Minecraft":
                    RenderUtil.rect(getX() - 0.5, getY() - 0.5, mc.fontRendererObj.getStringWidth(fpsString) + 4, mc.fontRendererObj.FONT_HEIGHT + 3, new Color(0,0,0,100));
            }
        }
        if (mode.getMode().equals("Minecraft")) {
            mc.fontRendererObj.drawStringWithShadow(fpsString, getX() + 2, getY() + 2, Color.WHITE.getRGB());
        } else {
            FontManager.getSpecialIcon(16).drawString("e", getX() + 6 + offsetX, getY() + 6 + offsetY, color.getRGB());
            regular18.drawString(fpsString, getX() + 18 + offsetX, getY() + 3.5f + offsetY, new Color(250, 250, 250, 200).getRGB());
        }
    }
}
