package cn.stars.reversal.music.ui.gui.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.music.api.base.Music;
import cn.stars.reversal.music.api.base.PlayList;
import cn.stars.reversal.music.thread.FetchPlayListThread;
import cn.stars.reversal.music.ui.MusicPlayerScreen;
import cn.stars.reversal.music.ui.ThemeColor;
import cn.stars.reversal.music.ui.component.impl.MusicButton;
import cn.stars.reversal.music.ui.gui.MusicPlayerGUI;
import cn.stars.reversal.util.render.RenderUtil;
import lombok.Setter;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是歌单界面
 *
 * @author ChengFeng
 * @since 2024/8/13
 **/
public class PlayListGUI extends MusicPlayerGUI {
    public final List<MusicButton> buttons = new ArrayList<>();
    @Setter
    private PlayList playList;
    private FetchPlayListThread currentLoadThread;
    private DynamicTexture texture;

    private boolean noMore = false;

    public PlayListGUI(PlayList playList, MusicPlayerGUI parent) {
        super(parent);
        this.playList = playList;
        int index = 1;
        for (Music music : playList.getMusicList()) {
            buttons.add(new MusicButton(music, index, playList));
            index++;
        }
    }

    public PlayListGUI(MusicPlayerGUI parent) {
        super(parent);
    }

    @Override
    public boolean draw(float x, float y, int mouseX, int mouseY, float cx, float cy, float scale) {
        if (texture == null) {
            try {
                texture = new DynamicTexture(ImageIO.read(playList.getCoverImage()));
            } catch (Exception e) {
            }
        }
        try {
            if (texture != null) {
                RenderUtil.image(texture, x + 10f, y + 10f, 50f, 50f);
            }
            regular20Bold.drawString(playList.getName(), x + 65f, y + 14f, Color.WHITE.getRGB());
            regular16.drawString(regular16.trimStringToWidth(playList.getDescription(), 300f, false, true), x + 65f, y + 34f, ThemeColor.greyColor.getRGB());
        } catch (Exception e) {
        }

        regular18.drawString("#", x + 7f, y + 75f, ThemeColor.greyColor.getRGB());
        regular18.drawString("歌曲", x + 15f, y + 75f, ThemeColor.greyColor.getRGB());
        regular18.drawString("专辑", x + 166f, y + 75f, ThemeColor.greyColor.getRGB());
        regular18.drawString("时长", x + 286f, y + 75f, ThemeColor.greyColor.getRGB());

        float buttonY = y + 90f;
        float leftX = cx + (x - cx) * scale;
        float rightX = cx + ((x + width) - cx) * scale;
        float topY = cy + (buttonY - cy) * scale;
        float bottomY = cy + ((buttonY + height) - cy) * scale;

        if (buttons.isEmpty()) {
            regular32.drawCenteredString("加载中", x + width / 2f, buttonY + 20f, Color.WHITE.getRGB());
            return false;
        }

        float realButtonY = buttonY + scrollAnim.getOutput().floatValue();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(leftX, topY, rightX - leftX, Math.max(bottomY - topY - 93f, 0f));
        for (MusicButton button : buttons) {
            if (realButtonY < bottomY) {
                button.width = width;
                button.updateState(x, realButtonY, mouseX, mouseY);
                if (!RenderUtil.isHovered(leftX, topY, rightX - leftX, bottomY - topY - 93f, mouseX, mouseY)) {
                    button.hovering = false;
                }
                button.draw();
            }
            realButtonY += button.height;
        }

        if (currentLoadThread != null) {
            if (currentLoadThread.isAlive()) {
                height += 20f;
                regular16.drawCenteredString("正在加载更多", x + width / 2f, realButtonY + 10f, ThemeColor.greyColor.getRGB());
            } else {
                buttons.addAll(currentLoadThread.getButtonsTemp());
                noMore = currentLoadThread.isNoMore();
                currentLoadThread = null;
            }
        }

        if (isBottom && currentLoadThread == null) {
            if (noMore) {
                height += 20f;
                regular16.drawCenteredString("已经到底了", x + width / 2f, realButtonY + 10f, ThemeColor.greyColor.getRGB());
            } else {
                if (playList.getId() == -1) {
                    noMore = true;
                } else {
                    currentLoadThread = new FetchPlayListThread(playList);
                    Reversal.threadPoolExecutor.execute(currentLoadThread);
                }
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        height = realButtonY - (buttonY + scrollAnim.getOutput().floatValue());

        return true;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (MusicButton button : buttons) {
            button.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void handleScroll() {
        // Scroll
        int wheel = Mouse.getDWheel() * 420;
        if (wheel != 0) {
            scrollAnim.setStartPoint(scrollAnim.getOutput());
            if (wheel > 0) {
                scrollAnim.setEndPoint(scrollAnim.getEndPoint() + 30f);
            } else {
                scrollAnim.setEndPoint(scrollAnim.getEndPoint() - 30f);
            }
            if (scrollAnim.getEndPoint() > 0) scrollAnim.setEndPoint(0f);
            float maxScroll = height + (noMore || (currentLoadThread != null && currentLoadThread.isAlive())? 30f : 0f) - (MusicPlayerScreen.height - MusicPlayerScreen.topWidth - MusicPlayerScreen.bottomWidth - 90f);
            if (-scrollAnim.getEndPoint() > maxScroll) {
                scrollAnim.setEndPoint(-maxScroll);
                isBottom = true;
            } else isBottom = false;
            scrollAnim.getAnimation().reset();
        }
    }

    @Override
    public void freeMemory() {
        for (MusicButton button : buttons) {
            if (button.texture != null) {
                GL11.glDeleteTextures(button.texture.getGlTextureId());
                button.texture = null;
            }
        }
    }
}
