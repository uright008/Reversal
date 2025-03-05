package cn.stars.reversal.command.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.misc.ModuleInstance;

@CommandInfo(name = "Toggle", description = "Toggle module", syntax = ".toggle <module> (<on/off>)", aliases = {"toggle", "t"})
public class Toggle extends Command {
    @Override
    public void onCommand(final String command, final String[] args) {
        Module module = ModuleInstance.getModule(args[0]);

        if (args.length == 1) {
            if (module != null) {
                module.toggleModule();
                Reversal.showMsg("Module toggled successfully.");
                return;
            }
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("on")) {
                if (module != null) {
                    module.toggleModule(true);
                    Reversal.showMsg("Module set to enabled successfully.");
                    return;
                }
            } else if (args[1].equalsIgnoreCase("off")) {
                if (module != null) {
                    module.toggleModule(false);
                    Reversal.showMsg("Module set to disabled successfully.");
                    return;
                }
            } else {
                Reversal.showMsg("Invalid state. 'on' or 'off' is required.");
                Reversal.notificationManager.registerNotification("Invalid state. 'on' or 'off' is required.", "Command", NotificationType.ERROR);
                return;
            }
        }

        Reversal.notificationManager.registerNotification("Invalid module.", "Command", NotificationType.ERROR);
        Reversal.showMsg("Invalid module.");
    }
}
