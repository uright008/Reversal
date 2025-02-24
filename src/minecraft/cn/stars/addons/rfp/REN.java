package cn.stars.addons.rfp;

import cn.stars.reversal.module.impl.addons.RFP;
import cn.stars.reversal.util.misc.ModuleInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

public class REN {
    private static boolean itemDebug;
    public static float bodyOffset;
    public static int[] modes;
    public static REN instance = new REN();
    public static boolean customItemOverride;
    public static boolean wasF1DownLastTick;
    public static byte currentMode = 0;
    private static byte spawnDelay = 100;
    public static EntityPlayerDummy dummy;
    private static String[] overrideItems;

    public REN() {
    }

    public void init() {
        overrideItems = new String[]{"map", "compass", "clock"};
        itemDebug = false;
        bodyOffset = 0.35f;
        modes = new int[]{111, 110, 011, 000, 001};
    }

    public void onTick() {
            if (Keyboard.isKeyDown(59)) {
                if (!wasF1DownLastTick) {
                    if (currentMode == modes.length - 1) {
                        currentMode = 0;
                    } else {
                        ++currentMode;
                    }
                }

                wasF1DownLastTick = true;
            } else {
                wasF1DownLastTick = false;
            }

            Minecraft.getMinecraft().gameSettings.hideGUI = modes[currentMode] % 10 != 1;

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
                    dummy = new EntityPlayerDummy(this, Minecraft.getMinecraft().theWorld);
                    Minecraft.getMinecraft().theWorld.spawnEntityInWorld(dummy);
                //    dummy.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                } else {
                    --spawnDelay;
                }
            } else if (dummy.worldObj.provider.getDimensionId() != player.worldObj.provider.getDimensionId() || dummy.getDistanceToEntity(player) > 5.0F) {
                dummy.setDead();
                dummy = null;
                spawnDelay = 100;
            }
        }

    }

    public static boolean shouldCancel() {
        return (ModuleInstance.getModule(RFP.class).enabled && modes[currentMode] / 100 == 1 && !customItemOverride);
    }
}
