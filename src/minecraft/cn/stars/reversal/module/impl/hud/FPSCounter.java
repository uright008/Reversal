package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.event.impl.Shader3DEvent;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.util.render.*;
import net.minecraft.client.Minecraft;

import java.awt.*;

@ModuleInfo(name = "FPSCounter", chineseName = "帧率显示", description = "Show your fps on screen",
        chineseDescription = "显示你的帧率", category = Category.HUD)
public class FPSCounter extends Module {
    private final ModeValue mode = new ModeValue("Mode", this, "Simple", "Simple", "Modern", "ThunderHack", "Empathy", "Minecraft");
    private final BoolValue rainbow = new BoolValue("Rainbow", this, false);
    private final BoolValue background = new BoolValue("Background", this, true);
    public FPSCounter() {
        setCanBeEdited(true);
        setWidth(100);
        setHeight(20);
    }
    MFont psm = FontManager.getPSM(18);
    MFont icon = FontManager.getSpecialIcon(20);

    @Override
    public void onShader3D(Shader3DEvent event) {
        String fpsString = Minecraft.getDebugFPS() + " fps";
        if (ModuleInstance.getModule(ClientSettings.class).hudTextWithBracket.enabled) fpsString = "[" + Minecraft.getDebugFPS() + " fps]";

        Color color = rainbow.isEnabled() ? ThemeUtil.getThemeColor(ThemeType.LOGO) : new Color(250, 250, 250, 200);

        if (background.isEnabled()) {
            switch (mode.getMode()) {
                case "Modern":
                    if (event.isBloom())
                        RoundedUtil.drawRound(getX() + 2, getY() - 1, 19 + psm.getWidth(fpsString), psm.getHeight() + 3, 4, ColorUtil.withAlpha(color, 255));
                    else
                        RoundedUtil.drawRound(getX() + 2, getY() - 1, 19 + psm.getWidth(fpsString), psm.getHeight() + 3, 4, Color.BLACK);
                    break;
                case "ThunderHack":
                    RoundedUtil.drawGradientRound(getX() + 0.5f, getY() - 2.5f, 22 + psm.getWidth(fpsString), psm.getHeight() + 6, 4,
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    break;
                case "Simple":
                    RenderUtil.rect(getX() + 2, getY() - 1, 19 + psm.getWidth(fpsString), psm.getHeight() + 3, Color.BLACK);
                    break;
                case "Empathy":
                    RenderUtil.roundedRectangle(getX(), getY() - 1, 21 + psm.getWidth(fpsString), psm.getHeight() + 3, 3f, ColorUtil.empathyGlowColor());
                    RenderUtil.roundedRectangle(getX() - 0.5, getY() + 1.5, 1.5, psm.getHeight() - 2.5, 1f, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));
                    break;
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String fpsString = Minecraft.getDebugFPS() + " fps";
        if (ModuleInstance.getModule(ClientSettings.class).hudTextWithBracket.enabled) fpsString = "[" + Minecraft.getDebugFPS() + " fps]";
        Color color = rainbow.isEnabled() ? ThemeUtil.getThemeColor(ThemeType.LOGO) : new Color(250, 250, 250, 200);

        if (background.isEnabled()) {
            switch (mode.getMode()) {
                case "Modern":
                    RoundedUtil.drawRound(getX() + 2, getY() - 1, 19 + psm.getWidth(fpsString), psm.getHeight() + 3, 4, new Color(0, 0, 0, 80));
                    RenderUtil.roundedOutlineRectangle(getX() + 1, getY() - 2, 21 + psm.getWidth(fpsString), psm.getHeight() + 5, 3, 1, color);
                    break;
                case "ThunderHack":
                    RoundedUtil.drawGradientRound(getX() + 0.5f, getY() - 2.5f, 22 + psm.getWidth(fpsString), psm.getHeight() + 6, 4,
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    RoundedUtil.drawRound(getX() + 1, getY() - 2, 21 + psm.getWidth(fpsString), psm.getHeight() + 5, 4, new Color(0, 0, 0, 220));
                    break;
                case "Simple":
                    RenderUtil.rect(getX() + 2, getY() - 1, 19 + psm.getWidth(fpsString), psm.getHeight() + 3, new Color(0, 0, 0, 80));
                    break;
                case "Empathy":
                    RenderUtil.roundedRectangle(getX(), getY() - 1, 21 + psm.getWidth(fpsString), psm.getHeight() + 3, 3f, ColorUtil.empathyColor());
                    RenderUtil.roundedRectangle(getX() - 0.5, getY() + 1.5, 1.5, psm.getHeight() - 2.5, 1f, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));
                    break;
                case "Minecraft":
                    RenderUtil.rect(getX() - 0.5, getY() - 0.5, mc.fontRendererObj.getStringWidth(fpsString) + 4, mc.fontRendererObj.FONT_HEIGHT + 3, new Color(0,0,0,100));
            }
        }
        if (mode.getMode().equals("Minecraft")) {
            mc.fontRendererObj.drawStringWithShadow(fpsString, getX() + 2, getY() + 2, Color.WHITE.getRGB());
        } else {
            icon.drawString("e", getX() + 4, getY() + 4, new Color(250, 250, 250, 200).getRGB());
            psm.drawString(fpsString, getX() + 17, getY() + 2.5f, new Color(250, 250, 250, 200).getRGB());
        }
    }
}
