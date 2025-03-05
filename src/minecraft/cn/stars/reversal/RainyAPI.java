/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025Aerolite Society, All rights reserved.
 */
package cn.stars.reversal;

import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.reversal.IRCInstance;
import cn.stars.reversal.util.math.RandomUtil;
import cn.stars.reversal.util.misc.FileUtil;
import cn.stars.reversal.util.render.video.VideoUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.opengl.Display;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;

@Getter
@Setter
@UtilityClass
@SuppressWarnings("all")
public class RainyAPI {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static long window;
    
    public static IRCInstance ircUser = null;
    public static boolean hasJavaFX = true;

    /**
     * 客户端设置
     * 在GuiReversalSettings里可以对选项进行修改，在游戏启动时使用loadAPI()加载保存的数据
     */
    public static int backgroundId = 9;
    public static boolean isShaderCompatibility = false;
    public static boolean isViaCompatibility = false;
    public static boolean isSplashScreenDisabled = false;
    public static boolean isPreInited = false;
    public static boolean mainMenuDate = false;
    public static boolean guiSnow = false;
    public static boolean backgroundBlur = false;
    public static boolean imageScreen = false;

    /**
     * 崩溃报告上面的字
     */
    public static final String[] wittyCrashReport = new String[]
            {"玩原神玩的", "原神?启动!", "哇真的是你啊", "你怎么似了", "加瓦,救一下啊", "Bomb has been planted",
                    "闭嘴!我的父亲在mojang工作,他可以使你的mInEcRaFt崩溃", "纪狗气死我了", "致敬传奇耐崩王MiNeCrAfT", "你的客户端坠机了",
                    "It's been a long day without you my friend", "回来吧牢端", "为了你,我变成狼人模样"};
    /**
     * 随机标题
     */
    public static final String[] wittyTitle = new String[]
            {"当一个人做出一个决定时,想必他已做好了觉悟", "一个没有错的人,有什么需要挽回的呢?", "我们见证时代的兴衰,我们感叹人生的轮回", "Tough. Complex. Incomprehensible.", "时间永远是最难跨过的分界线", "自己选择的路终究由你自己跨过",
            "渴望陪伴,又渴望孤独", "Heaven will not have mercy on the hateful.", "可惜我不是你,没能成为你这样的有成之人", "坐拥千万怀抱,不满生活一毛", "昨夜西风凋碧树,独上高楼,望尽天涯路", "明天和死亡哪个会先到来?",
            "虚伪才是绝对的真实", "为了一个人,你会付出自己的一切吗?", "你希望有一个人,为你付出他的一切吗?", "Metamorphosis.", "寂寞的人唱伤心的歌~"};

    public static String getRandomTitle() {
        return wittyTitle[RandomUtil.INSTANCE.nextInt(0, wittyTitle.length)];
    }

    /**
     * LWJGL3: 初始化GLFW
     * 获取窗口GL Context, 允许使用GLFW操作, 防止杂鱼无法得到上下文导致JVM崩溃
     * @author Stars
     */
    public static void setupGLFW() {
        window = Display.getWindow();
        GLFW.glfwMakeContextCurrent(window);
    }

    /**
     * 检测窗口拖入文件
     * 在主菜单检测是否有.mp4文件拖入并替换背景
     * @author Stars
     */
    public static void setupDrag() {
        GLFW.glfwSetDropCallback(window, (window, count, names) -> {
            String filePath = GLFWDropCallback.getName(names, 0);
            if (mc.currentScreen instanceof AtomicMenu) {
                if (count == 1) {
                    File droppedFile = new File(filePath);

                    // 检测后缀名
                    if (droppedFile.exists() && droppedFile.getName().toLowerCase().endsWith(".mp4")) {
                        ReversalLogger.info("Dragged file detected: " + droppedFile.getAbsolutePath());

                        try {
                            // 停止加载
                            VideoUtil.stop();
                            // 将获取的文件替换现有的文件
                            File tempFile = new File(Minecraft.getMinecraft().mcDataDir, "Reversal/Background");
                            File videoFile = new File(tempFile, "background.mp4");
                            Files.copy(droppedFile.toPath(), videoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            // 重新加载
                            VideoUtil.init(videoFile);
                            Reversal.notificationManager.registerNotification("成功更换视频背景!", "主菜单", 2000L, NotificationType.SUCCESS);
                        } catch (Exception e) {
                            ReversalLogger.error("Error while loading file.", e);
                            Reversal.notificationManager.registerNotification("更换视频背景时出现错误!", "主菜单", 2000L, NotificationType.ERROR);
                        }
                    } else {
                        Reversal.notificationManager.registerNotification("不支持的文件类型!", "主菜单", 2000L, NotificationType.ERROR);
                        ReversalLogger.error("Dragged file read failed.");
                    }
                }
            }
        });
    }

    /**
     * 加载客户端设置
     */
    public static void loadAPI(boolean post) {
        ReversalLogger.info("Loading RainyAPI...");
        final String client = FileUtil.loadFile("client.txt");

        // re-save if not available on start.
        if (client == null || !client.contains("DisableShader") || !client.contains("DisableViaMCP") || !client.contains("DisableSplashScreen") || !client.contains("IsPreInited")) {
            processAPI(false);
            return;
        }

        final String[] clientLines = client.split("\r\n");

        for (final String line : clientLines) {
            if (line == null) return;

            final String[] split = line.split("_");

            if (split[0].contains("DisableShader")) {
                isShaderCompatibility = Boolean.parseBoolean(split[1]);
            }
            if (split[0].contains("DisableViaMCP")) {
                isViaCompatibility = Boolean.parseBoolean(split[1]);
            }
            if (split[0].contains("DisableSplashScreen")) {
                isSplashScreenDisabled = Boolean.parseBoolean(split[1]);
            }
            if (split[0].contains("IsPreInited")) {
                isPreInited = Boolean.parseBoolean(split[1]);
            }
            if (split[0].contains("MainMenuDate")) {
                mainMenuDate = Boolean.parseBoolean(split[1]);
            }
            if (split[0].contains("GuiSnow")) {
                guiSnow = Boolean.parseBoolean(split[1]);
            }
            if (split[0].contains("BackgroundBlur")) {
                backgroundBlur = Boolean.parseBoolean(split[1]);
            }
            if (split[0].contains("ImageScreen")) {
                imageScreen = Boolean.parseBoolean(split[1]);
            }
            if (split[0].contains("CustomText") && post) {
                Reversal.customText = split[1];
            }
        }
    }

    /**
     * 保存客户端设置
     */
    public static void processAPI(boolean post) {
        final StringBuilder clientBuilder = new StringBuilder();
        clientBuilder.append("DisableShader_").append(isShaderCompatibility).append("\r\n");
        clientBuilder.append("DisableViaMCP_").append(isViaCompatibility).append("\r\n");
        clientBuilder.append("DisableSplashScreen_").append(isSplashScreenDisabled).append("\r\n");
        clientBuilder.append("IsPreInited_").append(isPreInited).append("\r\n");
        clientBuilder.append("LicenseReviewed_").append(isPreInited).append("\r\n");
        clientBuilder.append("MainMenuDate_").append(mainMenuDate).append("\r\n");
        clientBuilder.append("GuiSnow_").append(guiSnow).append("\r\n");
        clientBuilder.append("BackgroundBlur_").append(backgroundBlur).append("\r\n");
        clientBuilder.append("ImageScreen_").append(imageScreen).append("\r\n");
        if (post) clientBuilder.append("CustomText_").append(Reversal.customText).append("\r\n");

        FileUtil.saveFile("client.txt", true, clientBuilder.toString());
    }

    public static long createSubWindow() {
        GLFW.glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        return GLFW.glfwCreateWindow(1, 1, "SubWindow", MemoryUtil.NULL, Display.getWindow());
    }
}
