package cn.stars.reversal.music.ui.component.impl;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.music.thread.ChangeMusicThread;
import cn.stars.reversal.music.ui.ThemeColor;
import cn.stars.reversal.music.ui.component.Button;
import cn.stars.reversal.music.api.base.Music;
import cn.stars.reversal.music.api.base.PlayList;
import cn.stars.reversal.util.math.TimerUtil;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import net.minecraft.client.renderer.texture.DynamicTexture;

import javax.imageio.ImageIO;
import java.awt.*;

/**
 * @author ChengFeng
 * @since 2024/8/13
 **/
public class MusicButton extends Button implements GameInstance {
    private final Music music;
    private final int index;
    private final PlayList playList;

    public DynamicTexture texture;

    public MusicButton(Music music, int index, PlayList playList) {
        this.music = music;
        this.index = index;
        this.playList = playList;
    }

    @Override
    public void draw() {
        height = 30f;
        if (hovering) {
            RoundedUtil.drawRound(posX, posY, width - 1f, height, 0f, ColorUtil.applyOpacity(ThemeColor.greyColor, 0.1f));
        }

        float textX = posX + 7f;
        float textY = posY + 7f;
        regular16.drawString(index + "", textX, textY + 6f, ThemeColor.greyColor.getRGB());
        if (texture == null) {
            try {
                texture = new DynamicTexture(ImageIO.read(music.getCoverImage()));
            } catch (Exception e) {
            }
        }
        try {
            if (texture != null) {
                RenderUtil.image(texture, textX + 10f, textY, 16f, 16f);
            }

            regular16.drawString(regular16.trimStringToWidth(music.getName(), 124f, false, true), textX + 30f, textY + 2f, music.isFree() ? Color.WHITE.getRGB() : ThemeColor.redColor.getRGB());
            regular16.drawString(regular16.trimStringToWidth(music.getArtist(), 124f, false, true), textX + 30f, textY + 11f, ThemeColor.greyColor.getRGB());
        } catch (Exception e) {

        }

    //    Gui.drawRect(textX + 150f, textY, 0.5f, 20f, ColorUtil.applyOpacity(ThemeColor.greyColor, 0.2f).getRGB());

        regular16.drawString(regular16.trimStringToWidth(music.getAlbum_name(), 114f, false, true), textX + 160f, textY + 7f, ThemeColor.greyColor.getRGB());

        long totalSeconds = music.getDuration() / 1000;

        // 计算分钟和剩余秒数
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        String timeFormatted = String.format("%d:%02d", minutes, seconds);
    //    Gui.drawRect(textX + 270f, textY, 0.5f, 20f, ColorUtil.applyOpacity(ThemeColor.greyColor, 0.2f).getRGB());
        regular16.drawString(timeFormatted, textX + 280f, textY + 7f, ThemeColor.greyColor.getRGB());
    }

    private TimerUtil clickTimer = new TimerUtil();

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovering && button == 0) {
            if (!clickTimer.hasTimeElapsed(1500)) {
                Reversal.threadPoolExecutor.submit(new ChangeMusicThread(music, playList));
            }
            clickTimer.reset();
        }
    }
}
