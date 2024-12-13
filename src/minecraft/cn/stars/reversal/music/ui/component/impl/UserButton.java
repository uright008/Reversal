package cn.stars.reversal.music.ui.component.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.config.MusicHandler;
import cn.stars.reversal.music.MusicUtil;
import cn.stars.reversal.music.api.MusicAPI;
import cn.stars.reversal.music.api.user.User;
import cn.stars.reversal.music.thread.GetPlayListsThread;
import cn.stars.reversal.music.ui.ThemeColor;
import cn.stars.reversal.music.ui.component.Button;
import cn.stars.reversal.music.ui.gui.impl.LoginGUI;
import cn.stars.reversal.util.render.RoundedUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2024/8/13
 **/
public class UserButton extends Button {
    private LoginGUI loginGUI;

    public UserButton() {
        height = 10f;
    }

    @Override
    public void draw() {
        User user = MusicAPI.user;
        String text = user.isLoggedIn() ? user.getNickname() : "未登录";
        width = 20f + regular16.getStringWidth(text);
        RoundedUtil.drawRound(posX + 20, posY + height / 2f, width - 20, height, 2f, ThemeColor.playerColor);

        if (user.isLoggedIn()) {
            if (user.getAvatarTexture() == null) {
                user.setAvatarTexture(new DynamicTexture(MusicUtil.downloadImage(user.getAvatarUrl(), 200, 200)));
            }
            GlStateManager.bindTexture(user.getAvatarTexture().getGlTextureId());
            RoundedUtil.drawRoundTextured(posX, posY + 4f, 12, 12, 5f, 1f);
        }
        regular16.drawString(text, posX + 20, posY + 2.5f + height / 2f, hovering ? Color.WHITE.getRGB() : ThemeColor.greyColor.getRGB());

        if (loginGUI != null) {
            if (loginGUI.draw(0, 0, 0, 0, 0, 0, 0) /*参数不重要*/) {
                loginGUI = null;
                MusicHandler.save();
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0 && hovering) {
            if (!MusicAPI.user.isLoggedIn()) loginGUI = new LoginGUI();
            else if (!MusicAPI.user.isLoaded()) Reversal.threadPoolExecutor.submit(new GetPlayListsThread());
        }
    }
}
