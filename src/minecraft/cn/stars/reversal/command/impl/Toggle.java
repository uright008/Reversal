package cn.stars.reversal.command.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.misc.ModuleInstance;
import net.minecraft.client.resources.I18n;

@CommandInfo(name = "Toggle", description = "Toggle module", syntax = ".toggle <module> (<on/off>)", aliases = {"toggle", "t"})
public class Toggle extends Command {
    @Override
    public void onCommand(final String command, final String[] args) {
        Module module = ModuleInstance.getModule(args[0]);

        if (args.length == 1) {
            if (module != null) {
                module.toggleModule();
                Reversal.showMsg(I18n.format("command.Toggle.success", module.getModuleInfo().name(), module.isEnabled()));
                return;
            }
        } else if (args.length == 2) {
            if (module != null) {
                module.toggleModule(Boolean.parseBoolean(args[1]));
                Reversal.showMsg(I18n.format("command.Toggle.success", module.getModuleInfo().name(), module.isEnabled()));
                return;
            }
        }

        Reversal.notificationManager.registerNotification(I18n.format("command.message.moduleNonExist", args[0]), I18n.format("command.title"), NotificationType.ERROR);
        Reversal.showMsg(I18n.format("command.message.moduleNonExist", args[0]));
    }
}
