package cn.stars.reversal.module.impl.player;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.PacketReceiveEvent;
import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.StringCalculator;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.NoteValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.Timer;
import java.util.TimerTask;

@ModuleInfo(name = "ThePitUtilities", localizedName = "module.ThePitUtilities.name", description = "Utilities for Hypixel The Pit", localizedDescription = "module.ThePitUtilities", category = Category.PLAYER, experimentOnly = true)
public class ThePitUtilities extends Module {
    public final NoteValue note = new NoteValue("Use CHINESE!!!", this);
    public final BoolValue quickMaths = new BoolValue("Quick Maths", this, false);
    public final NumberValue quickMathsDelay = new NumberValue("Quick Maths Delay", this, 0.5, 0, 2, 0.1);
    public final BoolValue eventNotifier = new BoolValue("Event Notifier", this, false);
    public final BoolValue killNotifier = new BoolValue("Kill Notifier", this, false);

    private boolean quickMathListening = false;

    @Override
    public void onRender2D(Render2DEvent event) {

    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) event.getPacket();
            if (packet.getChatComponent() != null) {
                String message = packet.getChatComponent().getUnformattedText();
                if (quickMaths.enabled) {
                    if (message.contains("QUICK MATHS!") && !quickMathListening) {
                        log("Event begin: Quick Maths. Start listening for question!");
                        quickMathListening = true;
                        return;
                    }
                    if (message.contains("QUICK MATHS OVER!") && quickMathListening) {
                        log("Event end: Quick Maths. Stop listening.");
                        quickMathListening = false;
                        return;
                    }
                    if (quickMathListening) {
                        if (message.contains("Solve: ")) {
                            String toSolve = findSubstringAfter(removeColorCode(message), "Solve: ");
                            String result = StringCalculator.calculate(toSolve);
                            if (result != null && !result.isEmpty()) {
                                log("Found question and solution: %s = %s. Stop listening.", toSolve, result);
                                schedule(() -> mc.thePlayer.sendChatMessage(result), quickMathsDelay.getValue());
                                quickMathListening = false;
                            }
                        }
                    }
                }
                if (eventNotifier.enabled) {
                    if (message.contains("MINOR EVENT!") || message.contains("小型乱斗事件！")) {
                        log("A minor event is beginning!");
                    }
                    if (message.contains("MAJOR EVENT!") || message.contains("大型乱斗事件！")) {
                        log("A major event is beginning!");
                    }
                }
                if (killNotifier.enabled) {
                    if (message.contains("助攻！")) {
                        log("Assist: %s", findSubstringAfter(message, "协助击杀"));
                    } else if (message.contains("击杀！") || message.contains("二杀！") || message.contains("三杀！") || message.contains("四杀！") || message.contains("五杀！")) {
                        if (findSubstringAfter(message, "-").isEmpty()) return;
                        log("Kill: %s", findSubstringAfter(message, "-"));
                    } else if (message.contains("死亡！")) {
                        log("Died: %s", findSubstringAfter(message, "死亡！").replace("查看回放", ""));
                    }
                }
            }
        }
    }

    private void log(String message, Object... args) {
        mc.addScheduledTask(() -> {
            Reversal.notificationManager.registerNotification(String.format(message, args), "ThePitUtilities", NotificationType.NOTIFICATION);
        });
    }

    private void schedule(Runnable runnable, double delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, (long) delay * 1000L);
    }

    private String findSubstringAfter(String source, String search) {
        int index = source.indexOf(search);
        if (index == -1) {
            return "";
        }
        return source.substring(index + search.length());
    }
    private String removeColorCode(String input) {
        if (input == null) return "";
        String validChars = "0123456789abcdefklmnor";
        StringBuilder result = new StringBuilder();
        int i = 0;
        int length = input.length();

        while (i < length) {
            char current = input.charAt(i);
            if (current == '§' && i + 1 < length) {
                char nextChar = input.charAt(i + 1);
                if (validChars.indexOf(nextChar) != -1) {
                    i += 2;
                    continue;
                }
            }
            result.append(current);
            i++;
        }

        return result.toString();
    }
}
