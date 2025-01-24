package cn.stars.reversal.command.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.misc.ModuleInstance;

@CommandInfo(name = "Toggle", description = "Toggle module", syntax = ".toggle <module>", aliases = {"toggle", "t"})
public class Toggle extends Command {
    @Override
    public void onCommand(final String command, final String[] args) {
        Module module = ModuleInstance.getModule(args[0]);
        if (module != null) {
            module.toggleModule();
            return;
        }

        Reversal.notificationManager.registerNotification("Invalid module.", "Command", NotificationType.ERROR);
        Reversal.showMsg("Invalid module.");
    }
}
