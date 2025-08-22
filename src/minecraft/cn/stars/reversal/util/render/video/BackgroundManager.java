package cn.stars.reversal.util.render.video;

import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.misc.FileUtil;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;

import javax.imageio.ImageIO;
import java.io.File;

public class BackgroundManager {
    private static final File dictionary = new File(Minecraft.getMinecraft().mcDataDir, "Reversal/Background");
    private static final File backgroundImageFile = new File(dictionary, "background.png");
    private static final File backgroundVideoFile = new File(dictionary, "background.mp4");
    private static final File splashFile = new File(dictionary, "background.mp4");

    public static DynamicTexture backgroundImage = null;

    @SuppressWarnings("all")
    @SneakyThrows
    public static void loadFiles() {
        if (!dictionary.exists()) {
            dictionary.mkdirs();
        }
        if (!backgroundImageFile.exists()) {
            FileUtil.unpackFile(backgroundImageFile, "assets/minecraft/reversal/images/background.png");
        }

        if (!backgroundVideoFile.exists()) {
            FileUtil.unpackFile(backgroundVideoFile, "assets/minecraft/reversal/background.mp4");
        }

        if (!splashFile.exists()) {
            FileUtil.unpackFile(splashFile, "assets/minecraft/reversal/background.mp4");
        }
    }

    @SneakyThrows
    public static void loadSplash() {
        if (!splashFile.exists()) {
            ReversalLogger.error("Splash file not found, this should not happen! Reload files.");
            loadFiles();
        }
        VideoUtil.init(splashFile);
    }

    @SneakyThrows
    public static void loadBackground() {
        if (!backgroundVideoFile.exists() || !backgroundImageFile.exists()) {
            ReversalLogger.error("Background file not found, this should not happen! Reload files.");
            loadFiles();
        }
        VideoUtil.init(backgroundVideoFile);
        backgroundImage = new DynamicTexture(ImageIO.read(backgroundImageFile));
    }
}
