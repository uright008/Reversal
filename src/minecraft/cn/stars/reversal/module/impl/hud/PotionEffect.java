package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.event.impl.Shader3DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.ModeValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

@ModuleInfo(name = "PotionEffect", localizedName = "module.PotionEffect.name", description = "Draw potion effect stats", localizedDescription = "module.PotionEffect.desc",category = Category.HUD)
public class PotionEffect extends Module {
    public final ModeValue mode = new ModeValue("Mode", this, "Minecraft", "Minecraft", "Minecraft 2", "Simple", "Empathy");
    public final ColorValue colorValue = new ColorValue("Color", this);
    public final BoolValue background = new BoolValue("Background", this, true);
    public final BoolValue progress = new BoolValue("Progress", this, true);
    public final BoolValue reverse = new BoolValue("Reverse", this, true);
    public final BoolValue modernFont = new BoolValue("Modern Font", this, false);

    private final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
    private final net.minecraft.potion.PotionEffect emptyPotionEffect = new net.minecraft.potion.PotionEffect(1, 1, 1);

    @Override
    public void onUpdateAlwaysInGui() {
        progress.hidden = mode.getMode().equals("Minecraft");
        background.hidden = !mode.getMode().equals("Minecraft");
    }

    public PotionEffect() {
        setX(100);
        setY(100);
        setCanBeEdited(true);
    }

    @Override
    public void onShader3D(Shader3DEvent event) {
        int x = getX() + 1;
        int y = getY() + 1;
        ArrayList<net.minecraft.potion.PotionEffect> potionEffects = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        if (potionEffects.isEmpty() && mc.currentScreen instanceof GuiChat) potionEffects.add(emptyPotionEffect);
        if (reverse.enabled) potionEffects.sort(Comparator.comparingInt(net.minecraft.potion.PotionEffect::getDuration));
        else potionEffects.sort((o1, o2) -> Integer.compare(o2.getDuration(), o1.getDuration()));

        if (!potionEffects.isEmpty()) {
            for (net.minecraft.potion.PotionEffect potionEffect : potionEffects) {
                int renderY = (int) potionEffect.getYAnimation().getValue();
                int renderX = (int) potionEffect.getXAnimation().getValue();

                switch (mode.getMode()) {
                    case "Minecraft":
                        // No need to render anything
                        break;
                    case "Simple":
                        RenderUtil.rect(renderX, renderY, 140, 32, Color.BLACK);
                        break;
                    case "Empathy":
                        RenderUtil.roundedRectangle(renderX, renderY, 140, 32, 3f, ColorUtil.empathyGlowColor());
                        RenderUtil.roundedRectangle(renderX - 0.5, renderY + 11, 1.5, 10, 1f, colorValue.getColor());
                        break;
                }
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int x = getX() + 1;
        int y = getY() + 1;
        ArrayList<net.minecraft.potion.PotionEffect> potionEffects = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        if (potionEffects.isEmpty() && mc.currentScreen instanceof GuiChat) potionEffects.add(emptyPotionEffect);
        if (reverse.enabled) potionEffects.sort(Comparator.comparingInt(net.minecraft.potion.PotionEffect::getDuration));
        else potionEffects.sort((o1, o2) -> Integer.compare(o2.getDuration(), o1.getDuration()));

        if (!potionEffects.isEmpty()) {
            int posY = y;

            for (net.minecraft.potion.PotionEffect potionEffect : potionEffects) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                potionEffect.getXAnimation().run(x);
                potionEffect.getYAnimation().run(posY);
                int renderY = (int) potionEffect.getYAnimation().getValue();
                int renderX = (int) potionEffect.getXAnimation().getValue();
                
                Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                updateProgress(potionEffect);
                potionEffect.getProgressAnimation().run(140.0 * potionEffect.getProgress());

                switch (mode.getMode()) {
                    case "Minecraft":
                        if (background.enabled) {
                            mc.getTextureManager().bindTexture(inventoryBackground);
                            Gui.drawTexturedModalRectStatic(renderX, renderY, 0, 166, 140, 32);
                        }
                        break;
                    case "Simple":
                        RenderUtil.rect(renderX, renderY, 140, 32, new Color(0, 0, 0, 80));
                        if (progress.enabled) {
                            RenderUtil.rect(renderX, renderY, potionEffect.getProgressAnimation().getValue(), 32, new Color(0, 0, 0, 80));
                        }
                        break;
                    case "Empathy":
                        RenderUtil.roundedRectangle(renderX, renderY, 140, 32, 3f, ColorUtil.empathyColor());
                        if (progress.enabled) {
                            RenderUtil.roundedRectangle(renderX, renderY, potionEffect.getProgressAnimation().getValue(), 32, 3f, ColorUtil.empathyColor());
                        }
                        RenderUtil.roundedRectangle(renderX - 0.5, renderY + 11, 1.5, 10, 1f, colorValue.getColor());
                        break;
                }

                if (potion.hasStatusIcon() && !mode.getMode().equals("Minecraft 2")) {
                    int i1 = potion.getStatusIconIndex();
                    mc.getTextureManager().bindTexture(inventoryBackground);
                    Gui.drawTexturedModalRectStatic(renderX + 6, renderY + 7, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }

                String s1 = I18n.format(potion.getName());

                if (potionEffect.getAmplifier() == 1) {
                    s1 = s1 + " " + I18n.format("enchantment.level.2");
                } else if (potionEffect.getAmplifier() == 2) {
                    s1 = s1 + " " + I18n.format("enchantment.level.3");
                } else if (potionEffect.getAmplifier() == 3) {
                    s1 = s1 + " " + I18n.format("enchantment.level.4");
                }

                if (mode.getMode().equals("Minecraft 2")) {
                    drawString(s1, (float) renderX, (float) renderY, modernFont.enabled ? colorValue.getColor().getRGB() : Potion.potionTypes[potionEffect.getPotionID()].getLiquidColor());
                    String s = Potion.getDurationString(potionEffect);
                    drawString(s, renderX + 2 + fontWidth(s1), (float) renderY, Color.GRAY.getRGB());
                    if (reverse.enabled) posY -= 10;
                    else posY += 10;
                } else {
                    drawString(s1, (float) (renderX + 10 + 18), (float) (renderY + 7), Color.WHITE.getRGB());
                    String s = Potion.getDurationString(potionEffect);
                    drawString(s, (float) (renderX + 10 + 18), (float) (renderY + 8 + 10), Color.GRAY.getRGB());
                    if (reverse.enabled) posY -= 35;
                    else posY += 35;
                }
            }
        }

        if (mode.getMode().equals("Minecraft 2")) {
            setWidth(150);
            setHeight(10 * potionEffects.size() + 10);
            if (reverse.enabled) setAdditionalHeight(-10 * (potionEffects.size() - 1));
        } else {
            setWidth(150);
            setHeight(35 * potionEffects.size() + 10);
            if (reverse.enabled) setAdditionalHeight(-35 * (potionEffects.size() - 1));
        }
    }

    private void updateProgress(net.minecraft.potion.PotionEffect potionEffect) {
        if ((double) potionEffect.getDuration() / potionEffect.getInitialDuration() > 0.99) {
            potionEffect.setProgress(1);
        }
        if (potionEffect.getProgressTimer().hasReached(1000)) {
            potionEffect.setProgress((double) potionEffect.getDuration() / potionEffect.getInitialDuration());
            potionEffect.getProgressTimer().reset();
        }
    }

    public float fontHeight() {
        if (modernFont.enabled) return psm18.height() - 2;
        else return mc.fontRendererObj.FONT_HEIGHT;
    }

    public float drawString(String string, float x, float y, int color) {
        if (modernFont.enabled) return psm18.drawString(string, x, y, color);
        else return mc.fontRendererObj.drawStringWithShadow(string, (int) x, (int) y, color);
    }

    public float fontWidth(String string) {
        if (modernFont.enabled) return psm18.width(string);
        else return mc.fontRendererObj.getStringWidth(string);
    }
}
