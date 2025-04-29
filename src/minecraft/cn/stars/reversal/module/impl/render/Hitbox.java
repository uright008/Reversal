package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.optifine.shaders.Shaders;

import java.awt.*;

@ModuleInfo(name = "Hitbox", localizedName = "module.Hitbox.name", description = "Display the bounding boxes of entities", localizedDescription = "module.Hitbox.desc", category = Category.RENDER)
public class Hitbox extends Module {
    public final BoolValue boundingBox = new BoolValue("Bounding Box", this, true);
    public final ColorValue boundingBoxColor = new ColorValue("Bounding Box Color", this, Color.WHITE).defaultThemeColorEnabled(false);
    public final BoolValue eyeBox = new BoolValue("Eye Box", this, true);
    public final ColorValue eyeBoxColor = new ColorValue("Eye Box Color", this, Color.RED).defaultThemeColorEnabled(false);
    public final BoolValue lookIndicator = new BoolValue("Look Indicator", this, true);
    public final ColorValue lookIndicatorColor = new ColorValue("Look Indicator Color", this, Color.BLUE).defaultThemeColorEnabled(false);

    @Override
    public void onUpdateAlways() {
        mc.renderManager.setDebugBoundingBox(this.enabled);
    }

    @Override
    public void onUpdateAlwaysInGui() {
        boundingBoxColor.hidden = !boundingBox.enabled;
        eyeBoxColor.hidden = !eyeBox.enabled;
        lookIndicatorColor.hidden = !lookIndicator.enabled;
    }

    public void renderBoundingBox(Entity entityIn, double x, double y, double z, float partialTicks)
    {
        if (!boundingBox.isEnabled() && !eyeBox.isEnabled() && !lookIndicator.isEnabled()) return;
        if (!Shaders.isShadowPass)
        {
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            if (boundingBox.enabled) {
                AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox();
                AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - entityIn.posX + x, axisalignedbb.minY - entityIn.posY + y, axisalignedbb.minZ - entityIn.posZ + z, axisalignedbb.maxX - entityIn.posX + x, axisalignedbb.maxY - entityIn.posY + y, axisalignedbb.maxZ - entityIn.posZ + z);
                RenderGlobal.drawOutlinedBoundingBox(axisalignedbb1, boundingBoxColor.getColor());
            }

            if (entityIn instanceof EntityLivingBase && eyeBox.enabled)
            {
                float f = entityIn.width / 2.0F;
                RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(x - (double)f, y + (double)entityIn.getEyeHeight() - 0.009999999776482582D, z - (double)f, x + (double)f, y + (double)entityIn.getEyeHeight() + 0.009999999776482582D, z + (double)f), eyeBoxColor.getColor());
            }

            if (lookIndicator.enabled) {
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                Vec3 vec3 = entityIn.getLook(partialTicks);
                worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
                worldrenderer.pos(x, y + (double) entityIn.getEyeHeight(), z).color(lookIndicatorColor.getColor()).endVertex();
                worldrenderer.pos(x + vec3.xCoord * 2.0D, y + (double) entityIn.getEyeHeight() + vec3.yCoord * 2.0D, z + vec3.zCoord * 2.0D).color(lookIndicatorColor.getColor()).endVertex();
                tessellator.draw();
            }
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
        }
    }
}
