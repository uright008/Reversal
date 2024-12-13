package cn.stars.reversal.util.misc;

import java.io.File;
import java.nio.ByteBuffer;

import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.render.GlUtils;
import cn.stars.reversal.util.render.RenderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.lwjgl.opengl.GL11;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

@NativeObfuscation
public class VideoUtil {
    private static FFmpegFrameGrabber frameGrabber;
    private static double frameRate;
    private static int ticks;
    private static boolean flag;
    private static long time;
    public static boolean suspended = false;
    private static boolean stopped = false;
    private static final Logger logger = LogManager.getLogger("VideoPlayer");

    public static void init(File file) throws FFmpegFrameGrabber.Exception {
        ReversalLogger.info("[*] Initializing video player...");
        Frame frame;
        frameGrabber = new FFmpegFrameGrabber(file.getPath());
        frameGrabber.setPixelFormat(2);
        frameGrabber.setOption("loglevel", "quiet");
        frameGrabber.setOption("threads", "2");
        frameGrabber.setOption("buffer_size", "1024000");
        time = 0L;
        ticks = 0;
        flag = false;
        stopped = false;
        frameGrabber.start();
        frameRate = frameGrabber.getFrameRate();
        frameGrabber.grab();

        while ((frame = frameGrabber.grab()) == null || frame.image == null) {}


        RenderUtil.setBuffer((ByteBuffer)frame.image[0], frame.imageWidth, frame.imageHeight);

        time = System.currentTimeMillis();
        ++ticks;
        Thread thread = getThread();
        thread.start();
    }

    public static void retryIfFailed(File file) throws InterruptedException, FFmpegFrameGrabber.Exception {
        Thread.sleep(1000L);
        init(file);
    }

    public static void stop() {
        try {
            ReversalLogger.info("[*] Stopping video player...");
            stopped = true;
            frameGrabber.release();
            frameGrabber.stop();
        } catch (Exception ignored) {
        }
    }

    private static Thread getThread() {
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
                    logger.error(e.getMessage());
                }
                this.interrupt();
            }
        };
        thread.setDaemon(true);
        return thread;
    }

    private static void doGetBuffer() throws FFmpegFrameGrabber.Exception {
        int fLength = frameGrabber.getLengthInFrames() - 5;
        if (ticks < fLength) {
            Frame frame = frameGrabber.grab();
            if (frame != null && frame.image != null) {
                RenderUtil.setBuffer((ByteBuffer)frame.image[0], frame.imageWidth, frame.imageHeight);
                time = System.currentTimeMillis();
                ++ticks;
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

            GlUtils.startAntiAtlas();
            GlUtils.doAntiAtlas();

            GL11.glDepthMask(false);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            // 绘制图片
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex3f((float)left, (float)bottom, 0.0f);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex3f((float)right, (float)bottom, 0.0f);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex3f((float)right, (float)top, 0.0f);
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex3f((float)left, (float)top, 0.0f);
            GL11.glEnd();

            // 关闭
            GlUtils.stopAntiAtlas();
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        }
    }
}
