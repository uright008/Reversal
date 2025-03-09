/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.ui.atmoic.mainmenu.impl;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

public class ReversalSettingsGui extends AtomicGui {
    public GuiScreen parent;
    private TextButton exitButton, shaderButton, viaButton, mainMenuDateButton, guiSnowButton, backgroundBlurButton, imageScreenButton;
    private TextButton[] buttons;

    public ReversalSettingsGui() {
        super("Reversal Settings", "e");
    }

    @Override
    public void drawIcon(int posX, int posY, int color) {
        atomic24.drawString(icon, posX + 2, posY + 0.5, color);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(50, 65, width - 100, height - 85, 3, Color.BLACK);

            RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
            FontManager.getRainbowParty(48).drawString("reversal settings", 75, 35, Color.WHITE.getRGB());
        }, 2, 2);

        RoundedUtil.drawRound(50, 65, width - 100, height - 85, 3, new Color(20, 20, 20, 160));

        RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
        FontManager.getRainbowParty(48).drawString("reversal settings", 75, 35, Color.WHITE.getRGB());

        // Shader
        regular20Bold.drawString("Background Shader", 60, 80, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("开启这个选项后，你将可以在主菜单使用Shader背景。\n部分电脑不支持并会导致崩溃，如果崩溃请关闭。", 60, 95, new Color(220, 220, 220, 240).getRGB());

        // Via
        regular20Bold.drawString("Via Version", 60, 150, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("开启这个选项后，将允许客户端进行跨版本。\n如果你的客户端偶现无法加载，可以尝试关闭。", 60, 165, new Color(220, 220, 220, 240).getRGB());

        // MainMenu Date
        regular20Bold.drawString("Main Menu Date", 60, 220, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("使你的主菜单最上方增加日期显示。", 60, 235, new Color(220, 220, 220, 240).getRGB());

        // Gui Snow
        regular20Bold.drawString("Gui Snow", 60, 290, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("使你的界面有类似下雪的效果。", 60, 305, new Color(220, 220, 220, 240).getRGB());

        // Background Blur
        regular20Bold.drawString("Background Blur", 60, 360, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("使你的背景有模糊效果。", 60, 375, new Color(220, 220, 220, 240).getRGB());

        // Image Screen
        regular20Bold.drawString("Image Screen", 60, 430, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("在游戏窗口出现时显示一个Hoshino Shield的界面。\n纯属整活。（不会拖慢加载速度）", 60, 445, new Color(220, 220, 220, 240).getRGB());

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
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
        this.exitButton = new TextButton(width / 2f - 60, height - 60, 120, 35, () -> AtomicMenu.switchGui(0),
                "返回主菜单", "g", true, 12, 38, 11);
        if (!RainyAPI.isShaderCompatibility) {
            this.shaderButton = new TextButton(60, 115, 60, 25, () -> switchOption(() -> RainyAPI.isShaderCompatibility = !RainyAPI.isShaderCompatibility),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.shaderButton = new TextButton(60, 115, 60, 25, () -> switchOption(() -> RainyAPI.isShaderCompatibility = !RainyAPI.isShaderCompatibility),
                    "关", "0", true, 10, 34, 7);
        }
        if (!RainyAPI.isViaCompatibility) {
            this.viaButton = new TextButton(60, 185, 60, 25, () -> switchOption(() -> RainyAPI.isViaCompatibility = !RainyAPI.isViaCompatibility),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.viaButton = new TextButton(60, 185, 60, 25, () -> switchOption(() -> RainyAPI.isViaCompatibility = !RainyAPI.isViaCompatibility),
                    "关", "0", true, 10, 34, 7);
        }
        if (RainyAPI.mainMenuDate) {
            this.mainMenuDateButton = new TextButton(60, 255, 60, 25, () -> switchOption(() -> RainyAPI.mainMenuDate = !RainyAPI.mainMenuDate),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.mainMenuDateButton = new TextButton(60, 255, 60, 25, () -> switchOption(() -> RainyAPI.mainMenuDate = !RainyAPI.mainMenuDate),
                    "关", "0", true, 10, 34, 7);
        }
        if (RainyAPI.guiSnow) {
            this.guiSnowButton = new TextButton(60, 325, 60, 25, () -> switchOption(() -> RainyAPI.guiSnow = !RainyAPI.guiSnow),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.guiSnowButton = new TextButton(60, 325, 60, 25, () -> switchOption(() -> RainyAPI.guiSnow = !RainyAPI.guiSnow),
                    "关", "0", true, 10, 34, 7);
        }
        if (RainyAPI.backgroundBlur) {
            this.backgroundBlurButton = new TextButton(60, 395, 60, 25, () -> switchOption(() -> RainyAPI.backgroundBlur = !RainyAPI.backgroundBlur),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.backgroundBlurButton = new TextButton(60, 395, 60, 25, () -> switchOption(() -> RainyAPI.backgroundBlur = !RainyAPI.backgroundBlur),
                    "关", "0", true, 10, 34, 7);
        }
        if (RainyAPI.imageScreen) {
            this.imageScreenButton = new TextButton(60, 465, 60, 25, () -> switchOption(() -> RainyAPI.imageScreen = !RainyAPI.imageScreen),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.imageScreenButton = new TextButton(60, 465, 60, 25, () -> switchOption(() -> RainyAPI.imageScreen = !RainyAPI.imageScreen),
                    "关", "0", true, 10, 34, 7);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
    }
}
