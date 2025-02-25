package cn.stars.addons.rfp;

import cn.stars.reversal.util.misc.ModuleInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class RFP {
    private static boolean itemDebug;
    public static float bodyOffset;
    public static boolean customItemOverride;
    private static byte spawnDelay = 1;
    public static EntityPlayerDummy dummy;
    private static String[] overrideItems;

    public RFP() {
    }

    public void init() {
        overrideItems = new String[]{"map", "compass", "clock"};
        itemDebug = false;
        bodyOffset = 0.35f;
    }

    public void onTick() {
        customItemOverride = false;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            if (player.inventory.getCurrentItem() != null) {
                for(String itemName : overrideItems) {
                    if (itemDebug) {
                        System.out.println(player.inventory.getCurrentItem().getItem().getUnlocalizedName());
                    }

                    if (player.inventory.getCurrentItem().getItem().getUnlocalizedName().contains(itemName)) {
                        customItemOverride = true;
                        break;
                    }
                }
            }

            if (dummy == null) {
                if (spawnDelay == 0) {
                    dummy = new EntityPlayerDummy(Minecraft.getMinecraft().theWorld);
                    Minecraft.getMinecraft().theWorld.spawnEntityInWorld(dummy);
                    dummy.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                } else {
                    --spawnDelay;
                }
            } else if (dummy.worldObj.provider.getDimensionId() != player.worldObj.provider.getDimensionId() || dummy.getDistanceToEntity(player) > 5.0F) {
                dummy.setDead();
                dummy = null;
                spawnDelay = 1;
            }
        }
    }

    public void onDisable() {
        dummy.setDead();
        dummy = null;
        spawnDelay = 1;
    }

    public static boolean shouldCancel() {
        return (ModuleInstance.getModule(cn.stars.reversal.module.impl.addons.RealFirstPerson.class).enabled);
    }
}
