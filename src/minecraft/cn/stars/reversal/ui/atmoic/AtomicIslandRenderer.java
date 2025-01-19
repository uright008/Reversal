package cn.stars.reversal.ui.atmoic;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.module.impl.hud.AtomicIsland;
import cn.stars.reversal.ui.gui.GuiMicrosoftLoginPending;
import cn.stars.reversal.ui.gui.GuiReversalSettings;
import cn.stars.reversal.ui.modern.impl.ModernMainMenu;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.RenderUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.GuiConnecting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 原子岛
 * @author Stars
 */
@Getter
@Setter
public class AtomicIslandRenderer implements GameInstance {
    public Animation x = new Animation(Easing.EASE_OUT_EXPO, 800);
    public Animation y = new Animation(Easing.EASE_OUT_EXPO, 800);
    private ScaledResolution sr;
    public String mainText;
    public final ArrayList<AtomicTask> tasks = new ArrayList<>();

    private boolean taskScheduled = false;
    private long currentTaskTimeout;
    private TimeUtil taskTimeoutTimer = new TimeUtil();
    private long startTime;

    public AtomicIslandRenderer() {
        this.sr = new ScaledResolution(mc);
        Atomic.height = 15;
        update(this.sr);
    }

    public void render(ScaledResolution sr) {
        this.sr = sr;
        Atomic.sortTasksByPriority(tasks);
        if (tasks.isEmpty()) {
            update(this.sr);
            runToXy(Atomic.x, Atomic.y);

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(x.getValue() - 1, y.getValue() - 1, (float) ((Atomic.x - x.getValue()) * 2) + 2, (float) ((Atomic.y - y.getValue()) * 2) + 2);

            RenderUtil.roundedRectangle((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), (float) ((Atomic.y - y.getValue()) * 2), 7, ColorUtil.empathyColor());
            MODERN_BLOOM_RUNNABLES.add(() -> RenderUtil.roundedRectangle((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), (float) ((Atomic.y - y.getValue()) * 2), 7, Color.BLACK));
            MODERN_BLUR_RUNNABLES.add(() -> RenderUtil.roundedRectangle((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), (float) ((Atomic.y - y.getValue()) * 2), 7, Color.BLACK));

            psm18.drawString(mainText, x.getValue() + 5, y.getValue() + 5, new Color(250, 250, 250, 250).getRGB());

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();
        } else {
            runToXy(Atomic.x, Atomic.y);

            GL11.glEnable(GL11.GL_SCISSOR_TEST);

            RenderUtil.scissor(x.getValue() - 1, y.getValue() - 1, (float) ((Atomic.x - x.getValue()) * 2) + 2, (float) ((Atomic.y - y.getValue()) * 2) + 2);

            RenderUtil.roundedRectangle((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), (float) ((Atomic.y - y.getValue()) * 2), 7, ColorUtil.empathyColor());
            if (ModuleInstance.getModule(AtomicIsland.class).percentBar.enabled) RenderUtil.roundedRectangle((float) x.getValue() + 3, (float) (y.getValue() + ((Atomic.y - y.getValue()) * 2) - 0.5), (Atomic.width - (System.currentTimeMillis() - startTime) * (Atomic.width / tasks.get(0).getDelay()) - 3), 1f, 4, new Color(255,255,255,255));
            MODERN_BLOOM_RUNNABLES.add(() -> RenderUtil.roundedRectangle((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), (float) ((Atomic.y - y.getValue()) * 2), 7, Color.BLACK));
            MODERN_BLUR_RUNNABLES.add(() -> RenderUtil.roundedRectangle((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), (float) ((Atomic.y - y.getValue()) * 2), 7, Color.BLACK));

            tasks.get(0).getTask().run();

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            //    FontManager.getPSM(18).drawString(mainText, x.getValue() + 5, y.getValue() + 5, new Color(250, 250, 250, 250).getRGB());
            updateTask();
        }
    }

    /**
     * XY渲染动画
     * @param realX 实际渲染的X
     * @param realY 实际渲染的Y
     */
    public void runToXy(float realX, float realY) {
        x.run(Atomic.getRenderX(realX));
        y.run(Atomic.getRenderY(realY));
    //    RenderUtil.scissor(x.getValue(), y.getValue(), (realX - x.getValue()) * 2, (realY - y.getValue()) * 2);
    }

    /**
     * 更新文字内容
     * 在有界面时隐藏
     */
    public void update(ScaledResolution sr) {
        if (mc.currentScreen instanceof ModernMainMenu || mc.currentScreen instanceof GuiMultiplayer || mc.currentScreen instanceof GuiConnecting || mc.currentScreen instanceof GuiSelectWorld || mc.currentScreen instanceof GuiDisconnected
        || mc.currentScreen instanceof GuiOptions || mc.currentScreen instanceof GuiMicrosoftLoginPending || mc.currentScreen instanceof GuiReversalSettings) {
            Atomic.x = sr.getScaledWidth() / 2f;
            Atomic.y = 40 + ModuleInstance.getModule(AtomicIsland.class).yOffset.getFloat();
            Atomic.width = 0;
            Atomic.height = 0;
            this.mainText = "";
        } else {
            Atomic.x = sr.getScaledWidth() / 2f;
            Atomic.y = 40 + ModuleInstance.getModule(AtomicIsland.class).yOffset.getFloat();
            this.updateMainText();
        }
    }

    /**
     * 更新任务
     */
    public void updateTask() {
        Atomic.x = sr.getScaledWidth() / 2f;
        if (!tasks.isEmpty()) {
            if (taskTimeoutTimer.hasReached(tasks.get(0).getDelay())) {
                tasks.remove(tasks.get(0));
                taskTimeoutTimer.reset();
                if (!tasks.isEmpty()) startTime = System.currentTimeMillis();
            }
        }
    }

    public void updateMainText() {
        mainText = "Reversal | " + Minecraft.getDebugFPS() + " FPS | " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        Atomic.width = psm18.width(mainText) + 10;
        Atomic.height = 15;
    }
}
