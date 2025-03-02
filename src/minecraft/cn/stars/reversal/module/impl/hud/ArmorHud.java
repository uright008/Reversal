package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.value.impl.BoolValue;
import lombok.var;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

@ModuleInfo(name = "ArmorHud", chineseName = "装备显示", description = "Show your armors",
        chineseDescription = "显示你的装备", category = Category.HUD)
public class ArmorHud extends Module {
    //armor的背景开关
    public final BoolValue border = new BoolValue("Border", this, true);

    public ArmorHud() {
        setCanBeEdited(true);
        setWidth(80);
        setHeight(20);
        setX(100);
        setY(100);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (border.enabled) {
            //背景blur
            if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                MODERN_BLUR_RUNNABLES.add(() -> {
                    RenderUtil.roundedRectangle(getX() - 5, getY() - 2, getWidth() + 4, getHeight(), 5, Color.BLACK);
                });

            }
            //背景shadow
            if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                MODERN_BLOOM_RUNNABLES.add(() -> {
                    RenderUtil.roundedRectangle(getX() - 5, getY() - 2, getWidth() + 4, getHeight(), 5, Color.BLACK);
                    RenderUtil.roundedRectangle(getX() - 5, getY() - 2, getWidth() + 4, getHeight(), 5, Color.BLACK);
                });
            }
            //背景最底层
            RenderUtil.roundedRectangle(getX() - 5, getY() - 2, getWidth() + 4, getHeight(), 5, new Color(0, 0, 0, 100));
        }

        //当玩家手上没东西的时候画一个字符串
        if (mc.thePlayer.getCurrentEquippedItem() == null) {
            mc.fontRendererObj.drawStringWithShadow("空", getX() + 64, getY() + 4, new Color(255, 255, 255).getRGB());
        }
        drawArmor();
    }

    private void drawArmor() {
        GL11.glPushMatrix();
        var stuff = new ArrayList<ItemStack>();
        var split = 0;
        for (int index = 3; index >= 0; --index) {
            var armer = mc.thePlayer.inventory.armorInventory[index];
            if (armer == null) continue;
            stuff.add(armer);
        }
        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            stuff.add(mc.thePlayer.getCurrentEquippedItem());
        }
        for (ItemStack everything : stuff) {
            if (mc.theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                split += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            mc.getRenderItem().zLevel = -150.0f;

            mc.getRenderItem().renderItemIntoGUI(everything, getX() + split - 20, getY());
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, everything, getX() + split - 20, getY());

            RenderUtil.renderEnchantText(everything, getX() + split - 20, getY());

            mc.getRenderItem().zLevel = 0.0f;
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            everything.getEnchantmentTagList();
        }

        GL11.glPopMatrix();
    }
}
