package cn.stars.reversal.command.impl;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.reversal.Branch;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.Display;

@CommandInfo(name = "ClientTitle", description = "Customize the client window title", syntax = ".ct <name/%id%>", aliases = {"clienttitle", "ct"})
public final class ClientTitle extends Command {

    @Override
    public void onCommand(final String command, final String[] args) {
        if (args[0].startsWith("%") && args[0].endsWith("%")) {
            String identifier = args[0].substring(1, args[0].length() - 1);
            if (identifier.equals("reset")) {
                Reversal.setWindowTitle();
            } else {
                String storedId = System.getProperty("randomTitle.id");
                System.setProperty("randomTitle.id", identifier);
                Reversal.setWindowTitle();
                System.setProperty("randomTitle.id", storedId.isEmpty() ? "" : storedId);
            }
        } else {
            Display.setTitle(String.join(" ", args));
        }
        Reversal.notificationManager.registerNotification(I18n.format("command.ClientTitle.success", Display.getTitle()), I18n.format("command.title"), NotificationType.SUCCESS);
        Reversal.showMsg(I18n.format("command.ClientTitle.success", Display.getTitle()));
    }
}
