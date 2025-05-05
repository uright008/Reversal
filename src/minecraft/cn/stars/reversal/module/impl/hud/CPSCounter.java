package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.event.impl.Shader3DEvent;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.util.render.*;
import net.minecraft.client.gui.GuiChat;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "CPSCounter", localizedName = "module.CPSCounter.name", description = "Show your CPS on screen",
        localizedDescription = "module.CPSCounter.desc", category = Category.HUD)
public class CPSCounter extends Module {
    private final ModeValue mode = new ModeValue("Mode", this, "Simple", "Simple", "Modern", "ThunderHack", "Empathy", "Minecraft", "Shader");
    public final ColorValue colorValue = new ColorValue("Color", this);
    private final BoolValue displayOnClick = new BoolValue("Display On Click", this, false);
    private final BoolValue background = new BoolValue("Background", this, true);
    public CPSCounter() {
        setCanBeEdited(true);
        setWidth(100);
        setHeight(20);
        this.Lclicks = new ArrayList<>();
        this.Rclicks = new ArrayList<>();
    }
    public final List<Long> Lclicks;
    public final List<Long> Rclicks;

    @Override
    public void onShader3D(Shader3DEvent event) {
        if (displayOnClick.isEnabled() && (Lclicks.isEmpty() && Rclicks.isEmpty()) && !(mc.currentScreen instanceof GuiChat)) return;
        String cpsString = Lclicks.size() + " CPS | " + Rclicks.size() + " CPS";
        if (ModuleInstance.getModule(ClientSettings.class).hudTextWithBracket.enabled) cpsString = "[" + Lclicks.size() + " CPS | " + Rclicks.size() + " CPS]";

        if (background.isEnabled()) {
            switch (mode.getMode()) {
                case "Modern":
                    if (event.isBloom())
                        RoundedUtil.drawRound(getX() + 2, getY() - 1, 19 + psm18.getWidth(cpsString), psm18.getHeight() + 3, 4, colorValue.getColor());
                    else
                        RoundedUtil.drawRound(getX() + 2, getY() - 1, 19 + psm18.getWidth(cpsString), psm18.getHeight() + 3, 4, Color.BLACK);
                    break;
                case "ThunderHack":
                    RoundedUtil.drawGradientRound(getX() + 0.5f, getY() - 2.5f, 22 + psm18.getWidth(cpsString), psm18.getHeight() + 6, 4,
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    break;
                case "Simple":
                    RenderUtil.rect(getX() + 2, getY() - 1, 19 + psm18.getWidth(cpsString), psm18.getHeight() + 3, Color.BLACK);
                    break;
                case "Shader":
                    if (event.isBloom())
                        RenderUtil.rectForShaderTheme(getX() + 2, getY() - 1, 21 + regular18.getWidth(cpsString), regular18.getHeight() + 1.5, colorValue, true);
                    else
                        RenderUtil.roundedRectangle(getX() + 2, getY() - 1, 21 + regular18.getWidth(cpsString), regular18.getHeight() + 1.5, ModuleInstance.getClientSettings().roundStrength.getFloat(), Color.BLACK);
                    break;
                case "Empathy":
                    RenderUtil.roundedRectangle(getX(), getY() - 1, 21 + psm18.getWidth(cpsString), psm18.getHeight() + 3, 3f, ColorUtil.empathyGlowColor());
                    RenderUtil.roundedRectangle(getX() - 0.5, getY() + 1.5, 1.5, psm18.getHeight() - 2.5, 1f, colorValue.getColor());
                    break;
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (displayOnClick.isEnabled() && (Lclicks.isEmpty() && Rclicks.isEmpty())) return;
        String cpsString = Lclicks.size() + " CPS | " + Rclicks.size() + " CPS";
        if (ModuleInstance.getModule(ClientSettings.class).hudTextWithBracket.enabled) cpsString = "[" + Lclicks.size() + " CPS | " + Rclicks.size() + " CPS]";

        if (background.isEnabled()) {
            switch (mode.getMode()) {
                case "Modern":
                    RoundedUtil.drawRound(getX() + 2, getY() - 1, 19 + psm18.getWidth(cpsString), psm18.getHeight() + 3, 4, new Color(0, 0, 0, 80));
                    RenderUtil.roundedOutlineRectangle(getX() + 1, getY() - 2, 21 + psm18.getWidth(cpsString), psm18.getHeight() + 5, 3, 1, colorValue.getColor());
                    break;
                case "ThunderHack":
                    RoundedUtil.drawGradientRound(getX() + 0.5f, getY() - 2.5f, 22 + psm18.getWidth(cpsString), psm18.getHeight() + 6, 4,
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    RoundedUtil.drawRound(getX() + 1, getY() - 2, 21 + psm18.getWidth(cpsString), psm18.getHeight() + 5, 4, new Color(0, 0, 0, 220));
                    break;
                case "Simple":
                    RenderUtil.rect(getX() + 2, getY() - 1, 19 + psm18.getWidth(cpsString), psm18.getHeight() + 3, new Color(0, 0, 0, 80));
                    break;
                case "Shader":
                    RenderUtil.rectForShaderTheme(getX() + 2, getY() - 1, 21 + regular18.getWidth(cpsString), regular18.getHeight() + 1.5, colorValue, false);
                    break;
                case "Empathy":
                    RenderUtil.roundedRectangle(getX(), getY() - 1, 21 + psm18.getWidth(cpsString), psm18.getHeight() + 3, 3f, ColorUtil.empathyColor());
                    RenderUtil.roundedRectangle(getX() - 0.5, getY() + 1.5, 1.5, psm18.getHeight() - 2.5, 1f, colorValue.getColor());
                    break;
                case "Minecraft":
                    RenderUtil.rect(getX() - 0.5, getY() - 0.5, mc.fontRendererObj.getStringWidth(cpsString) + 4, mc.fontRendererObj.FONT_HEIGHT + 3, new Color(0,0,0,100));
            }
        }
        if (mode.getMode().equals("Minecraft")) {
            mc.fontRendererObj.drawStringWithShadow(cpsString, getX() + 2, getY() + 2, Color.WHITE.getRGB());
        } else if (mode.getMode().equals("Shader")) {
            FontManager.getIcon(20).drawString("P", getX() + 5.5, getY() + 3.5, colorValue.getColor().getRGB());
            regular18.drawString(cpsString, getX() + 18, getY() + 2.5f, new Color(250, 250, 250, 200).getRGB());
        } else {
            FontManager.getIcon(24).drawString("P", getX() + 3.5, getY() + 2, new Color(250, 250, 250, 200).getRGB());
            psm18.drawString(cpsString, getX() + 17, getY() + 3f, new Color(250, 250, 250, 200).getRGB());
        }
    }
}
