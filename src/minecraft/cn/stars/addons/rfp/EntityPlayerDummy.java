package cn.stars.addons.rfp;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityPlayerDummy extends Entity {
    public EntityPlayerDummy(REN this$0, World world) {
        super(world);
        this.noClip = true;
        this.setSize(0.0F, 2.0F);
    }

    @Override
    protected void entityInit() {

    }

    @Override
    public void onUpdate() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        REN.dummy.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }
}
