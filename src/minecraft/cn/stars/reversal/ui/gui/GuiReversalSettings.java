/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.ui.gui;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

import static cn.stars.reversal.GameInstance.*;

public class GuiReversalSettings extends GuiScreen {
    public GuiScreen parent;
    private TextButton exitButton, shaderButton, viaButton, mainMenuDateButton, guiSnowButton, backgroundBlurButton, imageScreenButton;
    private TextButton[] buttons;

    public GuiReversalSettings(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        updatePostProcessing(true, partialTicks);

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        RoundedUtil.drawRound(width / 2f - 500, 10, 1000, height - 15, 4, new Color(30, 30, 30, 160));
        RenderUtil.rect(width / 2f - 500, 30, 1000, 0.5, new Color(220, 220, 220, 240));
        NORMAL_BLUR_RUNNABLES.add(() -> RoundedUtil.drawRound(width / 2f - 500, 10, 1000, height - 15, 4, Color.BLACK));
        regular24Bold.drawCenteredString("Reversal设置 (按钮显示当前状态)", width / 2f, 16, new Color(220, 220, 220, 240).getRGB());

        // Shader
        regular20Bold.drawString("Background Shader", width / 2f - 490, 40, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("开启这个选项后，你将可以在主菜单使用Shader背景。\n部分电脑不支持并会导致崩溃，如果崩溃请关闭。", width / 2f - 490, 55, new Color(220, 220, 220, 240).getRGB());

        // Via
        regular20Bold.drawString("Via Version", width / 2f - 490, 110, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("开启这个选项后，将允许客户端进行跨版本。\n如果你的客户端偶现无法加载，可以尝试关闭。", width / 2f - 490, 125, new Color(220, 220, 220, 240).getRGB());

        // MainMenu Date
        regular20Bold.drawString("Main Menu Date", width / 2f - 490, 180, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("使你的主菜单最上方增加日期显示。", width / 2f - 490, 195, new Color(220, 220, 220, 240).getRGB());

        // Gui Snow
        regular20Bold.drawString("Gui Snow", width / 2f - 490, 250, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("使你的界面有类似下雪的效果。", width / 2f - 490, 265, new Color(220, 220, 220, 240).getRGB());

        // Background Blur
        regular20Bold.drawString("Background Blur", width / 2f - 490, 320, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("使你的背景有模糊效果。", width / 2f - 490, 335, new Color(220, 220, 220, 240).getRGB());

        // Image Screen
        regular20Bold.drawString("Image Screen", width / 2f - 490, 390, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("在游戏窗口出现时显示一个Hoshino Shield的界面。\n纯属整活。（不会拖慢加载速度）", width / 2f - 490, 405, new Color(220, 220, 220, 240).getRGB());

        updatePostProcessing(false, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        this.exitButton = new TextButton(width / 2f - 60, height - 60, 120, 35, () -> mc.displayGuiScreen(parent),
                "返回主菜单", "g", true, 12, 38, 11);

        createButton();

        buttons = new TextButton[]{exitButton, shaderButton, viaButton, mainMenuDateButton, guiSnowButton, backgroundBlurButton, imageScreenButton};
    }

    private void switchOption(Runnable runnable) {
        runnable.run();
        createButton();
        RainyAPI.processAPI(true);
        buttons = new TextButton[]{exitButton, shaderButton, viaButton, mainMenuDateButton, guiSnowButton, backgroundBlurButton, imageScreenButton};
    }

    private void createButton() {
        if (!RainyAPI.isShaderCompatibility) {
            this.shaderButton = new TextButton(width / 2f - 490, 75, 60, 25, () -> switchOption(() -> RainyAPI.isShaderCompatibility = !RainyAPI.isShaderCompatibility),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.shaderButton = new TextButton(width / 2f - 490, 75, 60, 25, () -> switchOption(() -> RainyAPI.isShaderCompatibility = !RainyAPI.isShaderCompatibility),
                    "关", "0", true, 10, 34, 7);
        }
        if (!RainyAPI.isViaCompatibility) {
            this.viaButton = new TextButton(width / 2f - 490, 145, 60, 25, () -> switchOption(() -> RainyAPI.isViaCompatibility = !RainyAPI.isViaCompatibility),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.viaButton = new TextButton(width / 2f - 490, 145, 60, 25, () -> switchOption(() -> RainyAPI.isViaCompatibility = !RainyAPI.isViaCompatibility),
                    "关", "0", true, 10, 34, 7);
        }
        if (RainyAPI.mainMenuDate) {
            this.mainMenuDateButton = new TextButton(width / 2f - 490, 215, 60, 25, () -> switchOption(() -> RainyAPI.mainMenuDate = !RainyAPI.mainMenuDate),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.mainMenuDateButton = new TextButton(width / 2f - 490, 215, 60, 25, () -> switchOption(() -> RainyAPI.mainMenuDate = !RainyAPI.mainMenuDate),
                    "关", "0", true, 10, 34, 7);
        }
        if (RainyAPI.guiSnow) {
            this.guiSnowButton = new TextButton(width / 2f - 490, 285, 60, 25, () -> switchOption(() -> RainyAPI.guiSnow = !RainyAPI.guiSnow),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.guiSnowButton = new TextButton(width / 2f - 490, 285, 60, 25, () -> switchOption(() -> RainyAPI.guiSnow = !RainyAPI.guiSnow),
                    "关", "0", true, 10, 34, 7);
        }
        if (RainyAPI.backgroundBlur) {
            this.backgroundBlurButton = new TextButton(width / 2f - 490, 355, 60, 25, () -> switchOption(() -> RainyAPI.backgroundBlur = !RainyAPI.backgroundBlur),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.backgroundBlurButton = new TextButton(width / 2f - 490, 355, 60, 25, () -> switchOption(() -> RainyAPI.backgroundBlur = !RainyAPI.backgroundBlur),
                    "关", "0", true, 10, 34, 7);
        }
        if (RainyAPI.imageScreen) {
            this.imageScreenButton = new TextButton(width / 2f - 490, 425, 60, 25, () -> switchOption(() -> RainyAPI.imageScreen = !RainyAPI.imageScreen),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.imageScreenButton = new TextButton(width / 2f - 490, 425, 60, 25, () -> switchOption(() -> RainyAPI.imageScreen = !RainyAPI.imageScreen),
                    "关", "0", true, 10, 34, 7);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
    }
}
