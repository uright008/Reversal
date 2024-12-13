package cn.stars.reversal.module.impl.player;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.PacketReceiveEvent;
import cn.stars.reversal.event.impl.UpdateEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.MiscUtil;
import cn.stars.reversal.util.math.TimeUtil;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(name = "AutoGG", chineseName = "自动GG", description = "Auto send gg when game end", chineseDescription = "在游戏结束时自动发送gg", category = Category.PLAYER)
public class AutoGG extends Module {
    String[] winMessage = new String[] {"Winner", "第一名", "1st", "胜利"};
    String[] exceptMessage = new String[] {"任务", "初尝"};
    TimeUtil timeUtil = new TimeUtil();
    private boolean isWinning = false;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.packet instanceof S02PacketChat) {
            String message = ((S02PacketChat) event.packet).getChatComponent().getUnformattedText();
            if (MiscUtil.containsAnyIgnoreCase(message, winMessage) && !MiscUtil.containsAnyIgnoreCase(message, exceptMessage) && !isWinning) {
                isWinning = true;
                mc.thePlayer.sendChatMessage("gg");
                Reversal.notificationManager.registerNotification("Sent GG at chat!", "AutoGG", 3000L, NotificationType.SUCCESS);
            }
        }
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (timeUtil.hasReached(5000L) && isWinning) {
            isWinning = false;
            timeUtil.reset();
        }
    }
}
