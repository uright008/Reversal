package cn.stars.reversal.command;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.value.Value;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.misc.ClassUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

public final class CommandManager {

    public static Command[] commandList = new Command[0];

    public void callCommand(final String input) {
        final String[] spit = input.split(" ");
        final String command = spit[0];
        final String args = input.substring(command.length()).trim();

        for (final Command c : commandList) {
            for (final String alias : c.getCommandInfo().aliases()) {
                if (alias.equalsIgnoreCase(command)) {
                    try {
                        c.onCommand(args, args.split(" "));
                    } catch (final Exception e) {
                        e.printStackTrace();
                        Reversal.notificationManager.registerNotification(
                                "Invalid command usage \"" + c.getCommandInfo().syntax() + "\"."
                                , "Command", NotificationType.ERROR);
                        Reversal.showMsg("Invalid command usage \"" + c.getCommandInfo().syntax() + "\".");
                    }

                    return;
                }
            }
        }

        for (final Module module : Reversal.moduleManager.getModuleList()) {
            if (module.getModuleInfo().name().equalsIgnoreCase(command)) {
                if (spit.length > 1) {

                    if (module.getSettingAlternative(spit[1]) != null) {
                        final Value setting = module.getSettingAlternative(spit[1]);

                        try {
                            try {
                                if (setting instanceof BoolValue) {
                                    ((BoolValue) setting).setEnabled(Boolean.parseBoolean(spit[2]));
                                } else if (setting instanceof NumberValue) {
                                    ((NumberValue) setting).setValue(Double.parseDouble(spit[2]));
                                } else if (setting instanceof ModeValue) {
                                    ((ModeValue) setting).set(spit[2]);
                                }

                            } catch (final NumberFormatException ignored) {
                                Reversal.notificationManager.registerNotification("Settings name error. Dont type space!", "Command", NotificationType.ERROR);
                                Reversal.showMsg("Settings name error. Dont type space!");
                                return;
                            }
                        } catch (final ArrayIndexOutOfBoundsException ignored) {
                            Reversal.notificationManager.registerNotification("Settings name error. Dont type space!", "Command", NotificationType.ERROR);
                            Reversal.showMsg("Settings name error. Dont type space!");
                        }

                        return;
                    }

                    Reversal.notificationManager.registerNotification("Settings " + spit[1].toLowerCase() + " in " + command.toLowerCase() + " doesn't exist!", "Command", NotificationType.ERROR);
                    Reversal.showMsg("Settings " + spit[1].toLowerCase() + " in " + command.toLowerCase() + " doesn't exist!");
                    return;
                }
            }
        }

        Reversal.notificationManager.registerNotification("Module or command " + command.toLowerCase() + " doesn't exist.", "Command", NotificationType.ERROR);
        Reversal.showMsg("Module or command " + command.toLowerCase() + " doesn't exist.");
    }

    public void registerCommands() {
        for (Command command : ClassUtil.instantiateList(ClassUtil.resolvePackage(this.getClass().getPackage().getName() + ".impl", Command.class))) {
            commandList = Arrays.stream(Stream.concat(Arrays.stream(commandList), Stream.of(command))
                    .toArray(Command[]::new)).sorted(Comparator.comparing(c -> c.getCommandInfo().name())).toArray(Command[]::new);
        }
    }

    public void registerCommands(Command[] commands) {
        commandList = commands;
    }
}
