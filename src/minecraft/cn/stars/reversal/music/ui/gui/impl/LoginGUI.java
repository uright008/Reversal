package cn.stars.reversal.music.ui.gui.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.music.MusicUtil;
import cn.stars.reversal.music.api.MusicAPI;
import cn.stars.reversal.music.api.user.QRCode;
import cn.stars.reversal.music.api.user.QRCodeState;
import cn.stars.reversal.music.api.user.ScanResult;
import cn.stars.reversal.music.api.user.User;
import cn.stars.reversal.music.thread.GetPlayListsThread;
import cn.stars.reversal.music.thread.LoginThread;
import cn.stars.reversal.music.ui.ThemeColor;
import cn.stars.reversal.music.ui.gui.MusicPlayerGUI;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2024/8/13
 **/
public class LoginGUI extends MusicPlayerGUI {
    private final float topWidth;

    private LoginThread thread;
    private DynamicTexture qrCodeTexture;

    public LoginGUI() {
        super(Reversal.musicManager.screen.getCurrentGUI());
        width = 100f;
        height = 150f;
        topWidth = 50f;
        ScaledResolution sr = new ScaledResolution(mc);
        posX = sr.getScaledWidth() / 2f - width / 2f;
        posY = sr.getScaledHeight() / 2f - height / 2f;

        thread = new LoginThread();
        thread.start();
    }

    /**
     * @return 是否扫描成功
     */
    @Override
    public boolean draw(float x, float y, int mouseX, int mouseY, float cx, float cy, float scale) {
        // 一些api
        ScanResult result = thread.getResult();
        QRCodeState state = result == null ? QRCodeState.WAITING_SCAN : result.getState();
        JsonObject response = result == null ? new JsonObject() : result.getResponse();
        QRCode qrCode = thread.getQrCode();
        User user = MusicAPI.user;
        boolean waitingConfirm = state == QRCodeState.WAITING_CONFIRM;

        String text = waitingConfirm ? "请在手机上确认登录" : "请扫描二维码登录";
        float textY = waitingConfirm ? posY + 35f : posY + 15f;

        RoundedUtil.drawRoundOutline(posX, posY, width, height, 3f, 0.2f, ThemeColor.bgColor, ThemeColor.outlineColor);
        regular18.drawCenteredString(text, posX + width / 2f, textY, Color.WHITE.getRGB());

        // 如果已经扫描，就把头像和昵称画上去
        if (waitingConfirm) {
            // 设置缓存头像和昵称
            if (user.getTempNickname() == null) user.setTempNickname(response.get("nickname").getAsString());
            if (user.getTempAvatarTexture() == null)
                user.setTempAvatarTexture(new DynamicTexture(MusicUtil.downloadImage(response.get("avatarUrl").getAsString(), 200, 200)));

            RenderUtil.image(user.getTempAvatarTexture(), posX + 13f, posY + 10f, 16, 16);
            regular18.drawString(user.getTempNickname(), posX + width / 2f - 13f, posY + 18f, Color.WHITE.getRGB());
        }

        // 如果二维码过期，就另起一个进程
        if (state == QRCodeState.EXPIRED) {
            thread = new LoginThread();
        }

        // 把二维码画上去，如果还没获取到二维码就不画
        if (qrCode == null) {
            return false;
        }
        if (qrCodeTexture == null) qrCodeTexture = new DynamicTexture(qrCode.getImage());

        RenderUtil.image(qrCodeTexture, posX, posY + topWidth, width, height - topWidth);

        // 如果获取成功，保存用户cookie
        if (state == QRCodeState.SUCCEED) {
            user.setCookie(response.get("cookie").getAsString());
            MusicAPI.updateUserInfo();
            user.setLoggedIn(true);
            // 另起一个线程，获取用户歌单列表
            Reversal.threadPoolExecutor.submit(new GetPlayListsThread());
        }

        return state == QRCodeState.SUCCEED;
    }
}
