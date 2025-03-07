/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025Aerolite Society, All rights reserved.
 */
package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.Render3DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.NumberValue;
import cn.stars.reversal.util.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

@ModuleInfo(name = "ReachDisplay", localizedName = "攻击距离", description = "Display your reach distance", localizedDescription = "显示你的攻击距离", category = Category.RENDER)
public class ReachDisplay extends Module {
    public final ColorValue colorValue = new ColorValue("Color", this);
    private final NumberValue thickness = new NumberValue("Thickness", this, 2f, 0.1f, 5f, 0.1f);
    @Override
    public void onRender3D(Render3DEvent event) {
        GL11.glPushMatrix();
        GL11.glTranslated(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY - 0.1,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
        );
        GlStateManager.enableBlend();
        GlStateManager.enableLineSmooth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glLineWidth((float) thickness.getValue());
        GL11.glRotatef(90F, 1F, 0F, 0F);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 0; i <= 360; i += 5) { // You can change circle accuracy (60 - accuracy)
            RenderUtils.color(colorValue.getColor(i / 5));
            float x = (float) (cos(i * Math.PI / 180.0) * 3);
            float y = (float) (sin(i * Math.PI / 180.0) * 3);
            GL11.glVertex2f(x, y);
        }

        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableLineSmooth();

        GL11.glPopMatrix();
    }
}
