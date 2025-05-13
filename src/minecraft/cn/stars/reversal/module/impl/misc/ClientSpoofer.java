package cn.stars.reversal.module.impl.misc;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.PacketSendEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.value.impl.ModeValue;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

@ModuleInfo(name = "ClientSpoofer", localizedName = "module.ClientSpoofer.name", description = "Makes servers think you're on other clients",
        localizedDescription = "module.ClientSpoofer.desc", category = Category.MISC)
public final class ClientSpoofer extends Module {
    private final ModeValue mode = new ModeValue("Mode", this, "Forge", "Forge", "Lunar", "LabyMod", "PvP Lounge", "CheatBreaker", "Geyser");

    @Override
    protected void onEnable() {
        ScaledResolution sr = new ScaledResolution(mc);
        Reversal.notificationManager.registerNotification("Rejoin for " + this.getModuleInfo().name() + " to work.", NotificationType.NOTIFICATION);
        Reversal.showMsg("W:"+sr.getScaledWidth() + " ,H:"+sr.getScaledHeight());
    }

    @Override
    public void onPacketSend(final PacketSendEvent event) {
        if (event.getPacket() instanceof C17PacketCustomPayload) {
            final C17PacketCustomPayload packet = (C17PacketCustomPayload) event.getPacket();
            switch (mode.getMode()) {
                case "Forge": {
                    packet.setData(createPacketBuffer("FML", true));
                    break;
                }

                case "Lunar": {
                    packet.setChannel("REGISTER");
                    packet.setData(createPacketBuffer("Lunar-Client", false));
                    break;
                }

                case "LabyMod": {
                    packet.setData(createPacketBuffer("LMC", true));
                    break;
                }

                case "PvP Lounge": {
                    packet.setData(createPacketBuffer("PLC18", false));
                    break;
                }

                case "CheatBreaker": {
                    packet.setData(createPacketBuffer("CB", true));
                    break;
                }

                case "Geyser": {
                    packet.setData(createPacketBuffer("Geyser", false));
                    break;
                }
            }
        }
    }

    private PacketBuffer createPacketBuffer(final String data, final boolean string) {
        if (string)
            return new PacketBuffer(Unpooled.buffer()).writeString(data);
        else
            return new PacketBuffer(Unpooled.wrappedBuffer(data.getBytes()));
    }
}
