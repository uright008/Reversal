package cn.stars.reversal.command.impl;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.math.TimeUtil;
import net.minecraft.client.resources.I18n;


@CommandInfo(name = "Online", description = "Display online irc users.", syntax = ".online", aliases = "online")
public final class Online extends Command {
    private final TimeUtil timeUtil = new TimeUtil();

    @Override
    public void onCommand(final String command, final String[] args) {
        if (RainyAPI.ircUser != null && RainyAPI.ircUser.isConnected()) {
            if (timeUtil.hasReached(30000L)) {
                RainyAPI.ircUser.sendMessage("Handshake", RainyAPI.ircUser.id);
            }
            Reversal.showMsg(I18n.format("command.Online.current", RainyAPI.ircUser.onlinePlayers.size()));
            Reversal.showMsg(RainyAPI.ircUser.onlinePlayers.toString());
            timeUtil.reset();
        } else {
            Reversal.notificationManager.registerNotification(I18n.format("command.Chat.notConnected"), I18n.format("command.title"), NotificationType.WARNING);
            Reversal.showMsg(I18n.format("command.Chat.notConnected"));
        }
    }
}
