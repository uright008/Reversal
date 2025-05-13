package net.minecraft.client.renderer.entity.layers;

import cn.stars.reversal.module.impl.render.Animations;
import cn.stars.reversal.util.misc.ModuleInstance;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldItem implements LayerRenderer<EntityLivingBase>
{
    private final RendererLivingEntity<?> livingEntityRenderer;

    public LayerHeldItem(RendererLivingEntity<?> livingEntityRendererIn)
    {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        ItemStack itemstack = entitylivingbaseIn.getHeldItem();

        if (itemstack != null)
        {
            GlStateManager.pushMatrix();

            if (this.livingEntityRenderer.getMainModel().isChild)
            {
                float f = 0.5F;
                GlStateManager.translate(0.0F, 0.625F, 0.0F);
                GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.scale(f, f, f);
            }

            ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F);
            GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);

            if (entitylivingbaseIn instanceof EntityPlayer && ((EntityPlayer)entitylivingbaseIn).fishEntity != null)
            {
                itemstack = new ItemStack(Items.fishing_rod, 0);
            }

            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();

            if (!ModuleInstance.getModule(Animations.class).swordMode.getMode().equals("None")) {
                AbstractClientPlayer player = null;
                if (entitylivingbaseIn instanceof AbstractClientPlayer) {
                    player = (AbstractClientPlayer) entitylivingbaseIn;
                }
                EnumAction var26;
                if (player != null && player.getItemInUseCount() > 0) {
                    var26 = itemstack.getItemUseAction();
                    if (var26 == EnumAction.BLOCK) {
                        GlStateManager.translate(0.05F, 0.0F, -0.1F);
                        GlStateManager.rotate(-50.0F, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-10.0F, 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate(-60.0F, 0.0F, 0.0F, 1.0F);
                    }
                }
            }

            if (item instanceof ItemBlock && Block.getBlockFromItem(item).getRenderType() == 2)
            {
                GlStateManager.translate(0.0F, 0.1875F, -0.3125F);
                GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                float f1 = 0.375F;
                GlStateManager.scale(-f1, -f1, f1);
            }

            if (entitylivingbaseIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.203125F, 0.0F);
            }

            minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
