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

import java.awt.*;

@ModuleInfo(name = "PingCounter", localizedName = "module.PingCounter.name", description = "Show your ping on screen",
        localizedDescription = "module.PingCounter.desc", category = Category.HUD)
public class PingCounter extends Module {
    private final ModeValue mode = new ModeValue("Mode", this, "Simple", "Simple", "Modern", "ThunderHack", "Empathy", "Minecraft", "Shader");
    public final ColorValue colorValue = new ColorValue("Color", this);
    private final BoolValue background = new BoolValue("Background", this, true);
    public PingCounter() {
        setCanBeEdited(true);
        setWidth(100);
        setHeight(20);
    }
    private final Animation rectAnimation = new Animation(Easing.EASE_OUT_EXPO, 500);
    private String pingString;

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
        if (mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()) != null) {
            pingString = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime() + " ms";
            if (ModuleInstance.getModule(ClientSettings.class).hudTextWithBracket.enabled)
                pingString = "[" + mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime() + " ms]";
        } else {
            pingString = "0 ms";
            if (ModuleInstance.getModule(ClientSettings.class).hudTextWithBracket.enabled)
                pingString = "[0 ms]";
        }

        int offsetX = 0;
        int offsetY = 0;

        if (background.isEnabled()) {
            rectAnimation.run(regular18.getWidth(pingString));
            switch (mode.getMode()) {
                case "Modern":
                    RenderUtil.roundedRectangle(getX() + 2, getY() - 1, 20 + rectAnimation.getValue(), regular18.getHeight() + 3, roundStrength, new Color(0, 0, 0, 80));
                    RenderUtil.roundedOutlineRectangle(getX() + 1, getY() - 2, 22 + rectAnimation.getValue(), regular18.getHeight() + 5, roundStrength, 1, colorValue.getColor());
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
                    RenderUtil.roundedRectangle(getX() - 0.5, getY() + 1.5, 1.5, regular18.getHeight() - 2.5, 1f, colorValue.getColor());
                    offsetX = -1;
                    break;
                case "Minecraft":
                    RenderUtil.rect(getX() - 0.5, getY() - 0.5, mc.fontRendererObj.getStringWidth(pingString) + 4, mc.fontRendererObj.FONT_HEIGHT + 3, new Color(0,0,0,100));
            }
        }
        if (mode.getMode().equals("Minecraft")) {
            mc.fontRendererObj.drawStringWithShadow(pingString, getX() + 2, getY() + 2, Color.WHITE.getRGB());
        } else {
            FontManager.getSpecialIcon(20).drawString("c", getX() + 6.5 + offsetX, getY() + 5 + offsetY, colorValue.getColor().getRGB());
            regular18.drawString(pingString, getX() + 18 + offsetX, getY() + 3.5f + offsetY, new Color(250, 250, 250, 200).getRGB());
        }
    }
}
