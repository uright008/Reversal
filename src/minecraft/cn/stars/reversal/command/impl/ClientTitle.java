package cn.stars.reversal.command.impl;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.reversal.Branch;
import org.lwjgl.opengl.Display;

@CommandInfo(name = "ClientTitle", description = "Customize the client window title", syntax = ".clienttitle <name/%reset%>", aliases = {"clienttitle", "ct"})
public final class ClientTitle extends Command {

    @Override
    public void onCommand(final String command, final String[] args) {
        if (args[0].equals("%reset%")) {
            Reversal.setWindowTitle();
            Reversal.notificationManager.registerNotification("Successfully reset the client window title.", "Command", NotificationType.SUCCESS);
            Reversal.showMsg("Successfully reset the client window title.");
        } else {
            Display.setTitle(String.join(" ", args));
            Reversal.notificationManager.registerNotification("Successfully set the client window title.", "Command", NotificationType.SUCCESS);
            Reversal.showMsg("Successfully set the client window title.");
        }
    }
}
