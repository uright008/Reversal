package cn.stars.reversal.music.ui;

import cn.stars.reversal.music.api.base.Music;
import cn.stars.reversal.music.ui.component.Button;
import cn.stars.reversal.GameInstance;
import cn.stars.reversal.music.api.player.MusicPlayer;
import cn.stars.reversal.music.thread.SearchMusicThread;
import cn.stars.reversal.music.ui.component.impl.CategoryButton;
import cn.stars.reversal.music.ui.component.impl.IconButton;
import cn.stars.reversal.music.ui.component.impl.UserButton;
import cn.stars.reversal.music.ui.component.slider.PlayerSlider;
import cn.stars.reversal.music.ui.component.slider.VolumeSlider;
import cn.stars.reversal.music.ui.gui.MusicPlayerGUI;
import cn.stars.reversal.music.ui.gui.impl.PlayListGUI;
import cn.stars.reversal.music.ui.gui.impl.PlayListListGUI;
import cn.stars.reversal.ui.modern.TextField;
import cn.stars.reversal.util.animation.advanced.Animation;
import cn.stars.reversal.util.animation.advanced.Direction;
import cn.stars.reversal.util.animation.advanced.impl.DecelerateAnimation;
import cn.stars.reversal.util.math.TimerUtil;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChengFeng
 * @since 2024/8/12
 **/
public class MusicPlayerScreen extends GuiScreen {
    // Coords
    private float x;
    private float y;
    public static float width, height, topWidth, leftWidth, bottomWidth;
    private boolean dragging;
    private float dragX, dragY;

    // Animation
    private Animation windowAnim;

    // Current Page
    public final List<CategoryButton> categoryButtons = new ArrayList<>();
    private final UserButton userButton = new UserButton();
    @Getter
    private final cn.stars.reversal.ui.modern.TextField searchField = new TextField(150, 10, GameInstance.regular16, ThemeColor.bgColor, ThemeColor.outlineColor);
    @Getter
    private MusicPlayerGUI currentGUI;

    // Thread
    private SearchMusicThread currentThread;

    // Player
    public final MusicPlayer player = new MusicPlayer();

    private final IconButton playBtn = new IconButton("play.png", player::play);
    private final IconButton pauseBtn = new IconButton("pause.png", player::pause);
    private final IconButton preBtn = new IconButton("previous.png", player::previous);
    private final IconButton nextBtn = new IconButton("next.png", player::next);
    private final PlayerSlider playerSlider = new PlayerSlider();
    private final VolumeSlider volumeSlider = new VolumeSlider();

    private final TimerUtil timeUtil = new TimerUtil();

    public MusicPlayerScreen() {
        x = 10f;
        y = 10f;
        width = 470f;
        height = 310f;
        topWidth = 30f;
        leftWidth = 100f;
        bottomWidth = 35f;

        categoryButtons.add(new CategoryButton("为我推荐", MusicCategory.RECOMMENDED, new PlayListListGUI()));
        categoryButtons.add(new CategoryButton("我喜欢的音乐", MusicCategory.LIKED, new PlayListGUI(categoryButtons.get(0).getGui())));

        currentGUI = categoryButtons.get(0).getGui();
        categoryButtons.get(0).setSelected(true);
    }

    public void setCurrentGUI(MusicPlayerGUI newGUI) {
        currentGUI.freeMemory();
        currentGUI = newGUI;
    }

    @Override
    public void initGui() {
        windowAnim = new DecelerateAnimation(100, 1d);
    }

    float coverAngle = 0f;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (windowAnim.finished(Direction.BACKWARDS)) mc.displayGuiScreen(null);

        // Drag
        if (dragging) {
            x += mouseX - dragX;
            y += mouseY - dragY;
            dragX = mouseX;
            dragY = mouseY;
        }

        // 碰撞检测
        ScaledResolution sr = new ScaledResolution(mc);
        if (x < 10) x = 10;
        if (y < 10) y = 10;
        if (x + width > sr.getScaledWidth() - 10) x = sr.getScaledWidth() - 10 - width;
        if (y + height > sr.getScaledHeight() - 10) y = sr.getScaledHeight() - 10 - height;

        RenderUtil.scaleStart(x + width / 2, y + height / 2, (float) windowAnim.getOutput());
        RoundedUtil.drawRoundOutline(x, y, width, height, 3f, 0.2f, ThemeColor.bgColor, ThemeColor.outlineColor);
        RoundedUtil.drawRound(x + 2f, y + 2f, leftWidth - 2f, height - 4f, 2.6f, ThemeColor.categoryColor);

    //    NanoUtil.beginFrame();
    //    NanoUtil.scaleStart(0, 0, sr.getScaleFactor() * 0.5f);
    //    NanoUtil.scaleStart(x + width / 2, y + height / 2, windowAnim.getOutput().floatValue());

        RenderUtil.image(new ResourceLocation("reversal/images/music/netease.png"), x + 12f, y + 10f, 16, 16);
        GameInstance.regular20Bold.drawString("网易云音乐", x + 32f, y + 15f, Color.WHITE.getRGB());

        float btnX = x + leftWidth / 2f - categoryButtons.get(0).width / 2f;
        float btnY = y + 40f;
        for (Button btn : categoryButtons) {
            btn.updateState(btnX, btnY, mouseX, mouseY);
            btn.draw();
            btnY += btn.height + 8f;
        }

        userButton.updateState(x + width - userButton.width - 10f, y + 10f - userButton.height / 2f, mouseX, mouseY);
        userButton.draw();

        searchField.height = 15f;
        searchField.draw(x + leftWidth + 30f, y + 6f, mouseX, mouseY);

        RoundedUtil.drawRound(x + 2f, y + height - bottomWidth - 2f, width - 4f, bottomWidth, 2.6f, ThemeColor.playerColor);

        currentGUI.setWidth(width - leftWidth);
        currentGUI.setHeight(height - topWidth - bottomWidth);
        currentGUI.draw(x + leftWidth, y + topWidth, mouseX, mouseY, x + width / 2f, y + width / 2f, (float) windowAnim.getOutput());

        if (RenderUtil.isHovered(x + leftWidth, y + topWidth, width - leftWidth, height - topWidth - bottomWidth, mouseX, mouseY)) {
            currentGUI.handleScroll();
        }

        if (currentGUI.parent != null) {
            RenderUtil.image(new ResourceLocation("reversal/images/music/arrow-left.png"), x + leftWidth + 5f, y + 6f, 16f, 16f,
                    RenderUtil.isHovered(x + leftWidth + 5f, y + 7f, 16f, 16f, mouseX, mouseY) ? Color.WHITE : ThemeColor.greyColor);
        }

        Music music = player.getMusic();
        if (music != null && player.getMediaPlayer() != null) {
            if (music.getTexture() == null) {
                try {
                    music.setTexture(new DynamicTexture(ImageIO.read(music.getCoverImage())));
                } catch (Exception e) {
                }
            }
            float playerY = y + (height - bottomWidth);
        //    RoundedUtil.drawRound(x + 19f, playerY + 6f, 20f, 20f, 10f, ThemeColor.bgColor);
            if (!player.isPaused()) {
                coverAngle = (float) (0.1 * timeUtil.getTime());
            }
            if (music.getTexture() != null) {
                GL11.glPushMatrix();
                GlStateManager.bindTexture(music.getTexture().getGlTextureId());
                GL11.glTranslatef(x + 25f, playerY + 16f, 0f);
                GL11.glRotatef(coverAngle, 0f, 0f, 1f);
                GL11.glTranslatef(-10f, -10f, 0f);
                RoundedUtil.drawRoundTextured(0f, 0f, 20f, 20f, 10f, 1f);
                GL11.glPopMatrix();
            } else {
                RoundedUtil.drawRound(x + 14f, playerY + 5f, 22f, 22f, 10f, ThemeColor.bgColor);
            }
            GameInstance.regular18.drawString(GameInstance.regular18.trimStringToWidth(music.getName(), 120, false, true), x + 45f, playerY + 8f, Color.WHITE.getRGB());
            GameInstance.regular16.drawString(music.getArtist(), x + 45f, playerY + 20f, ThemeColor.greyColor.getRGB());

            if (player.isPaused()) {
                playBtn.setSize(16);
                playBtn.setBg(true);
                playBtn.updateState(x + width / 2f - 10f, playerY + 4f, mouseX, mouseY);
                playBtn.draw();
            } else {
                pauseBtn.setSize(16);
                pauseBtn.setBg(true);
                pauseBtn.updateState(x + width / 2f - 10f, playerY + 4f, mouseX, mouseY);
                pauseBtn.draw();
            }

            preBtn.setSize(15);
            preBtn.updateState(x + width / 2f - 35f, playerY + 4f, mouseX, mouseY);
            preBtn.draw();

            nextBtn.setSize(15);
            nextBtn.updateState(x + width / 2f + 16f, playerY + 4f, mouseX, mouseY);
            nextBtn.draw();

            playerSlider.draw(x + width / 2f - 100f, playerY + 27f, mouseX, mouseY);
            volumeSlider.draw(x + width - 120f, playerY + 27f, mouseX, mouseY);
        }

        RenderUtil.scaleEnd();

        if (currentThread != null && !currentThread.isAlive()) {
            setCurrentGUI(currentThread.getGui());
            searchField.text = "";
            searchField.focused = false;
            currentThread = null;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE && windowAnim.getDirection() == Direction.FORWARDS) {
            windowAnim.changeDirection();
            Keyboard.enableRepeatEvents(false);
        }

        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER && !searchField.text.isEmpty() && (currentThread == null || !currentThread.isAlive())) {
            currentThread = new SearchMusicThread(this);
            currentThread.start();
        }

        searchField.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (RenderUtil.isHovered(x, y, width, topWidth, mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
            dragX = mouseX;
            dragY = mouseY;
        }

        if (RenderUtil.isHovered(x + leftWidth + 5f, y + 7f, 16f, 16f, mouseX, mouseY) && mouseButton == 0 && currentGUI.parent != null) {
            setCurrentGUI(currentGUI.parent);
        }

        if (RenderUtil.isHovered(x, y, leftWidth, height, mouseX, mouseY)) {
            CategoryButton selectedBtn = null;
            for (CategoryButton btn : categoryButtons) {
                if (btn.hovering && mouseButton == 0) {
                    btn.setSelected(true);
                    setCurrentGUI(btn.getGui());
                    selectedBtn = btn;
                }
            }

            // 如果点了空白地方就不要改了
            if (selectedBtn != null) {
                for (CategoryButton button : categoryButtons) {
                    if (!button.hovering) button.setSelected(false);
                }
            }
        }

        userButton.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);

        if (RenderUtil.isHovered(x + leftWidth, y + topWidth, width - leftWidth, height - topWidth - bottomWidth - 3f, mouseX, mouseY)) {
            currentGUI.mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (RenderUtil.isHovered(x, y + (height - bottomWidth), width, bottomWidth, mouseX, mouseY) && player.getMusic() != null) {
            if (player.isPaused()) {
                playBtn.mouseClicked(mouseX, mouseY, mouseButton);
            } else pauseBtn.mouseClicked(mouseX, mouseY, mouseButton);
            preBtn.mouseClicked(mouseX, mouseY, mouseButton);
            nextBtn.mouseClicked(mouseX, mouseY, mouseButton);
            playerSlider.mouseClicked(mouseX, mouseY, mouseButton);
            volumeSlider.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        searchField.mouseDragged(mouseX, mouseY, clickedMouseButton);
    }

    @Override
    public void onGuiClosed() {
        currentGUI.onGuiClosed();
        super.onGuiClosed();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        if (player.getMusic() != null) {
            playerSlider.mouseReleased();
            volumeSlider.mouseReleased();
        }
    }
}
