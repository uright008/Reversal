package cn.stars.reversal.command.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.config.ConfigHandler;
import cn.stars.reversal.ui.notification.NotificationType;
import net.minecraft.client.resources.I18n;

@CommandInfo(name = "Config", description = "Modify your configs", syntax = ".config <save/create/load/list/delete> <name>", aliases = {"config", "cfg"})
public final class Config extends Command {

    @Override
    public void onCommand(final String command, final String[] args) {
        Reversal.threadPoolExecutor.execute(() -> {
            switch (args[0].toLowerCase()) {
                case "save": {
                    if (args[1].isEmpty()) {
                        Reversal.showMsg(I18n.format("command.Config.emptyName"));
                        Reversal.notificationManager.registerNotification(I18n.format("command.Config.emptyName"), I18n.format("command.title"), NotificationType.ERROR);
                        return;
                    }
                    ConfigHandler.save(args[1]);
                    break;
                }

                case "create": {
                    if (args[1].isEmpty()) {
                        Reversal.showMsg(I18n.format("command.Config.emptyName"));
                        Reversal.notificationManager.registerNotification(I18n.format("command.Config.emptyName"), I18n.format("command.title"), NotificationType.ERROR);
                        return;
                    }
                    ConfigHandler.create(args[1]);
                    break;
                }

                case "load": {
                    ConfigHandler.load(args[1]);
                    break;
                }

                case "list": {
                    ConfigHandler.list();
                    break;
                }

                case "delete": {
                    ConfigHandler.delete(args[1]);
                    break;
                }

                default: {
                    Reversal.showMsg(".config <save/create/load/list/delete> <name>");
                    Reversal.notificationManager.registerNotification(I18n.format("command.message.invalid", args[0]), I18n.format("command.title"), NotificationType.ERROR);
                }
            }
        });
    }
}
