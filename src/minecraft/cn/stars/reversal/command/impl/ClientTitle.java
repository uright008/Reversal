package cn.stars.reversal.command.impl;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.reversal.Branch;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.Display;

@CommandInfo(name = "ClientTitle", description = "Customize the client window title", syntax = ".clienttitle <name/%reset%>", aliases = {"clienttitle", "ct"})
public final class ClientTitle extends Command {

    @Override
    public void onCommand(final String command, final String[] args) {
        if (args[0].equals("%reset%")) {
            Reversal.setWindowTitle();
            Reversal.notificationManager.registerNotification(I18n.format("command.ClientTitle.success", Display.getTitle()), I18n.format("command.title"), NotificationType.SUCCESS);
            Reversal.showMsg(I18n.format("command.ClientTitle.success", Display.getTitle()));
        } else {
            Display.setTitle(String.join(" ", args));
            Reversal.notificationManager.registerNotification(I18n.format("command.ClientTitle.success", Display.getTitle()), I18n.format("command.title"), NotificationType.SUCCESS);
            Reversal.showMsg(I18n.format("command.ClientTitle.success", Display.getTitle()));
        }
    }
}
