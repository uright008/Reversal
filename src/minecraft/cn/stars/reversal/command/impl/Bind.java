package cn.stars.reversal.command.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.misc.ModuleInstance;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

@CommandInfo(name = "Bind", description = "Binds the given module to the given key", syntax = ".bind <module> <key>", aliases = "bind")
public final class Bind extends Command {

    @Override
    public void onCommand(final String command, final String[] args) {
        Module module = ModuleInstance.getModule(args[0]);

        if (module != null) {
            if (args.length > 1) {
                args[1] = args[1].toUpperCase();
                final int key = Keyboard.getKeyIndex(args[1]);
                module.setKeyBind(key);

                Reversal.notificationManager.registerNotification(I18n.format("command.Bind.success", module.getModuleInfo().name(), Keyboard.getKeyName(key)), I18n.format("command.title"), NotificationType.SUCCESS);
                Reversal.showMsg(I18n.format("command.Bind.success", module.getModuleInfo().name(), Keyboard.getKeyName(key)));
            } else {
                Reversal.notificationManager.registerNotification(I18n.format("command.Bind.invalidArgs"), "Command", NotificationType.ERROR);
                Reversal.showMsg(I18n.format("command.Bind.invalidArgs"));
            }
            return;
        }

//        for (final Script script : Rise.INSTANCE.getScriptManager().getScripts()) {
//            if (args[0].equalsIgnoreCase(script.getName())) {
//                args[1] = args[1].toUpperCase();
//                final int key = Keyboard.getKeyIndex(args[1]);
//
//                script.setKey(key);
//
//                Rise.INSTANCE.getNotificationManager().registerNotification("Set " + script.getName() + "'s bind to " + Keyboard.getKeyName(key) + ".");
//                return;
//            }
//        }

        Reversal.notificationManager.registerNotification(I18n.format("command.message.moduleNonExist", args[0]), "Command", NotificationType.ERROR);
        Reversal.showMsg(I18n.format("command.message.moduleNonExist", args[0]));
    }
}
