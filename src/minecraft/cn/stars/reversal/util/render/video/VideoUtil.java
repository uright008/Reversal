package cn.stars.reversal.util.render.video;

import java.io.File;
import java.nio.ByteBuffer;

import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.render.GlUtils;
import cn.stars.reversal.util.render.RenderUtil;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

@NativeObfuscation
public class VideoUtil {
    private static FFmpegFrameGrabber frameGrabber;
    private static double frameRate;
    private static int ticks;
    private static final TimeUtil nullTickTimer = new TimeUtil();
    private static boolean flag;
    private static long time;
    public static volatile boolean suspended = false;
    private static volatile boolean stopped = false;

    @SneakyThrows
    public static void init(File file) {
        ReversalLogger.info("[VideoPlayer] Initializing video player...");
        Frame frame;

        frameGrabber = new FFmpegFrameGrabber(file.getPath());
        frameGrabber.setPixelFormat(2);
        frameGrabber.setOption("loglevel", "quiet");
        frameGrabber.setOption("threads", "4");
        frameGrabber.setOption("hwaccel", "auto");
    //    frameGrabber.setOption("fflags", "nobuffer");

        time = 0L;
        ticks = 0;
        nullTickTimer.reset();
        flag = false;
        stopped = false;
        frameGrabber.start();
        frameRate = frameGrabber.getFrameRate();
        frameGrabber.grab();

        while ((frame = frameGrabber.grab()) == null || frame.image == null) {}

        RenderUtil.setBuffer((ByteBuffer)frame.image[0], frame.imageWidth, frame.imageHeight);

        time = System.currentTimeMillis();
        ticks++;
        startPlaybackThread();
    }

    @SneakyThrows
    public static void stop() {
        ReversalLogger.info("[VideoPlayer] Stopping video player...");
        stopped = true;
        if (frameGrabber != null) {
            frameGrabber.stop();
            frameGrabber.close();
        }
    }

    @SneakyThrows
    public static void restart() {
        ReversalLogger.info("[VideoPlayer] Restarting video player...");
        frameGrabber.restart();
    }

    @SneakyThrows
    private static void startPlaybackThread() {
        Thread thread = new Thread("Video Background"){

            @Override
            public void run() {
                try {
                    while (!stopped) {
                        if (flag && (!((double)(System.currentTimeMillis() - time) > 700.0 / frameRate) || suspended)) continue;
                        doGetBuffer();
                    }
                }
                catch (Exception e) {
                    ReversalLogger.error("[VideoPlayer] Error:", e);
                    ticks++;
                }
                this.interrupt();
            }
        };

        thread.setDaemon(true);
        thread.start();
    }

    @SneakyThrows
    private static void doGetBuffer() {
        int fLength = frameGrabber.getLengthInFrames() - 5;
        if (ticks < fLength) {
            Frame frame = frameGrabber.grab();
            if (frame != null && frame.image != null) {
                RenderUtil.setBuffer((ByteBuffer)frame.image[0], frame.imageWidth, frame.imageHeight);
                time = System.currentTimeMillis();
                ticks++;
                nullTickTimer.reset();
            } else {
                if (nullTickTimer.hasReached(500L)) {
                    ReversalLogger.warn("[VideoPlayer] Frame remains null for more than 0.5s! This should not happen. Resetting progress.");
                    ReversalLogger.warn("[VideoPlayer] Stats: {tick:" + ticks + ", frameNumber:" + frameGrabber.getFrameNumber() + " , totalFrames:" + fLength + "}");
                    time = System.currentTimeMillis();
                    ticks = 0;
                    frameGrabber.setFrameNumber(0);
                    nullTickTimer.reset();
                }
            }
        } else {
            ticks = 0;
            frameGrabber.setFrameNumber(0);
        }
        if (!flag) {
            flag = true;
        }
    }

    public static void render(int left, int top, int right, int bottom) {
        if (!stopped) {
            suspended = false;
            // 绑定材质
            RenderUtil.bindTexture();

            // 准备绘制
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glDepthMask(false);

            // 绘制图片
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex3f(left, bottom, 0.0f);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex3f(right, bottom, 0.0f);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex3f(right, top, 0.0f);
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex3f(left, top, 0.0f);
            GL11.glEnd();

            // 关闭
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        }
    }
}
