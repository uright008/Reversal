package cn.stars.reversal;

import cn.stars.addons.creativetab.ReversalTab;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.CommandManager;
import cn.stars.reversal.command.impl.*;
import cn.stars.reversal.command.impl.Chat;
import cn.stars.reversal.config.DefaultHandler;
import cn.stars.reversal.config.MusicHandler;
import cn.stars.reversal.module.*;
import cn.stars.reversal.module.impl.client.*;
import cn.stars.reversal.module.impl.hud.*;
import cn.stars.reversal.module.impl.render.*;
import cn.stars.reversal.module.impl.player.*;
import cn.stars.reversal.module.impl.combat.*;
import cn.stars.reversal.module.impl.misc.*;
import cn.stars.reversal.module.impl.movement.*;
import cn.stars.reversal.module.impl.addons.*;
import cn.stars.reversal.module.impl.world.*;
import cn.stars.reversal.music.MusicManager;
import cn.stars.reversal.ui.clickgui.modern.MMTClickGUI;
import cn.stars.reversal.ui.clickgui.modern.ModernClickGUI;
import cn.stars.reversal.ui.notification.NotificationManager;
import cn.stars.reversal.ui.theme.GuiTheme;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.misc.FileUtil;
import cn.stars.reversal.util.render.video.VideoManager;
import cn.stars.reversal.util.render.video.VideoUtil;
import cn.stars.reversal.util.reversal.Branch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.opengl.Display;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (c) 2025 Reversal, All rights reserved.
 * A Hack-Visual PVP Client by Stars.
 */
@NativeObfuscation
@StringEncryption
@Getter
public class Reversal {
    // Client Info
    public static final String NAME = "Reversal";
    public static final String VERSION = "v1.5.0";
    public static final String MINECRAFT_VERSION = "1.8.9";
    public static final String AUTHOR = "Stars";
    public static final Branch BRANCH = Branch.PRODUCTION;

    // Init
    public static Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();
    public static final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(2);

    public static String customName = "";
    public static String customText = ".setText <text>";

    public static ModuleManager moduleManager;
    public static NotificationManager notificationManager;
    public static CommandManager cmdManager;
    public static MusicManager musicManager;

    public static ModernClickGUI modernClickGUI;
    public static MMTClickGUI mmtClickGUI;

    public static GuiTheme guiTheme;
    public static CreativeTabs creativeTab;
    public static boolean firstBoot;

    // Core
    public static void start() {
        try {
            ReversalLogger.info("Loading client...");

            RainyAPI.loadAPI(true);

            // ViaMCP init
            if (!RainyAPI.isViaCompatibility) {
                ViaMCP.create();
                ViaMCP.INSTANCE.initAsyncSlider();
            }

            initialize();

            DefaultHandler.loadConfigs();

            ReversalLogger.info("Client loaded successfully.");
            ReversalLogger.info(NAME + " " + VERSION + " (Minecraft " + MINECRAFT_VERSION + "), made with love by " + AUTHOR + ".");
        } catch (Exception e) {
            ReversalLogger.error("An error has occurred while loading Reversal: ", e);
        }
    }

    public static void stop() {
        saveAll();
        threadExecutor.shutdownNow();
        threadPoolExecutor.shutdownNow();
    }

    // Usages
    public static void showMsg(Object msg) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§7[§b§l" + NAME + "§r§7] §r" + msg));
        }
    }
    public static void showCustomMsg(Object msg) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText((String) msg));
        }
    }

    // run required
    public static void saveAll() {
        DefaultHandler.saveConfig(false);
        RainyAPI.processAPI(true);
    }


    public static void initialize() {
        try {
            // Minecraft Pre-Initialize
            // Shut the fast render off
            Minecraft.getMinecraft().gameSettings.ofFastRender = false;

            // Reversal Initialize
            moduleManager = new ModuleManager();
            moduleManager.registerModules(modules);

            notificationManager = new NotificationManager();

            cmdManager = new CommandManager();
            cmdManager.registerCommands(commands);

            try {
                musicManager = new MusicManager();
                MusicHandler.load();
                musicManager.initGUI();
            } catch (NoClassDefFoundError e) {
                RainyAPI.hasJavaFX = false;
                ReversalLogger.warn("No JavaFX found in the current java version! Music player is disabled.");
            }

            guiTheme = new GuiTheme();
            modernClickGUI = new ModernClickGUI();
            mmtClickGUI = new MMTClickGUI();

            creativeTab = new ReversalTab();

        }
        catch (final Exception e) {
            ReversalLogger.error("An error has occurred while loading Reversal: ", e);
        }

        try {
            // 创建文件夹
            if (!FileUtil.coreDirectoryExists()) {
                firstBoot = true;
                FileUtil.createCoreDirectory();
            }

            if (!FileUtil.exists("Config" + File.separator)) {
                FileUtil.createDirectory("Config" + File.separator);
            }

            if (!FileUtil.exists("Script" + File.separator)) {
                FileUtil.createDirectory("Script" + File.separator);
            }

            if (!FileUtil.exists("Cache" + File.separator)) {
                FileUtil.createDirectory("Cache" + File.separator);
            }

            if (!FileUtil.exists("Background" + File.separator)) {
                FileUtil.createDirectory("Background" + File.separator);
            }
        } catch (final Exception e) {
            ReversalLogger.error("An error has occurred while loading Reversal: ", e);
        }
    }

    public static void postInitialize() {
        try {
            VideoUtil.stop();
            VideoManager.loadBackground();

            Display.setTitle(NAME + " " + VERSION + " " + Branch.getBranchName(BRANCH) + " | " + RainyAPI.getRandomTitle());
            //    Display.setTitle(NAME + " " + VERSION + " " + Branch.getBranchName(BRANCH));
            //    Display.setTitle(NAME + " (" + VERSION + "/" + BRANCH.name() + "/RainyAPI/LWJGL " + Sys.getVersion() + ")");
        } catch (final Exception e) {
            ReversalLogger.error("An error has occurred while loading Reversal: ", e);
        }
    }

    public static boolean onSendChatMessage(final String s) {
        if (s.startsWith(".") && !s.startsWith("./")) {
            cmdManager.callCommand(s.substring(1));
            return false;
        }
        return true;
    }

    private static final Command[] commands = new Command[] {
            new Bind(),
            new Chat(),
            new ClientName(),
            new ClientTitle(),
            new Config(),
            new Help(),
            new Name(),
            new Say(),
            new SetText(),
            new Toggle()
    };

    public static final Module[] modules = new Module[] {
            // Addons
            new FreeLook(),
            new MoBends(),
            new MusicPlayer(),
            new WaveyCapes(),
            new SkinLayers3D(),
            // Combat
            new ClickSound(),
            new NoClickDelay(),
            // Movement
            new Sprint(),
            // Misc
            new ClientSpoofer(),
            new CustomName(),
            new NoAchievements(),
            new Protocol(),
            // World
            new TimeTraveller(),
            // Player
            new AutoGG(),
            new Dinnerbone(),
            new HealthWarn(),
            new SmallPlayer(),
            new SmoothSneak(),
            // Render
            new Animations(),
            new AppleSkin(),
            new BAHalo(),
            new BetterFont(),
            new BlockinDisplay(),
            new BlockOverlay(),
            new Trail(),
            new ChinaHat(),
            new ClickGui(),
            new Crosshair(),
            new DamageParticle(),
            new EnvironmentEffect(),
            new Fullbright(),
            new HitEffect(),
            new ItemPhysics(),
            new JumpCircle(),
            new LineGlyphs(),
            new MotionBlur(),
            new NoBob(),
            new ReachDisplay(),
            new TargetESP(),
            new TNTTimer(),
            new TrueSights(),
            new Particles(),
            new SpeedGraph(),
            new Wings(),
            // Hud
            new Arraylist(),
            new AtomicIsland(),
            new BASticker(),
            new BPSCounter(),
            new CPSCounter(),
            new CustomText(),
            new FPSCounter(),
            new HUD(),
            new Keystrokes(),
            new MusicInfo(),
            new MusicVisualizer(),
            new PingCounter(),
            new PotionEffects(),
            new Scoreboard(),
            new SessionInfo(),
            new TargetHud(),
            new TextGui(),
            // Client
            new ClientSettings(),
            new PostProcessing(),
            new cn.stars.reversal.module.impl.client.Chat(),
            new HurtCam(),
            new Hotbar(),
            new NameTag(),
            new Optimization(),
            new IRC(),
    };

    public static int CLIENT_THEME_COLOR = new Color(159, 24, 242).hashCode();
    public static int CLIENT_THEME_COLOR_2 = new Color(159, 24, 242).hashCode();
    public static int CLIENT_THEME_COLOR_BRIGHT = new Color(185, 69, 255).hashCode();
    public static int CLIENT_THEME_COLOR_BRIGHT_2 = new Color(185, 69, 255).hashCode();

}