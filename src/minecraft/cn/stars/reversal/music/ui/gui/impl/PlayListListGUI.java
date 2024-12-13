package cn.stars.reversal.music.ui.gui.impl;

import cn.stars.reversal.music.api.MusicAPI;
import cn.stars.reversal.music.api.base.PlayList;
import cn.stars.reversal.music.ui.component.impl.PlayListButton;
import cn.stars.reversal.music.ui.gui.MusicPlayerGUI;
import cn.stars.reversal.util.render.RenderUtil;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * 虽然很奇怪，但是他就是叫“歌单”列表
 *
 * @author ChengFeng
 * @since 2024/8/13
 **/
@Getter
@Setter
public class PlayListListGUI extends MusicPlayerGUI {
    private List<PlayListButton> buttons = new ArrayList<>();

    public PlayListListGUI() {
        super(null);
    }

    @Override
    public boolean draw(float x, float y, int mouseX, int mouseY, float cx, float cy, float scale) {
        if (buttons.isEmpty()) {
            regular32.drawCenteredString(MusicAPI.user.isLoggedIn() ? "获取中" : "请先登录", x + width / 2f, y + 50f, Color.WHITE.getRGB());
            return false;
        }

        posX = x;
        posY = y + scrollAnim.getOutput().floatValue();

        float leftX = cx + (x - cx) * scale;
        float rightX = cx + ((x + width) - cx) * scale;
        float topY = cy + (y - cy) * scale;
        float bottomY = cy + ((y + height) - cy) * scale;

        float buttonY = posY;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(leftX, topY, rightX - leftX, Math.max(bottomY - topY - 3f, 0f));
        try {
            for (PlayListButton button : buttons) {
                // 懒加载
                if (buttonY < bottomY) {
                    button.width = this.width;
                    button.updateState(posX, buttonY, mouseX, mouseY);
                    button.draw();
                }
                buttonY += button.height + 10f;
            }
        } catch (ConcurrentModificationException e) {

        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        height = buttonY - posY;
        return true;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (PlayListButton button : buttons) {
            button.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void addPlayList(PlayList playList) {
        buttons.add(new PlayListButton(playList, this));
        System.out.println("Add " + playList.getName());
    }

    @Override
    public void freeMemory() {
        for (PlayListButton button : buttons) {
            if (button.coverTexture != null) {
                GL11.glDeleteTextures(button.coverTexture.getGlTextureId());
                button.coverTexture = null;
            }
        }
    }
}
