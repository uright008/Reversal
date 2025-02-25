package cn.stars.addons.rfp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderPlayerDummy extends Render {
    public RenderPlayerDummy(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float ticks) {
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

            Render<AbstractClientPlayer> render = this.renderManager.getEntityRenderObject(player);
            RenderPlayer playerRenderer = (RenderPlayer) render;
            ModelPlayer playerModel = playerRenderer.getMainModel();
            playerModel.bipedBody.isHidden = false;
            playerModel.bipedLeftArm.isHidden = false;
            ItemStack tempStack = player.inventory.getCurrentItem();
            playerModel.bipedHead.isHidden = true;
            playerModel.bipedHeadwear.isHidden = true;

            ItemStack helmetStack = player.inventory.armorInventory[3];
            player.inventory.armorInventory[3] = null;
            if (player.isSneaking()) {
                playerRenderer.doRender(player, player.posX - entity.posX + x, player.posY - entity.posY + y, player.posZ - entity.posZ + z, player.rotationYaw, ticks);
            } else {
                double renderOffset = player.rotationPitch - (player.rotationPitch - player.rotationYaw);
                playerRenderer.doRender(player, player.posX - entity.posX + x + (double) RFP.bodyOffset * Math.sin(Math.toRadians(renderOffset)), player.posY - entity.posY + y, player.posZ - entity.posZ + z - (double) RFP.bodyOffset * Math.cos(Math.toRadians(renderOffset)), (float) renderOffset, ticks);
            }

            player.inventory.armorInventory[3] = helmetStack;
            playerModel.bipedBody.isHidden = false;
            playerModel.bipedLeftArm.isHidden = false;
            player.inventory.setInventorySlotContents(player.inventory.currentItem, tempStack);
            playerModel.bipedLeftLeg.isHidden = false;
            playerModel.bipedRightLeg.isHidden = false;
            playerModel.bipedHead.isHidden = false;
            playerModel.bipedHeadwear.isHidden = false;
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
