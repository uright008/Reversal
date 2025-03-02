/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2024 Starlight, All rights reserved.
 */
package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.Render3DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.module.impl.player.SmallPlayer;
import cn.stars.reversal.util.animation.advanced.composed.CustomAnimation;
import cn.stars.reversal.util.animation.advanced.impl.SmoothStepAnimation;
import cn.stars.reversal.util.render.*;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.util.misc.ModuleInstance;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * SKID?!
 */
@ModuleInfo(name = "BAHalo",  chineseName = "蔚蓝档案光环", description = "Blue archive halos", chineseDescription = "蔚蓝档案角色的光环", category = Category.RENDER)
public class BAHalo extends Module {
    private final ModeValue mode = new ModeValue("Student", this, "Shiroko", "Shiroko", "Hoshino", "Reisa", "Azusa");
    private final BoolValue showInFirstPerson = new BoolValue("First Person", this, false);
//    private final BoolValue lighting = new BoolValue("Lighting", this, false);
    CustomAnimation animation = new CustomAnimation(SmoothStepAnimation.class, 2000, 0.0, 0.1);
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldRenderer = tessellator.getWorldRenderer();

    public final ResourceLocation HOSHINO = new ResourceLocation("reversal/images/ba/halo/hoshino.png");
    public final ResourceLocation SHIROKO = new ResourceLocation("reversal/images/ba/halo/shiroko.png");
    public final ResourceLocation REISA = new ResourceLocation("reversal/images/ba/halo/reisa.png");
    public final ResourceLocation AZUSA = new ResourceLocation("reversal/images/ba/halo/azusa.png");

    @Override
    public void onRender3D(Render3DEvent event) {
        if (mc.gameSettings.thirdPersonView == 0 && !showInFirstPerson.isEnabled()) return;
        if (mc.gameSettings.thirdPersonView != 0 && ModuleInstance.getModule(SmallPlayer.class).isEnabled()) {
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
        }
        Vec3 vec = getVec3().add(new Vec3(0.0D, mc.thePlayer.height + 0.25, 0.0D));
        switch (mode.getMode()) {
            case "Shiroko": {
                drawModernHalo(SHIROKO, vec, 0.9f);
                break;
            }
            case "Hoshino": {
                drawModernHalo(HOSHINO, vec, 1.1f);
                break;
            }
            case "Reisa": {
                drawModernHalo(REISA, vec, 1.0f);
                break;
            }
            case "Azusa": {
                drawModernHalo(AZUSA, vec.add(new Vec3(1.0, 0.6, 0.4)), 1.0f, () -> {
                    GL11.glRotatef(-180, 0F, 0F, 1F);
                });
                break;
            }
        }
    }

    private void drawModernHalo(ResourceLocation rs, Vec3 pos, float radius) {
        // 浮动动画
    /*    animation.run(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true; */
        // 聪明人已经在用 animation.run(isReversing ? 0 : 0.1); 了
        if (animation.getAnimation().finished(animation.getDirection())) animation.changeDirection();

        mc.getTextureManager().bindTexture(rs);
        mc.getTextureManager().getTexture(rs).setBlurMipmap(true, true);

        GlStateManager.pushMatrix();

        // 移动至玩家头顶,跟随视角旋转
        GL11.glRotatef(-mc.thePlayer.rotationYaw, 0F, 1F, 0F);
        GlStateManager.translate(pos.xCoord - radius / 2d, pos.yCoord + animation.getOutput(), pos.zCoord - radius / 2d);
        GL11.glRotatef(90, 1F, 0F, 0F);

        // 确保颜色和显示正常
        GlStateManager.enableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlUtils.startAntiAlias();

        drawBoundTexture(radius);

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();

        GlStateManager.popMatrix();
    }

    private void drawModernHalo(ResourceLocation rs, Vec3 pos, float radius, Runnable directionParser) {
        // 浮动动画
    /*    animation.run(isReversing ? 0 : 0.1);

        if (animation.getValue() == 0) isReversing = false;
        if (animation.getValue() == 0.1) isReversing = true; */
        if (animation.getAnimation().finished(animation.getDirection())) animation.changeDirection();

        mc.getTextureManager().bindTexture(rs);
        mc.getTextureManager().getTexture(rs).setBlurMipmap(true, true);

        GlStateManager.pushMatrix();

        // 移动至玩家头顶,跟随视角旋转
        GL11.glRotatef(-mc.thePlayer.rotationYaw, 0F, 1F, 0F);
        GlStateManager.translate(pos.xCoord - radius / 2d, pos.yCoord + animation.getOutput(), pos.zCoord - radius / 2d);
        // 自定义方向
        directionParser.run();

        // 确保颜色和显示正常
        GlStateManager.enableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlUtils.startAntiAlias();

        drawBoundTexture(radius);
    //    if (lighting.isEnabled()) drawLightTexture(radius);

        GlUtils.stopAntiAlias();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();

        GlStateManager.popMatrix();
    }

    private void drawBoundTexture(float radius) {
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(0, 0, 0).tex(0, 0).endVertex();
        worldRenderer.pos(0, radius, 0).tex(0, 1).endVertex();
        worldRenderer.pos(radius, radius, 0).tex(1, 1).endVertex();
        worldRenderer.pos(radius, 0, 0).tex(1, 0).endVertex();
        tessellator.draw();
    }

    private void drawLightTexture(float radius) {
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(radius * -0.02, radius * -0.02, 0).tex(0, 0).color(ColorUtil.swapAlpha(Color.WHITE.getRGB(), 20 + (float)(animation.getOutput() * 2000))).endVertex();
        worldRenderer.pos(radius * -0.02, radius * 1.02, 0).tex(0, 1).color(ColorUtil.swapAlpha(Color.WHITE.getRGB(), 20 + (float) (animation.getOutput() * 2000))).endVertex();
        worldRenderer.pos(radius * 1.02, radius * 1.02, 0).tex(1, 1).color(ColorUtil.swapAlpha(Color.WHITE.getRGB(), 20 + (float) (animation.getOutput() * 2000))).endVertex();
        worldRenderer.pos(radius * 1.02, radius * -0.02, 0).tex(1, 0).color(ColorUtil.swapAlpha(Color.WHITE.getRGB(), 20 + (float) (animation.getOutput() * 2000))).endVertex();
        tessellator.draw();
    }

    private static Vec3 getVec3() {
        return new Vec3(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
        );
    }
}
