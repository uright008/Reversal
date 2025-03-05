/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025Aerolite Society, All rights reserved.
 */
package cn.stars.reversal.music;

import cn.stars.reversal.music.api.MusicAPI;
import cn.stars.reversal.util.ReversalLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MusicUtil {
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static BufferedImage downloadImage(String imageUrl, int width, int height) {
        return downloadImage(imageUrl, width, height, 0);
    }

    private static BufferedImage downloadImage(String imageUrl, int width, int height, int count) {
        ReversalLogger.info("[MusicPlayer] Download: " + imageUrl);
        try {
            URL url = new URL(imageUrl);
            try (InputStream inputStream = url.openStream()) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    return resizeImage(ImageIO.read(new URL(MusicAPI.user.getAvatarUrl()).openStream()), width, height);
                }
                return resizeImage(image, width, height);
            }
        } catch (OutOfMemoryError ee) {
            ReversalLogger.error("[MusicPlayer] Out of memory! Failed to download, retrying...");
            System.gc();
            return downloadImage(imageUrl, width, height, count + 1);
        } catch (IOException e) {
            if (count >= 4) {
                throw new NullPointerException("Download failed after five tries.");
            }
            ReversalLogger.error("[MusicPlayer] Failed to download, retrying...");
            return downloadImage(imageUrl, width, height, count + 1);
        }
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
        // 创建一个新的缓冲图像
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        // 获取 Graphics2D 对象
        Graphics2D g2d = resizedImage.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 绘制缩小后的图像
        g2d.drawImage(originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }
}
