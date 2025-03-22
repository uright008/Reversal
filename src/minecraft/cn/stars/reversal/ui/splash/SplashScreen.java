package cn.stars.reversal.ui.splash;

import cn.stars.reversal.HopeEngine;
import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.ui.splash.impl.VideoLoadingScreen;
import cn.stars.reversal.ui.splash.util.AsyncGLContentLoader;
import cn.stars.reversal.ui.splash.util.Interpolations;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.math.StopWatch;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.video.VideoManager;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author ImXianyu
 * @since 4/24/2023 9:57 AM
 */
public class SplashScreen {

    private static final Animation animation = new Animation(Easing.EASE_OUT_EXPO, 1500);
    private static final Animation animation2 = new Animation(Easing.EASE_OUT_EXPO, 1500);
    public static final Object renderLock = new Object();
    public static final Object finishLock = new Object();
    private static Minecraft mc = Minecraft.getMinecraft();
    private static final int backgroundColor = ColorUtil.hexColor(0, 0, 0, 255);
    private static final LoadingScreenRenderer loadingScreenRenderer = getLoadingScreen();
    public static int progress = 0;
    public static String progressText = "";
    public static Thread splashThread;
    public static float alpha = 1;
    public static boolean waiting = false;
    private static boolean firstFrame = false;
    private static Throwable threadError;
    private static StopWatch stopWatch = new StopWatch();

    public static boolean crashDetected = false;

    public static long subWindow;

    /**
     * choose a loading screen randomly.
     *
     * @return loading screen's instance
     */
    @SneakyThrows
    private static LoadingScreenRenderer getLoadingScreen() {
        return new VideoLoadingScreen();
    }

    @SneakyThrows
    public static void init() {
        if (RainyAPI.isSplashScreenDisabled) return;
        // INIT LOL
        subWindow = RainyAPI.createSubWindow();
        GLFW.glfwMakeContextCurrent(subWindow);
        GL.createCapabilities();
        mc.updateDisplay();
        VideoManager.loadSplash();

        splashThread = new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {

                GLFW.glfwMakeContextCurrent(Display.getWindow());
                GL.createCapabilities();

                initGL();

                loadingScreenRenderer.init();


                while (true) {

                    if (Display.wasResized()) {
                        Minecraft.getMinecraft().resize(mc.displayWidth, mc.displayHeight);
                        initGL();
                    }

                    if (Display.isCloseRequested()) {
                        System.exit(0);
                    }

                    glClear(GL_COLOR_BUFFER_BIT);

                    if (!firstFrame) {
                        firstFrame = true;
                        RenderUtil.deltaFrameTime = 0;
                    }

                    animation.run(Display.getWidth() / 100.0 * progress);
                    animation2.run(progress);

                    synchronized (renderLock) {

                        GlStateManager.pushAttrib();

                        Interpolations.calcFrameDelta();

                        int width = Display.getWidth();
                        int height = Display.getHeight();

                        GlStateManager.matrixMode(GL11.GL_PROJECTION);
                        GlStateManager.loadIdentity();
                        GlStateManager.ortho(0.0D, Display.getWidth(), Display.getHeight(), 0.0D, 1000.0D, 3000.0D);
                        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                        GlStateManager.loadIdentity();
                        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
                        GlStateManager.disableLighting();
                        GlStateManager.disableFog();
                        GlStateManager.disableDepth();
                        GlStateManager.enableTexture2D();
                        GlStateManager.enableAlpha();
                        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

                        loadingScreenRenderer.render(Display.getWidth(), Display.getHeight());

                        if (progress != 100)
                            alpha = (Interpolations.interpBezier(alpha * 255, 0, 0.1f) * 0.003921568627451F);

                    //    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                    //    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

                    //    Rect.draw(0, 0, width, height, ColorUtil.hexColor(0, 0, 0, (int) (alpha * 255)), Rect.RectType.EXPAND);

                    //    Rect.draw(0, height - 2, animation.getValue(), 2, ColorUtil.hexColor(255,255,255, 150 + (int) animation2.getValue()), Rect.RectType.EXPAND);

                        GlStateManager.enableAlpha();
                        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                        Display.update();
                        Display.sync(240);
                        GlStateManager.popAttrib();

                        if (waiting && loadingScreenRenderer.isLoadingScreenFinished() && AsyncGLContentLoader.isAllTasksFinished()) {

                            if (mc == null)
                                mc = Minecraft.getMinecraft();


                            mc.displayWidth = Display.getWidth();
                            mc.displayHeight = Display.getHeight();
                            mc.resize(mc.displayWidth, mc.displayHeight);
                            glClearColor(1, 1, 1, 1);
                            glEnable(GL_DEPTH_TEST);
                            glDepthFunc(GL_LEQUAL);
                            glEnable(GL_ALPHA_TEST);
                            glAlphaFunc(GL_GREATER, .1f);

                            GLFW.glfwMakeContextCurrent(0L);

                            synchronized (finishLock) {
                                finishLock.notifyAll();
                            }

                            break;
                        }


                    }
                }
            }

            private void initGL() {
                glClearColor((float) ((backgroundColor >> 16) & 0xFF) / 0xFF, (float) ((backgroundColor >> 8) & 0xFF) / 0xFF, (float) (backgroundColor & 0xFF) / 0xFF, 1);
                glDisable(GL_LIGHTING);
                glDisable(GL_DEPTH_TEST);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }

        }, "Loading Screen Thread");

        splashThread.setPriority(Thread.MAX_PRIORITY);
        splashThread.setUncaughtExceptionHandler((t, e) -> threadError = e);
        splashThread.start();
        checkThreadState();
    }

    private static void checkThreadState() {
        if (splashThread.getState() == Thread.State.TERMINATED || threadError != null) {
            throw new IllegalStateException("Loading Screen thread", threadError);
        }
    }

    @SneakyThrows
    public static void hide() {
//        hide = true;

        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.clearDepth(1.0D);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.cullFace(1029);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        mc.displayWidth = Display.getWidth();
        mc.displayHeight = Display.getHeight();
        mc.resize(mc.displayWidth, mc.displayHeight);
        glClearColor(1, 1, 1, 1);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, .1f);

        GLFW.glfwMakeContextCurrent(Display.getWindow());
        GL.createCapabilities();
    }

    @SneakyThrows
    public static void notifyGameLoaded() {
        if (RainyAPI.isSplashScreenDisabled) return;
        loadingScreenRenderer.onGameLoadFinishedNotify();

        while (!AsyncGLContentLoader.isAllTasksFinished()) {}

        waiting = true;
        synchronized (finishLock) {
            finishLock.wait();
        }

        GLFW.glfwMakeContextCurrent(Display.getWindow());
        GL.createCapabilities();
        hide();

        Reversal.postInitialize();

        ReversalLogger.info("[Startup] Totally took " + stopWatch.getElapsedTime() + " ms for game initialization!");
        Reversal.notificationManager.registerNotification(Reversal.NAME + " initialized successfully in " + stopWatch.getElapsedTime() + " ms!", "Startup", NotificationType.NOTIFICATION);
        stopWatch = null;

        Display.sync(60);
        mc.updateDisplay();
    }

    @SneakyThrows
    public static void show() {
//        hide = false;
        waiting = false;
        alpha = 1;
        mc.updateDisplay();

        synchronized (finishLock) {
            finishLock.notifyAll();
        }
    }

    @SneakyThrows
    public static void setProgress(int progress, String detail) {
        if (RainyAPI.isSplashScreenDisabled) return;
        SplashScreen.progress = progress;
        SplashScreen.progressText = detail;
        mc.updateDisplay();
        ReversalLogger.info("[Startup] " + progress + "% - " + detail);
        Display.setTitle("Reversal Startup Progress | HopeEngine " + HopeEngine.version + " | " + progress + "%");
    }

}
