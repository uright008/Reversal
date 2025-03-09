package cn.stars.reversal.command.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.ui.notification.NotificationType;
import net.minecraft.client.resources.I18n;

@CommandInfo(name = "SetText", description = "Set the custom text", syntax = ".settext <name>", aliases = "settext")
public final class SetText extends Command {

    @Override
    public void onCommand(final String command, final String[] args) throws Exception {
        Reversal.customText = String.join(" ", args);
        Reversal.notificationManager.registerNotification(I18n.format("command.SetText.success", Reversal.customText), I18n.format("command.title"), NotificationType.SUCCESS);
        Reversal.showMsg(I18n.format("command.SetText.success", Reversal.customText));
    }
}
