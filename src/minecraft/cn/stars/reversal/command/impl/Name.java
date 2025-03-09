package cn.stars.reversal.command.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.ui.notification.NotificationType;
import net.minecraft.client.resources.I18n;

@CommandInfo(name = "Name", description = "Customize the player name", syntax = ".name <name>", aliases = {"name", "cn"})
public final class Name extends Command {

    @Override
    public void onCommand(final String command, final String[] args) throws Exception {
        Reversal.customName = String.join(" ", args);
        Reversal.notificationManager.registerNotification(I18n.format("command.Name.success", Reversal.customName), I18n.format("command.title"), NotificationType.SUCCESS);
        Reversal.showMsg(I18n.format("command.Name.success", Reversal.customName));
    }
}
