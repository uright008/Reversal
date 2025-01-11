package cn.stars.reversal.ui.atmoic;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Getter
@Setter
public class AtomicIslandRenderer implements GameInstance {
    public Animation x = new Animation(Easing.EASE_OUT_EXPO, 800);
    public Animation y = new Animation(Easing.EASE_OUT_EXPO, 800);
    private ScaledResolution sr;
    public String mainText;
    public final ArrayList<Runnable> toRender = new ArrayList<>();

    private boolean taskScheduled = false;
    private long currentTaskTimeout;
    private TimeUtil taskTimeout = new TimeUtil();

    public AtomicIslandRenderer() {
        this.sr = new ScaledResolution(mc);
        Atomic.height = 15;
        update(this.sr);
    }

    public void render(Render2DEvent event) {
        this.sr = event.getScaledResolution();
        if (!taskScheduled) {
            update(this.sr);
            runToXy(Atomic.x, Atomic.y);

            if (!x.isFinished() || !y.isFinished()) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.scissor(x.getValue() - 2, y.getValue() - 2, (float) ((Atomic.x - x.getValue()) * 2) + 4, (float) ((Atomic.y - y.getValue()) * 2) + 4);
            }

            RoundedUtil.drawRound((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), (float) ((Atomic.y - y.getValue()) * 2), 7, ColorUtil.empathyColor());
            psm18.drawString(mainText, x.getValue() + 5, y.getValue() + 5, new Color(250, 250, 250, 250).getRGB());

            if (!x.isFinished() || !y.isFinished()) GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            updateTask();
            runToXy(Atomic.x, Atomic.y);

            if (!x.isFinished() || !y.isFinished()) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.scissor(x.getValue() - 2, y.getValue() - 2, (float) ((Atomic.x - x.getValue()) * 2) + 4, (float) ((Atomic.y - y.getValue()) * 2) + 4);
            }

            RoundedUtil.drawRound((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), (float) ((Atomic.y - y.getValue()) * 2), 7, ColorUtil.empathyColor());
            toRender.forEach(Runnable::run);

            if (!x.isFinished() || !y.isFinished()) GL11.glDisable(GL11.GL_SCISSOR_TEST);
            //    FontManager.getPSM(18).drawString(mainText, x.getValue() + 5, y.getValue() + 5, new Color(250, 250, 250, 250).getRGB());
        }
    }

    public void runToXy(float realX, float realY) {
        x.run(Atomic.getRenderX(realX));
        y.run(Atomic.getRenderY(realY));
    //    RenderUtil.scissor(x.getValue(), y.getValue(), (realX - x.getValue()) * 2, (realY - y.getValue()) * 2);
    }

    public void update(ScaledResolution sr) {
        Atomic.x = sr.getScaledWidth() / 2f;
        Atomic.y = 40;
        this.updateMainText();
    }

    public void updateTask() {
        if (taskScheduled) {
            if (taskTimeout.hasReached(currentTaskTimeout)) {
                toRender.clear();
                taskTimeout.reset();
                taskScheduled = false;
            }
        }
    }

    public void updateMainText() {
        mainText = "Reversal | " + Minecraft.getDebugFPS() + " FPS | " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        Atomic.width = psm18.width(mainText) + 10;
        Atomic.height = 15;
    }
}
