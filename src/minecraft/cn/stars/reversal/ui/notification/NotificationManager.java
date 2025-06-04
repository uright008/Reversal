package cn.stars.reversal.ui.notification;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.atmoic.island.Atomic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.Deque;

public final class NotificationManager implements GameInstance {

    private static final Deque<Notification> notifications = new ArrayDeque<>();

    public void registerNotification(final String description, final String title, final long delay, final NotificationType type) {
        notifications.add(new Notification(description, title, delay, type));
        Atomic.registerAtomic(description, title, delay, "t");
    }

    public void registerNotification(final String description, final String title, final long delay, final NotificationType type, final int priority) {
        notifications.add(new Notification(description, title, delay, type));
        Atomic.registerAtomic(description, title, delay, "t", priority);
    }

    public void registerNotification(final String description, final String title, final NotificationType type) {
        long delay = (long) (regular20.getWidth(description) * 30);
        notifications.add(new Notification(description, title, delay, type));
        Atomic.registerAtomic(description, title, delay, "t");
    }

    public void registerNotification(final String description, final long delay, final NotificationType type) {
        String title = StringUtils.capitalize(type.name().toLowerCase());
        notifications.add(new Notification(description, title, delay, type));
        Atomic.registerAtomic(description, title, delay, "t");
    }

    public void registerNotification(final String description, final NotificationType type) {
        String title = StringUtils.capitalize(type.name().toLowerCase());
        long delay = (long) (regular20.getWidth(description) * 30);
        notifications.add(new Notification(description, title, delay, type));
        Atomic.registerAtomic(description, title, delay, "t");
    }

    public void registerNotification(final String description) {
        String title = StringUtils.capitalize(NotificationType.NOTIFICATION.name().toLowerCase());
        long delay = (long) (regular20.getWidth(description) * 30);
        notifications.add(new Notification(description, title, delay, NotificationType.NOTIFICATION));
        Atomic.registerAtomic(description, title, delay, "t");
    }


    public static void onRender2D() {
        if (!notifications.isEmpty()) {
           if (notifications.getFirst().getEnd() > System.currentTimeMillis()) {
                notifications.getFirst().y = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - 50;
                notifications.getFirst().render();
            } else {
                notifications.removeFirst();
            }
        }

        if (!notifications.isEmpty()) {
            int i = 0;
            try {
                for (final Notification notification : notifications) {
                    if (i == 0) {
                        i++;
                        continue;
                    }

                    notification.y = (new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - 18) - (35 * (i + 1));
                    notification.render();
                    i++;
                }
            } catch (final ConcurrentModificationException ignored) {
            }
        }
    }
}
