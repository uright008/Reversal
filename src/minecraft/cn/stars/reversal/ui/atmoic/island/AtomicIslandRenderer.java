package cn.stars.reversal.ui.atmoic.island;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.module.impl.hud.AtomicIsland;
import cn.stars.reversal.music.api.player.MusicPlayer;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.ColorUtils;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

    // maybe another location better
    private DynamicTexture coverTexture;

    public String mainText;
    public final ArrayList<AtomicTask> tasks = new ArrayList<>();

    private TimeUtil taskTimeoutTimer = new TimeUtil();
    private long startTime;

    private MFont psm20 = FontManager.getPSM(20);
    private MFont psr18 = FontManager.getPSR(18);

    public AtomicIslandRenderer() {
        this.sr = new ScaledResolution(mc);
        Atomic.height = 15;
        update(this.sr);
    }

    public void render(ScaledResolution sr) {
        if (!ModuleInstance.getModule(AtomicIsland.class).enabled) {
            tasks.clear();
            return;
        }
        this.sr = sr;
        Atomic.sortTasksByPriority(tasks);
        if (tasks.isEmpty()) {
            MusicPlayer musicPlayer = RainyAPI.hasJavaFX ? Reversal.musicManager.screen.player : null;
            if (musicPlayer != null && musicPlayer.getMusic() != null && !musicPlayer.isPaused()) {
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_SCISSOR_TEST);

                if (coverTexture == null) try {
                    coverTexture = new DynamicTexture(ImageIO.read(musicPlayer.getMusic().getCoverImage()));
                } catch (Exception ignored) {
                }
                if (coverTexture != null) {
                    GlStateManager.bindTexture(coverTexture.getGlTextureId());
                }
                Atomic.x = sr.getScaledWidth() / 2f;
                Atomic.y = 40 + ModuleInstance.getModule(AtomicIsland.class).yOffset.getFloat();
                Atomic.height = 40;
                Atomic.width = 45 + Math.max(Math.max(psm20.width(musicPlayer.getMusic().getName()), psr18.width(musicPlayer.getMusic().getArtist())), psr18.width(getLyrics(musicPlayer)));
                runToXy(Atomic.x, Atomic.y);

                drawBackgroundAuto(1);

                if (ModuleInstance.getModule(AtomicIsland.class).percentBar.enabled) {
                    RenderUtil.roundedRectangle((float) x.getValue() + 6, (float) (y.getValue() + ((Atomic.y - y.getValue()) * 2) + 0.5f), Atomic.width - 12, 5f, 2.5f, new Color(255, 255, 255, 80));
                    RenderUtil.roundedRectangle((float) x.getValue() + 6, (float) (y.getValue() + ((Atomic.y - y.getValue()) * 2) + 0.5f), (musicPlayer.getCurrentTime() * ((Atomic.width - 12) / musicPlayer.getMusic().getDuration())), 5f, 2.5f, new Color(255, 255, 255, 255));
                    MODERN_BLOOM_RUNNABLES.add(() -> RenderUtil.roundedRectangle((float) x.getValue() + 6, (float) (y.getValue() + ((Atomic.y - y.getValue()) * 2) + 0.5f), (musicPlayer.getCurrentTime() * ((Atomic.width - 12) / musicPlayer.getMusic().getDuration())), 5f, 2.5f, new Color(255, 255, 255, 255)));
                }

                RoundedUtil.drawRoundTextured((float) x.getValue() + 5, (float) y.getValue() + 5, 30, 30, 5, 255);

                psm20.drawString(musicPlayer.getMusic().getName(), x.getValue() + 40,  y.getValue() + 5, new Color(250, 250, 250, 250).getRGB());
                psr16.drawString(musicPlayer.getMusic().getArtist(), x.getValue() + 40,  y.getValue() + 16, new Color(220, 220, 220, 220).getRGB());
                RenderUtil.rect(x.getValue() + 41, y.getValue() + 25, Atomic.width - 46, 0.5, new Color(200, 200, 200, 200));
                psr18.drawString(getLyrics(musicPlayer), x.getValue() + 40,  y.getValue() + 30, new Color(250, 250, 250, 250).getRGB());

                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                GL11.glPopMatrix();
            } else {
                update(this.sr);
                runToXy(Atomic.x, Atomic.y);

                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_SCISSOR_TEST);

                drawBackgroundAuto(0);

                psm18.drawString(mainText, x.getValue() + 5, y.getValue() + 5, new Color(250, 250, 250, 250).getRGB());

                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                GL11.glPopMatrix();
            }
        } else {
            runToXy(Atomic.x, Atomic.y);

            GL11.glEnable(GL11.GL_SCISSOR_TEST);

            drawBackgroundAuto(1);

            AtomicTask task = tasks.get(0);

            if (ModuleInstance.getModule(AtomicIsland.class).percentBar.enabled) {
                RenderUtil.roundedRectangle((float) x.getValue() + 6, (float) (y.getValue() + ((Atomic.y - y.getValue()) * 2)), Atomic.width - 12, 5f, 2.5f, new Color(255, 255, 255, 80));
                RenderUtil.roundedRectangle((float) x.getValue() + 6, (float) (y.getValue() + ((Atomic.y - y.getValue()) * 2)), ((System.currentTimeMillis() - startTime) * ((Atomic.width - 12) / task.getDelay())), 5f, 2.5f, new Color(255, 255, 255, 255));
                MODERN_BLOOM_RUNNABLES.add(() -> RenderUtil.roundedRectangle((float) x.getValue() + 6, (float) (y.getValue() + ((Atomic.y - y.getValue()) * 2)), ((System.currentTimeMillis() - startTime) * ((Atomic.width - 12) / task.getDelay())), 5f, 2.5f, new Color(255, 255, 255, 255)));
            }
            task.getTask().run();

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
     * 绘制黑色背景
     * GUI和世界不是一个后处理机制，分别处理
     */
    public void drawBackgroundAuto(int identifier) {
        float renderHeight = (float) ((Atomic.y - y.getValue()) * 2) + (ModuleInstance.getModule(AtomicIsland.class).percentBar.enabled && identifier == 1 ? 10 : 0);

        RenderUtil.scissor(x.getValue() - 1, y.getValue() - 1, (float) ((Atomic.x - x.getValue()) * 2) + 2, renderHeight + 2);

        if (!ModuleInstance.getModule(AtomicIsland.class).runningLight.enabled) {
            RenderUtil.roundedRectangle((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), renderHeight, 7, ColorUtil.empathyColor());
            MODERN_BLOOM_RUNNABLES.add(() -> RenderUtil.roundedRectangle((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), renderHeight, 7, Color.BLACK));
            MODERN_BLUR_RUNNABLES.add(() -> RenderUtil.roundedRectangle((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), renderHeight, 7, Color.BLACK));
        } else {
            RoundedUtil.drawGradientRound((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), renderHeight, 8,
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, ColorUtil.transparent, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, ColorUtil.transparent, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, ColorUtil.transparent, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, ColorUtil.transparent, true));
            RenderUtil.roundedRectangle((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), renderHeight, 7, ColorUtil.empathyColor());
            MODERN_BLOOM_RUNNABLES.add(() -> RoundedUtil.drawGradientRound((float) x.getValue(), (float) y.getValue(), (float) ((Atomic.x - x.getValue()) * 2), renderHeight, 8,
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, ColorUtil.transparent, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, ColorUtil.transparent, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, ColorUtil.transparent, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, ColorUtil.transparent, true)));
        }
    }

    /**
     * 更新文字内容
     * 在有界面时隐藏
     */
    public void update(ScaledResolution sr) {
        if (mc.theWorld == null) {
            Atomic.x = sr.getScaledWidth() / 2f;
            Atomic.y = 40 + ModuleInstance.getModule(AtomicIsland.class).yOffset.getFloat();
            Atomic.width = 0;
            Atomic.height = 0;
            this.mainText = "";
        } else {
            mainText = "Reversal | " + Minecraft.getDebugFPS() + " FPS | " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
            Atomic.width = psm18.width(mainText) + 10;
            Atomic.height = 15;
            Atomic.x = sr.getScaledWidth() / 2f;
            Atomic.y = 40 + ModuleInstance.getModule(AtomicIsland.class).yOffset.getFloat();
        }
    }

    /**
     * 更新任务
     */
    public void updateTask() {
        Atomic.x = sr.getScaledWidth() / 2f;
        Atomic.y = 40 + ModuleInstance.getModule(AtomicIsland.class).yOffset.getFloat();
        if (!tasks.isEmpty()) {
            if (taskTimeoutTimer.hasReached(tasks.get(0).getDelay())) {
                tasks.remove(tasks.get(0));
                taskTimeoutTimer.reset();
                if (!tasks.isEmpty()) startTime = System.currentTimeMillis();
            }
        }
    }

    public void reset() {
        coverTexture = null;
    }

    public String getLyrics(MusicPlayer musicPlayer) {
        switch (ModuleInstance.getModule(AtomicIsland.class).musicLyricsMode.getMode()) {
            case "Origin": return musicPlayer.getCurrentLyric(false);
            case "Translated": return musicPlayer.getCurrentLyric(true);
            case "Both": return musicPlayer.getMusic().hasTranslate || !musicPlayer.getMusic().translatedLyrics.isEmpty() ? musicPlayer.getCurrentLyric(true) + " (" + musicPlayer.getCurrentLyric(false) + ")" : musicPlayer.getCurrentLyric(false);
        }
        return "Unknown";
    }
}
