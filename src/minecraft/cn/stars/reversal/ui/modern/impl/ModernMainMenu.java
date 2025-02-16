package cn.stars.reversal.ui.modern.impl;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.atmoic.island.Atomic;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.ui.gui.GuiMicrosoftLoginPending;
import cn.stars.reversal.ui.modern.MenuButton;
import cn.stars.reversal.ui.notification.NotificationManager;
import cn.stars.reversal.util.animation.advanced.composed.ColorAnimation;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.reversal.Branch;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

@NativeObfuscation
public class ModernMainMenu extends GuiScreen implements GameInstance {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Animation updateLogAnimationX = new Animation(Easing.EASE_OUT_EXPO, 500);
    private final Animation updateLogAnimationY = new Animation(Easing.EASE_OUT_EXPO, 500);

    private final Animation loginAnimationX = new Animation(Easing.EASE_OUT_EXPO, 500);
    private final Animation settingsAnimationX = new Animation(Easing.EASE_OUT_EXPO, 500);
    private final Animation backgroundIdAnimationX = new Animation(Easing.EASE_OUT_EXPO, 500);
    private final Animation exitAnimationX = new Animation(Easing.EASE_OUT_EXPO, 500);

    private Animation textAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final ColorAnimation colorAnimation = new ColorAnimation(new Color(220,220,220,220), new Color(120,120,120,220), 2000);
    private String title = "";

    private TextButton singlePlayerButton, multiPlayerButton, settingsButton, viaVersionButton, exitButton, cbButton, updateLogButton, loginButton, atomicButton;
    private TextButton[] buttons;
    private boolean showUpdateLog = false;
    private final ArrayList<String> updateLog = new ArrayList<>();


    @SneakyThrows
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // background
        drawDefaultBackground();

        updatePostProcessing(true, partialTicks);

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        // 动画
        loginAnimationX.run(RenderUtil.isHovered(this.width - 60, this.height - 190, 35, 35, mouseX, mouseY) ? 100 : 0);
        settingsAnimationX.run(RenderUtil.isHovered(this.width - 60, this.height - 145, 35, 35, mouseX, mouseY) ? 100 : 0);
        backgroundIdAnimationX.run(RenderUtil.isHovered(this.width - 60, this.height - 100, 35, 35, mouseX, mouseY) ? 100 : 0);
        exitAnimationX.run(RenderUtil.isHovered(this.width - 60, this.height - 55, 35, 35, mouseX, mouseY) ? 100 : 0);

        updateLogAnimationX.run(showUpdateLog ? 270 : 0);
        updateLogAnimationY.run(showUpdateLog ? 400 : 0);

        RenderUtil.roundedRectangle(this.width - 25 - updateLogAnimationX.getValue(), 10, updateLogAnimationX.getValue(), updateLogAnimationY.getValue(), 4, new Color(30, 30, 30, 200));
        RenderUtil.roundedRectangle(this.width - 60 - loginAnimationX.getValue(), this.height - 190, loginAnimationX.getValue(), 35, 4, new Color(30, 30, 30, 200));
        RenderUtil.roundedRectangle(this.width - 60 - settingsAnimationX.getValue(), this.height - 145, settingsAnimationX.getValue(), 35, 4, new Color(30, 30, 30, 200));
        RenderUtil.roundedRectangle(this.width - 60 - backgroundIdAnimationX.getValue(), this.height - 100, backgroundIdAnimationX.getValue(), 35, 4, new Color(30, 30, 30, 200));
        RenderUtil.roundedRectangle(this.width - 60 - exitAnimationX.getValue(), this.height - 55, exitAnimationX.getValue(), 35, 4, new Color(30, 30, 30, 200));

        NORMAL_BLUR_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(this.width - 60 - loginAnimationX.getValue(), this.height - 190, loginAnimationX.getValue(), 35, 4, Color.BLACK);
            RenderUtil.roundedRectangle(this.width - 25 - updateLogAnimationX.getValue(), 10, updateLogAnimationX.getValue(), updateLogAnimationY.getValue(), 4, Color.BLACK);
            RenderUtil.roundedRectangle(this.width - 60 - settingsAnimationX.getValue(), this.height - 145, settingsAnimationX.getValue(), 35, 4, Color.BLACK);
            RenderUtil.roundedRectangle(this.width - 60 - backgroundIdAnimationX.getValue(), this.height - 100, backgroundIdAnimationX.getValue(), 35, 4, Color.BLACK);
            RenderUtil.roundedRectangle(this.width - 60 - exitAnimationX.getValue(), this.height - 55, exitAnimationX.getValue(), 35, 4, Color.BLACK);
        });

        // 动画
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(this.width - 60 - loginAnimationX.getValue(), this.height - 190, loginAnimationX.getValue(), 35);
        regular32.drawString("微软登录", this.width - 142, this.height - 178, new Color(220, 220, 220, 240).getRGB());

        RenderUtil.scissor(this.width - 60 - settingsAnimationX.getValue(), this.height - 145, settingsAnimationX.getValue(), 35);
        regular32.drawString("游戏设置", this.width - 142, this.height - 133, new Color(220, 220, 220, 240).getRGB());

        RenderUtil.scissor(this.width - 60 - backgroundIdAnimationX.getValue(), this.height - 97, backgroundIdAnimationX.getValue(), 35);
        regular32.drawString("更换背景", this.width - 142, this.height - 91, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("(当前ID: " + RainyAPI.backgroundId + ")", this.width - 132, this.height - 75, new Color(220, 220, 220, 240).getRGB());

        RenderUtil.scissor(this.width - 60 - exitAnimationX.getValue(), this.height - 55, exitAnimationX.getValue(), 35);
        regular32.drawString("退出游戏", this.width - 142, this.height - 43, new Color(220, 220, 220, 240).getRGB());

        // 更新日志
        //    RenderUtil.rect(4, 41, updateLogAnimationX.getValue(), 0.5, new Color(220, 220, 220, 240));
        RenderUtil.scissor(this.width - 25 - updateLogAnimationX.getValue(), 10, updateLogAnimationX.getValue(), updateLogAnimationY.getValue());
        int y = 55;
        for (String s : updateLog) {
            regular20.drawString(s, this.width - 290, y, new Color(220, 220, 220, 240).getRGB());
            y += 10;
        }
        regular32.drawString("Reversal", this.width - 280, 14, new Color(220, 220, 220, 240).getRGB());
        regular32.drawString("Announcements™", this.width - 250, 31, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString(Reversal.VERSION + " " + Branch.getBranchName(Reversal.BRANCH), this.width - 290, 400, new Color(220, 220, 220, 240).getRGB());
        RenderUtil.image(new ResourceLocation("reversal/images/1.png"), this.width - 280, 70, 240, 160);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // MainMenu
        RenderUtil.rect(0, 0, 230, height, new Color(0, 0, 0, 50));
        RenderUtil.rect(230, 0, 0.5, height, new Color(220, 220, 220, 240));
        if (RainyAPI.mainMenuDate) {
            FontManager.getRainbowParty(96).drawCenteredString(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH), width / 2f, 20, new Color(250,250,250,250).getRGB());
            FontManager.getRainbowParty(48).drawCenteredString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")), width / 2f, 72, new Color(250,250,250,250).getRGB());
        }
        FontManager.getRegular(56).drawCenteredString(Reversal.NAME.toUpperCase(), 100, height / 6f + 18, new Color(250,250,250,250).getRGB());
        FontManager.getRegular(32).drawCenteredString(Reversal.VERSION, 170, height / 6f + 42, new Color(220,220,220,150).getRGB());

        regular18.drawCenteredString("© 2025 Reversal, 保留部分权利.", this.width / 2f, this.height - 10, colorAnimation.getOutput().getRGB());
        if (RainyAPI.isShaderCompatibility) regular18.drawCenteredString("警告: 配置已开启DisableShader选项！你将只能使用ID 9作为背景！", this.width / 2f, this.height - 30, new Color(220, 20, 20, 220).getRGB());

        //    regular.drawCenteredString(tipString, width / 2f, height / 2f + 100,
        //            ColorUtil.withAlpha(new Color(250, 250, 250, 250), (int) fontAnimation.getValue()).getRGB());
        if (colorAnimation.isFinished()) colorAnimation.changeDirection();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        textAnimation.run(1);
        RenderUtil.scissor(this.width / 2f - 1, this.height - 30, regular18.width(title) / 2f * textAnimation.getValue() + 2, 20);
        regular18.drawCenteredString(title, this.width / 2f, this.height - 20, colorAnimation.getOutput().getRGB());
        RenderUtil.scissor(this.width / 2f - regular18.width(title) / 2f * textAnimation.getValue() + 1, this.height - 30, regular18.width(title) / 2f * textAnimation.getValue(), 20);
        regular18.drawCenteredString(title, this.width / 2f, this.height - 20, colorAnimation.getOutput().getRGB());
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        regular18.drawString(">", this.width / 2f + regular18.width(title) / 2f * textAnimation.getValue() + 10, this.height - 20, colorAnimation.getOutput().getRGB());
        regular18.drawString("<", this.width / 2f - regular18.width(title) / 2f * textAnimation.getValue() - 10, this.height - 20, colorAnimation.getOutput().getRGB());

        NotificationManager.onRender2D();
        Atomic.INSTANCE.render(new ScaledResolution(mc));

        updatePostProcessing(false, partialTicks);
    }

    @Override
    public void initGui() {
        title = RainyAPI.getRandomTitle();
        textAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);

        updateLog.clear();
        updateLog.add("蛇年快乐!");

        // 定义按钮
        this.singlePlayerButton = new TextButton(45, this.height / 6f + 80, 120, 35, () -> mc.displayGuiScreen(new GuiSelectWorld(this)),
                "单人游戏", "a", true, 12, 40, 11);
        this.multiPlayerButton = new TextButton(45, this.height / 6f + 125, 120, 35, () -> mc.displayGuiScreen(new GuiMultiplayer(this)),
                "多人游戏", "b", true, 9, 40, 11);
        this.viaVersionButton = new TextButton(45, this.height / 6f + 170, 120, 35, () -> mc.displayGuiScreen(new ModernViaMenu(this)),
                "跨版本", "d", true, 10, 44, 11);
        this.atomicButton = new TextButton(45, this.height / 6f + 215, 120, 35, () -> mc.displayGuiScreen(Reversal.atomicMenu),
                "AtomicGui", "", true, 10, 30, 11);
        this.loginButton = new TextButton(this.width - 60, this.height - 190, 35, 35, () -> mc.displayGuiScreen(new GuiMicrosoftLoginPending(this)),
                "", "9", true, 9, 50, 11);
        this.settingsButton = new TextButton(this.width - 60, this.height - 145, 35, 35, () -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings)),
                "", "e", true, 11, 50, 11);
        this.cbButton = new TextButton(this.width - 60, this.height - 100, 35, 35, () -> changeMenuBackground(false),
                "", "c", true, 9, 0, 11);
        this.exitButton = new TextButton(this.width - 60, this.height - 55, 35, 35, mc::shutdown,
                "", "g", true, 9, 0, 11);

        this.updateLogButton = new TextButton(this.width - 60, 10, 35, 35, () -> showUpdateLog = !showUpdateLog,
                "", "f", true, 9, 0, 11);

        // 简化MouseClicked方法
        this.buttons = new TextButton[] {this.singlePlayerButton, this.multiPlayerButton, this.settingsButton, this.viaVersionButton, this.exitButton, this.cbButton, this.updateLogButton, this.loginButton, this.atomicButton} ;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.buttons == null) return;

        // 执行按钮点击
        if (mouseButton == 0) {
            for (TextButton menuButton : this.buttons) {
                if (RenderUtil.isHovered(menuButton.getX(), menuButton.getY(), menuButton.getWidth(), menuButton.getHeight(), mouseX, mouseY)) {
                    mc.getSoundHandler().playButtonPress();
                    menuButton.runAction();
                    break;
                }
            }
        }

        if (mouseButton == 1) {
            for (MenuButton menuButton : this.buttons) {
                if (menuButton == cbButton && RenderUtil.isHovered(menuButton.getX(), menuButton.getY(), menuButton.getWidth(), menuButton.getHeight(), mouseX, mouseY)) {
                    mc.getSoundHandler().playButtonPress();
                    changeMenuBackground(true);
                    break;
                }
            }
        }
    }
}
