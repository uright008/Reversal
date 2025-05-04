/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.ui.atmoic.mainmenu.impl;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.misc.component.TextButton;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ReversalSettingsGui extends AtomicGui {
    public GuiScreen parent;
    private TextButton exitButton, shaderButton, guiSnowButton, backgroundBlurButton, imageScreenButton;
    private TextButton[] buttons;

    public ReversalSettingsGui() {
        super("Reversal Settings", "reversal settings", "e");
    }

    @Override
    public void drawIcon(int posX, int posY, int color) {
        atomic24.drawString(icon, posX + 2, posY + 0.5, color);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(50, 65, width - 100, height - 85, 3, Color.BLACK);
        }, 2, 2);

        RoundedUtil.drawRound(50, 65, width - 100, height - 85, 3, new Color(20, 20, 20, 160));

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(50, 65, width - 100, height - 85);

        // Shader
        regular20Bold.drawString("Background Shader", 60, 80, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("开启这个选项后，你将可以在主菜单使用Shader背景。\n部分电脑不支持并会导致崩溃，如果崩溃请关闭。", 60, 95, new Color(220, 220, 220, 240).getRGB());

        // Gui Snow
        regular20Bold.drawString("Gui Snow", 60, 150, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("使你的界面有类似下雪的效果。", 60, 165, new Color(220, 220, 220, 240).getRGB());

        // Background Blur
        regular20Bold.drawString("Background Blur", 60, 220, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("使你的背景有模糊效果。", 60, 235, new Color(220, 220, 220, 240).getRGB());

        // Image Screen
        regular20Bold.drawString("Image Screen", 60, 290, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("在游戏窗口出现时显示一个Hoshino Shield的界面。\n纯属整活。（不会拖慢加载速度）", 60, 305, new Color(220, 220, 220, 240).getRGB());

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        createButton();

        buttons = new TextButton[]{exitButton, shaderButton, guiSnowButton, backgroundBlurButton, imageScreenButton};
    }

    private void switchOption(Runnable runnable) {
        runnable.run();
        createButton();
        RainyAPI.processAPI(true);
        buttons = new TextButton[]{exitButton, shaderButton, guiSnowButton, backgroundBlurButton, imageScreenButton};
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
        if (RainyAPI.guiSnow) {
            this.guiSnowButton = new TextButton(60, 185, 60, 25, () -> switchOption(() -> RainyAPI.guiSnow = !RainyAPI.guiSnow),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.guiSnowButton = new TextButton(60, 185, 60, 25, () -> switchOption(() -> RainyAPI.guiSnow = !RainyAPI.guiSnow),
                    "关", "0", true, 10, 34, 7);
        }
        if (RainyAPI.backgroundBlur) {
            this.backgroundBlurButton = new TextButton(60, 255, 60, 25, () -> switchOption(() -> RainyAPI.backgroundBlur = !RainyAPI.backgroundBlur),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.backgroundBlurButton = new TextButton(60, 255, 60, 25, () -> switchOption(() -> RainyAPI.backgroundBlur = !RainyAPI.backgroundBlur),
                    "关", "0", true, 10, 34, 7);
        }
        if (RainyAPI.imageScreen) {
            this.imageScreenButton = new TextButton(60, 325, 60, 25, () -> switchOption(() -> RainyAPI.imageScreen = !RainyAPI.imageScreen),
                    "开", "9", true, 10, 34, 7);
        } else {
            this.imageScreenButton = new TextButton(60, 325, 60, 25, () -> switchOption(() -> RainyAPI.imageScreen = !RainyAPI.imageScreen),
                    "关", "0", true, 10, 34, 7);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
    }
}
