package cn.stars.reversal.command.impl;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.Transformer;
import cn.stars.reversal.util.reversal.UserHandshakeThread;
import net.minecraft.client.resources.I18n;

@CommandInfo(name = "Chat", description = "IRC Chat", syntax = ".chat <message>", aliases = "chat")
public final class Chat extends Command {

    @Override
    public void onCommand(final String command, final String[] args) {
        if (args[0] != null) {
            if (RainyAPI.ircUser != null && RainyAPI.ircUser.isConnected()) {
                String message = String.join(" ", args).replace("&", "§");
                Reversal.threadPoolExecutor.submit(() -> RainyAPI.ircUser.sendMessage("Message", Transformer.getIRCTitle(RainyAPI.ircUser.id.toLowerCase()) + "§7[" + RainyAPI.ircUser.id + "] §r" + message));
            } else {
                Reversal.notificationManager.registerNotification(I18n.format("command.Chat.notConnected"), I18n.format("command.title"), NotificationType.WARNING);
                Reversal.showMsg(I18n.format("command.Chat.notConnected"));
            }
        }
    }
}
