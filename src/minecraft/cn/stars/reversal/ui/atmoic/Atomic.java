package cn.stars.reversal.ui.atmoic;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.module.impl.hud.AtomicIsland;
import cn.stars.reversal.util.misc.ModuleInstance;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * 原子
 * @author Stars
 */
@Getter
@Setter
public class Atomic implements GameInstance {

    public static AtomicIslandRenderer INSTANCE = new AtomicIslandRenderer();

    public static float x;
    public static float y;
    public static float width;
    public static float height;

    /**
     * 获取实际渲染时的XY
     */
    public static float getRenderX(float x) {
        return x - width / 2;
    }
    public static float getRenderY(float y) {
        return y - height / 2;
    }

    /**
     * 提交一个自定义渲染任务至AtomicIslandRenderer
     * @param task 渲染任务
     * @param timeout 渲染时间
     * @param allowRepeat 是否允许重复的任务
     */
    public static void submitTask(Runnable task, long timeout, boolean allowRepeat) {
        if (INSTANCE.tasks.isEmpty()) {
            INSTANCE.setStartTime(System.currentTimeMillis());
            INSTANCE.getTaskTimeoutTimer().reset();
        } else {
            if (!allowRepeat || !ModuleInstance.getModule(AtomicIsland.class).allowRepeat.enabled) {
                clearTasks();
                INSTANCE.setStartTime(System.currentTimeMillis());
                INSTANCE.getTaskTimeoutTimer().reset();
            }
        }
        INSTANCE.tasks.put(task, timeout);
    }

    public static void submitTask(Runnable task, long timeout) {
        submitTask(task, timeout, true);
    }

    /**
     * 提交一个AtomicIslandRenderer展示的预设消息
     * @param description 内容
     * @param title 标题
     * @param delay 渲染时间
     */
    public static void registerAtomic(final String description, final String title, final long delay) {
        registerAtomic(description, title, delay, "t");
    }

    /**
     * @param icon 标题前的图标
     */
    public static void registerAtomic(final String description, final String title, final long delay, final String icon) {
        registerAtomic(description, title, delay, icon, true);
    }

    public static void registerAtomic(final String description, final String title, final long delay, final String icon, final boolean allowRepeat) {
        submitTask(() -> {
            Atomic.width = Math.max(psm18.width(description), psb20.width(title) + 10) + 10;
            Atomic.height = 30;
            FontManager.getIcon(24).drawString(icon, Atomic.INSTANCE.x.getValue() + 3, Atomic.INSTANCE.y.getValue() + 6,  new Color(250, 250, 250, 250).getRGB());
            psb20.drawString(title, Atomic.INSTANCE.x.getValue() + 16, Atomic.INSTANCE.y.getValue() + 6, new Color(250, 250, 250, 250).getRGB());
            psm18.drawString(description, Atomic.INSTANCE.x.getValue() + 5, Atomic.INSTANCE.y.getValue() + 18, new Color(250, 250, 250, 250).getRGB());
        }, delay, allowRepeat);
    }

    public static void registerAtomic(final String description, final long delay) {
        submitTask(() -> {
            Atomic.width = psm18.width(description) + 10;
            Atomic.height = 15;
            psm18.drawString(description, Atomic.INSTANCE.x.getValue() + 5, Atomic.INSTANCE.y.getValue() + 5, new Color(250, 250, 250, 250).getRGB());
        }, delay);
    }

    public static void clearTasks() {
        INSTANCE.tasks.clear();
    }
}
