package cn.stars.reversal.module.impl.player;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.PacketReceiveEvent;
import cn.stars.reversal.event.impl.PacketSendEvent;
import cn.stars.reversal.event.impl.WorldEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.IChatComponent;

import java.util.Timer;
import java.util.TimerTask;

@ModuleInfo(name = "AutoPlay", localizedName = "module.AutoPlay.name", description = "Auto enter next game", localizedDescription = "module.AutoPlay.desc", category = Category.PLAYER)
public class AutoPlay extends Module {
    public final ModeValue server = new ModeValue("Server", this, "Hypixel", "Hypixel");
    public final NumberValue delay = new NumberValue("Delay", this, 3, 0, 10, 1);

    int clickState = 0;
    boolean queued = false;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (server.getMode().equals("Hypixel") && clickState == 1 && packet instanceof S2DPacketOpenWindow) {
            event.setCancelled(true);
        }
        if (packet instanceof S2FPacketSetSlot) {
            S2FPacketSetSlot p = (S2FPacketSetSlot) packet;
            if (p.func_149174_e() == null) return;
            ItemStack item = p.func_149174_e();
            int windowId = p.func_149175_c();
            int slot = p.func_149173_d();
            String itemName = item.getUnlocalizedName();
            String displayName = item.getDisplayName();

            if (server.getMode().equals("Hypixel")) {
                if (clickState == 0 && windowId == 0 && slot == 43 && itemName.toLowerCase().contains("paper")) {
                    queueAutoPlay(delay.getInt() * 1000L ,() -> {
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(7));
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(item));
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(item));
                    });
                    clickState = 1;
                }
                if (clickState == 1 && windowId != 0 && itemName.equalsIgnoreCase("item.fireworks")) {
                    mc.getNetHandler().addToSendQueue(new C0EPacketClickWindow(windowId, slot, 0, 0, item, (short) 1919));
                    mc.getNetHandler().addToSendQueue(new C0DPacketCloseWindow(windowId));
                }
            }
        } else if (packet instanceof S02PacketChat) {
            S02PacketChat p = (S02PacketChat) packet;
            if (p.getChatComponent() != null) process(p.getChatComponent());
        }
    }

    private void queueAutoPlay(long delay, Runnable runnable) {
        if (queued) {
            return;
        }
        queued = true;
        Reversal.notificationManager.registerNotification("Sending you to the next game in " + delay / 1000 + "s!", "AutoPlay", 3000L, NotificationType.NOTIFICATION, 5);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                {
                    if (AutoPlay.this.enabled && queued) {
                        queued = false;
                        runnable.run();
                    }
                }
            }
        }, delay);
    }

    @Override
    public void onWorld(WorldEvent event) {
        clickState = 0;
        queued = false;
    }

    private void process(IChatComponent component) {
        String value = component.getChatStyle().getChatClickEvent() == null ? null : component.getChatStyle().getChatClickEvent().getValue();
        if (value != null && value.startsWith("/play again")) {
            queueAutoPlay(delay.getInt() * 1000L , () -> mc.thePlayer.sendChatMessage(value));
            component.getSiblings().forEach(this::process);
        }
    }
}
