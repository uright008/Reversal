package cn.stars.reversal.command;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.misc.ClassUtil;
import cn.stars.reversal.value.Value;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.client.resources.I18n;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

public final class CommandManager {

    public static Command[] commandList = new Command[0];

    public void callCommand(final String input) {
        final String[] split = input.split(" ");
        final String command = split[0];
        final String args = input.substring(command.length()).trim();

        for (final Command c : commandList) {
            for (final String alias : c.getCommandInfo().aliases()) {
                if (alias.equalsIgnoreCase(command)) {
                    try {
                        c.onCommand(args, args.split(" "));
                    } catch (final Exception e) {
                        Reversal.notificationManager.registerNotification(I18n.format("command.message.invalid", input), I18n.format("command.title"), NotificationType.ERROR);
                        Reversal.showMsg(I18n.format("command.message.invalid", input));
                    }
                    return;
                }
            }
        }

        for (final Module module : Reversal.moduleManager.getModuleList()) {
            if (module.getModuleInfo().name().equalsIgnoreCase(command)) {
                if (split.length > 1) {

                    if (module.getSettingAlternative(split[1]) != null) {
                        final Value value = module.getSettingAlternative(split[1]);

                        try {
                            try {
                                if (value instanceof BoolValue) {
                                    ((BoolValue) value).setEnabled(Boolean.parseBoolean(split[2]));
                                } else if (value instanceof NumberValue) {
                                    ((NumberValue) value).setValue(Double.parseDouble(split[2]));
                                } else if (value instanceof ModeValue) {
                                    ((ModeValue) value).set(split[2]);
                                }

                            } catch (final NumberFormatException ignored) {
                                Reversal.notificationManager.registerNotification(I18n.format("command.message.valueError", split[1]), I18n.format("command.title"), NotificationType.ERROR);
                                Reversal.showMsg(I18n.format("command.message.valueError", split[1]));
                                return;
                            }
                        } catch (final ArrayIndexOutOfBoundsException ignored) {
                            Reversal.notificationManager.registerNotification(I18n.format("command.message.valueError", split[1]), I18n.format("command.title"), NotificationType.ERROR);
                            Reversal.showMsg(I18n.format("command.message.valueError", split[1]));
                        }

                        return;
                    }

                    Reversal.notificationManager.registerNotification(I18n.format("command.message.valueNonExist", split[1].toLowerCase(), command.toLowerCase()),
                            I18n.format("command.title"), NotificationType.ERROR);
                    Reversal.showMsg(I18n.format("command.message.valueNonExist", split[1].toLowerCase(), command.toLowerCase()));
                    return;
                }
            }
        }

        Reversal.notificationManager.registerNotification(I18n.format("command.message.moduleOrCommandNonExist", command.toLowerCase()), I18n.format("command.title"), NotificationType.ERROR);
        Reversal.showMsg(I18n.format("command.message.moduleOrCommandNonExist", command.toLowerCase()));
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
