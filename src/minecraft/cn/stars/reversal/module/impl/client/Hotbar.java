package cn.stars.reversal.module.impl.client;

import cn.stars.reversal.event.impl.Shader3DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

@ModuleInfo(name = "Hotbar", localizedName = "module.Hotbar.name", description = "Change game hotbar", localizedDescription = "module.Hotbar.desc", category = Category.CLIENT)
public class Hotbar extends Module {
    public final ModeValue mode = new ModeValue("Mode", this, "Vanilla", "Vanilla", "Modern");
    public final BoolValue modernBars = new BoolValue("Modern Bars", this, false);
    private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    private ScaledResolution sr = new ScaledResolution(mc);
    EntityPlayer entityplayer;
    private final Animation animation = new Animation(Easing.EASE_OUT_EXPO, 500);

    @Override
    public void onUpdateAlways() {
        checkClientModuleState();
    }

    public void renderHotbar(ScaledResolution sr, float partialTicks) {
        if (mc.getRenderViewEntity() instanceof EntityPlayer) {
            this.sr = sr;
            entityplayer = (EntityPlayer) mc.getRenderViewEntity();
            if (mode.getMode().equals("Vanilla")) renderMinecraftTooltip(sr, partialTicks);
            else {
                animation.run(entityplayer.inventory.currentItem * 20);
                renderModernTooltip(sr, partialTicks);
            }
        }
    }

    @Override
    public void onShader3D(Shader3DEvent event) {
        if (mode.getMode().equals("Modern") && mc.getRenderViewEntity() instanceof EntityPlayer) {
            int x = sr.getScaledWidth() / 2;
            RenderUtil.roundedRectangle(x - 91, sr.getScaledHeight() - 22, 182, 25, 2, Color.BLACK);
            RenderUtil.roundedRectangle(x - 91 + animation.getValue(), sr.getScaledHeight() - 22, 22, 22, 3, Color.BLACK);
        }
    }

    public void renderMinecraftTooltip(ScaledResolution sr, float partialTicks) {
        if (entityplayer == null) return;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(widgetsTexPath);
        int x = sr.getScaledWidth() / 2;
        float f = Gui.zLevel;
        Gui.zLevel = -90.0F;
        RenderUtil.drawTexturedModalRect(x - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
        RenderUtil.drawTexturedModalRect(x - 91 - 1 + entityplayer.inventory.currentItem * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
        Gui.zLevel = f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();

        for (int j = 0; j < 9; ++j) {
            int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
            int l = sr.getScaledHeight() - 16 - 3;
            this.renderHotbarItem(j, k, l, partialTicks, entityplayer);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }

    private void renderModernTooltip(ScaledResolution sr, float partialTicks) {
        if (entityplayer == null) return;
        float f = Gui.zLevel;
        Gui.zLevel = -90.0F;
        int x = sr.getScaledWidth() / 2;
        RenderUtil.roundedRectangle(x - 91, sr.getScaledHeight() - 22, 182, 22, 2, new Color(0,0,0,60));
        RenderUtil.roundedRectangle(x - 91 + animation.getValue(), sr.getScaledHeight() - 22, 22, 22, 3, new Color(0,0,0,80));
        Gui.zLevel = f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();

        for (int j = 0; j < 9; ++j) {
            int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
            int l = sr.getScaledHeight() - 16 - 3;
            this.renderHotbarItem(j, k, l, partialTicks, entityplayer);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }

    private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player)
    {
        ItemStack itemstack = player.inventory.mainInventory[index];

        if (itemstack != null)
        {
            float f = (float)itemstack.animationsToGo - partialTicks;

            if (f > 0.0F)
            {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GlStateManager.translate((float)(xPos + 8), (float)(yPos + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(xPos + 8)), (float)(-(yPos + 12)), 0.0F);
            }

            mc.getRenderItem().renderItemAndEffectIntoGUI(itemstack, xPos, yPos);

            if (f > 0.0F)
            {
                GlStateManager.popMatrix();
            }

            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, itemstack, xPos, yPos);
        }
    }
}
