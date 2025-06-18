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
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

@ModuleInfo(name = "PotionEffect", localizedName = "module.PotionEffect.name", description = "Draw potion effect stats", localizedDescription = "module.PotionEffect.desc",category = Category.HUD)
public class PotionEffect extends Module {
    public final ModeValue mode = new ModeValue("Mode", this, "Minecraft", "Minecraft", "Minecraft 2", "Modern", "Simple", "Empathy", "Shader", "Blue Archive");
    public final ColorValue colorValue = new ColorValue("Color", this);
    public final NumberValue spacing = new NumberValue("Spacing", this, 10, 10, 100, 1);
    public final BoolValue background = new BoolValue("Background", this, true);
    public final BoolValue progress = new BoolValue("Progress", this, true);
    public final BoolValue reverse = new BoolValue("Reverse", this, true);
    public final BoolValue modernFont = new BoolValue("Modern Font", this, false);
    public final BoolValue noAnimation = new BoolValue("No Animation", this, false);

    private final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
    private final net.minecraft.potion.PotionEffect emptyPotionEffect = new net.minecraft.potion.PotionEffect(1, 1, 1);

    @Override
    public void onUpdateAlwaysInGui() {
        progress.hidden = mode.getMode().equals("Minecraft") || mode.getMode().equals("Minecraft 2") || mode.getMode().equals("Blue Archive");
        background.hidden = !mode.getMode().equals("Minecraft");
    }

    public PotionEffect() {
        setX(100);
        setY(100);
        setCanBeEdited(true);
    }

    @Override
    public void onShader3D(Shader3DEvent event) {
        ArrayList<net.minecraft.potion.PotionEffect> potionEffects = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        if (potionEffects.isEmpty() && mc.currentScreen instanceof GuiChat) potionEffects.add(emptyPotionEffect);
        if (reverse.enabled) potionEffects.sort(Comparator.comparingInt(net.minecraft.potion.PotionEffect::getDuration));
        else potionEffects.sort((o1, o2) -> Integer.compare(o2.getDuration(), o1.getDuration()));

        if (!potionEffects.isEmpty()) {
            for (net.minecraft.potion.PotionEffect potionEffect : potionEffects) {
                double renderY = potionEffect.getYAnimation().getValue();
                double renderX = potionEffect.getXAnimation().getValue();

                switch (mode.getMode()) {
                    case "Minecraft":
                        // No need to render anything
                        break;
                    case "Simple":
                        RenderUtil.rect(renderX, renderY, 140, 32, Color.BLACK);
                        break;
                    case "Modern":
                        if (event.isBloom())
                            RenderUtil.roundedRectangle(renderX, renderY, 140, 32, roundStrength, colorValue.getColor());
                        else
                            RenderUtil.roundedRectangle(renderX, renderY, 140, 32, roundStrength, Color.BLACK);
                        break;
                    case "Shader":
                        if (event.isBloom())
                            RenderUtil.rectForShaderTheme(renderX, renderY, 140, 32, colorValue, true);
                        else
                            RenderUtil.roundedRectangle(renderX, renderY, 140, 32, roundStrength, Color.BLACK);
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
        int x = getX();
        int y = getY();
        ArrayList<net.minecraft.potion.PotionEffect> potionEffects = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        if (potionEffects.isEmpty() && mc.currentScreen instanceof GuiChat) potionEffects.add(emptyPotionEffect);
        if (reverse.enabled) potionEffects.sort(Comparator.comparingInt(net.minecraft.potion.PotionEffect::getDuration));
        else potionEffects.sort((o1, o2) -> Integer.compare(o2.getDuration(), o1.getDuration()));

        if (!potionEffects.isEmpty()) {
            double posX = x;
            double posY = y;

            for (net.minecraft.potion.PotionEffect potionEffect : potionEffects) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                potionEffect.getXAnimation().run(posX);
                potionEffect.getYAnimation().run(posY);

                if (noAnimation.enabled) {
                    potionEffect.getXAnimation().finishNow();
                    potionEffect.getYAnimation().finishNow();
                }

                double renderX = potionEffect.getXAnimation().getValue();
                double renderY = potionEffect.getYAnimation().getValue();
                
                Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                updateProgress(potionEffect);
                potionEffect.getProgressAnimation().run(140.0 * potionEffect.getProgress());

                switch (mode.getMode()) {
                    case "Minecraft":
                        if (background.enabled) {
                            mc.getTextureManager().bindTexture(inventoryBackground);
                            Gui.drawTexturedModalRectStatic((int)renderX, (int)renderY, 0, 166, 140, 32);
                        }
                        break;
                    case "Simple":
                        RenderUtil.rect(renderX, renderY, 140, 32, new Color(0, 0, 0, 80));
                        if (progress.enabled) {
                            RenderUtil.rect(renderX, renderY, potionEffect.getProgressAnimation().getValue(), 32, new Color(0, 0, 0, 80));
                        }
                        break;
                    case "Shader":
                        RenderUtil.rectForShaderTheme(renderX, renderY, 140, 32, colorValue, false);
                        if (progress.enabled) {
                            RenderUtil.rectForShaderTheme(renderX, renderY, potionEffect.getProgressAnimation().getValue(), 32, colorValue, false);
                        }
                        break;
                    case "Modern":
                        RenderUtil.roundedOutlineRectangle(renderX - 1, renderY - 1, 142, 34, roundStrength, 1f, colorValue.getColor());
                        RenderUtil.roundedRectangle(renderX, renderY, 140, 32, roundStrength, new Color(0, 0, 0, 80));
                        if (progress.enabled) {
                            RenderUtil.roundedRectangle(renderX, renderY, potionEffect.getProgressAnimation().getValue(), 32, roundStrength, new Color(0, 0, 0, 80));
                        }
                        break;
                    case "Empathy":
                        RenderUtil.roundedRectangle(renderX, renderY, 140, 32, 3f, ColorUtil.empathyColor());
                        if (progress.enabled) {
                            RenderUtil.roundedRectangle(renderX, renderY, potionEffect.getProgressAnimation().getValue(), 32, 3f, new Color(0, 0, 0, 80));
                        }
                        RenderUtil.roundedRectangle(renderX - 0.5, renderY + 11, 1.5, 10, 1f, colorValue.getColor());
                        break;
                }

                if (potion.hasStatusIcon() && !mode.getMode().equals("Minecraft 2") && !mode.getMode().equals("Blue Archive")) {
                    int i1 = potion.getStatusIconIndex();
                    mc.getTextureManager().bindTexture(inventoryBackground);
                    Gui.drawTexturedModalRectStatic((int)renderX + 6, (int)renderY + 7, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }

                String potionName = I18n.format(potion.getName());
                ResourceLocation potionAmplifierIcon = new ResourceLocation("reversal/images/ba/potion/x1.png");

                if (potionEffect.getAmplifier() == 1) {
                    potionName = potionName + " " + I18n.format("enchantment.level.2");
                    potionAmplifierIcon = new ResourceLocation("reversal/images/ba/potion/x2.png");
                } else if (potionEffect.getAmplifier() == 2) {
                    potionName = potionName + " " + I18n.format("enchantment.level.3");
                    potionAmplifierIcon = new ResourceLocation("reversal/images/ba/potion/x3.png");
                } else if (potionEffect.getAmplifier() == 3) {
                    potionName = potionName + " " + I18n.format("enchantment.level.4");
                    potionAmplifierIcon = new ResourceLocation("reversal/images/ba/potion/x4.png");
                }

                if (mode.getMode().equals("Minecraft 2")) {
                    drawString(potionName, (float) renderX, (float) renderY, modernFont.enabled ? colorValue.getColor().getRGB() : Potion.potionTypes[potionEffect.getPotionID()].getLiquidColor());
                    String s = Potion.getDurationString(potionEffect);
                    drawString(s, (float) renderX + 2 + fontWidth(potionName), (float) renderY, Color.GRAY.getRGB());
                    if (reverse.enabled) posY -= spacing.getValue();
                    else posY += spacing.getValue();
                } else if (mode.getMode().equals("Blue Archive")) {
                    RenderUtil.image(potion.getImage(), (float)renderX, (float)renderY, 12, 13);
                    RenderUtil.image(potionAmplifierIcon, (float)renderX + 7, (float)renderY + 7, 8, 7);
                    if (reverse.enabled) posX -= spacing.getValue() + 5;
                    else posX += spacing.getValue() + 5;
                } else {
                    drawString(potionName, (float) (renderX + 10 + 18), (float) (renderY + 7), Color.WHITE.getRGB());
                    String s = Potion.getDurationString(potionEffect);
                    drawString(s, (float) (renderX + 10 + 18), (float) (renderY + 8 + 10), Color.GRAY.getRGB());
                    if (reverse.enabled) posY -= spacing.getValue() + 25;
                    else posY += spacing.getValue() + 25;
                }
            }
        }

        if (mode.getMode().equals("Minecraft 2")) {
            setWidth(150);
            setHeight(spacing.getFloat() * potionEffects.size() + spacing.getFloat());
            if (reverse.enabled) {
                setAdditionalWidth(0);
                setAdditionalHeight(-spacing.getFloat() * (potionEffects.size() - 1));
            } else {
                setAdditionalWidth(0);
                setAdditionalHeight(0);
            }
        } else if (mode.getMode().equals("Blue Archive")) {
            setWidth((spacing.getFloat() + 10) * potionEffects.size() + 10);
            setHeight(30);
            if (reverse.enabled) {
                setAdditionalWidth(-(spacing.getFloat() + 10) * (potionEffects.size() - 1));
                setAdditionalHeight(0);
            } else {
                setAdditionalWidth(0);
                setAdditionalHeight(0);
            }
        } else {
            setWidth(150);
            setHeight((spacing.getFloat() + 25) * potionEffects.size() + 10);
            if (reverse.enabled) {
                setAdditionalWidth(0);
                setAdditionalHeight(-(spacing.getFloat() + 25) * (potionEffects.size() - 1));
            } else {
                setAdditionalWidth(0);
                setAdditionalHeight(0);
            }
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
        if (modernFont.enabled) return regular18.height() - 2;
        else return mc.fontRendererObj.FONT_HEIGHT;
    }

    public float drawString(String string, float x, float y, int color) {
        if (modernFont.enabled) return regular18.drawString(string, x, y, color);
        else return mc.fontRendererObj.drawStringWithShadow(string, (int) x, (int) y, color);
    }

    public float fontWidth(String string) {
        if (modernFont.enabled) return regular18.width(string);
        else return mc.fontRendererObj.getStringWidth(string);
    }
}
