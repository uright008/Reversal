/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.ui.atmoic.mainmenu.impl;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.misc.component.TextButton;
import cn.stars.reversal.ui.atmoic.msgbox.AtomicMsgBox;
import cn.stars.reversal.ui.atmoic.msgbox.MsgBoxFactory;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ReversalSettingsGui extends AtomicGui {
    public GuiScreen parent;
    private TextButton exitButton, shaderButton, guiSnowButton, backgroundBlurButton, imageScreenButton, asyncLoadingButton;
    private TextButton[] buttons;

    public ReversalSettingsGui() {
        super("Reversal Settings", "reversal settings", "e");
    }

    @Override
    public void drawIcon(float posX, float posY, int color) {
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
        regular20Bold.drawString("Disable Shader", 60, 80, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("开启这个选项后，你将不可以在主菜单使用Shader背景。\n部分电脑不支持并会导致崩溃，如果崩溃请开启。", 60, 95, new Color(220, 220, 220, 240).getRGB());

        // Gui Snow
        regular20Bold.drawString("Gui Snow", 60, 150, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("使你的界面有类似下雪的效果。", 60, 165, new Color(220, 220, 220, 240).getRGB());

        // Background Blur
        regular20Bold.drawString("Background Blur", 60, 220, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("使你的背景有模糊效果。", 60, 235, new Color(220, 220, 220, 240).getRGB());

        // Image Screen
        regular20Bold.drawString("Image Screen", 60, 290, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("在游戏窗口出现时显示一个Hoshino Shield的界面。\n纯属整活，不会拖慢加载速度。", 60, 305, new Color(220, 220, 220, 240).getRGB());

        // Async Loading
        regular20Bold.drawString("Async Loading", 60, 360, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("使用多线程加载客户端。\n部分情况下多线程可能导致问题，此时可选择关闭。", 60, 375, new Color(220, 220, 220, 240).getRGB());

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

        buttons = new TextButton[]{exitButton, shaderButton, guiSnowButton, backgroundBlurButton, imageScreenButton, asyncLoadingButton};
    }

    private void switchOption(String name) {
        Reversal.atomicMsgBox = new MsgBoxFactory()
                .setStyle(AtomicMsgBox.MsgBoxStyle.CONFIRM)
                .setTitle("开关功能")
                .addLine("请选择需要设置的开关状态")
                .addLine("(Yes=开启, No=关闭)")
                .onYes(() -> {
                    try {
                        ReversalLogger.info("run");
                        RainyAPI.class.getDeclaredField(name).set(null, true);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onNo(() -> {
                    try {
                        RainyAPI.class.getDeclaredField(name).set(null, false);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onFinish(this::createButton)
                .build();
    }

    private void createButton() {
        this.exitButton = new TextButton(width / 2f - 60, height - 60, 120, 35, () -> AtomicMenu.switchGui(0),
                "返回主菜单", "g", true, 12, 38, 11);
        this.shaderButton = new TextButton(60, 115, 60, 25, () -> switchOption("isShaderCompatibility"),
                RainyAPI.isShaderCompatibility ? "开" : "关", RainyAPI.isShaderCompatibility ? "9" : "0", true, 10, 34, 7);
        this.guiSnowButton = new TextButton(60, 185, 60, 25, () -> switchOption("guiSnow"),
                RainyAPI.guiSnow ? "开" : "关", RainyAPI.guiSnow ? "9" : "0", true, 10, 34, 7);
        this.backgroundBlurButton = new TextButton(60, 255, 60, 25, () -> switchOption("backgroundBlur"),
                RainyAPI.backgroundBlur ? "开" : "关", RainyAPI.backgroundBlur ? "9" : "0", true, 10, 34, 7);
        this.imageScreenButton = new TextButton(60, 325, 60, 25, () -> switchOption("imageScreen"),
                RainyAPI.imageScreen ? "开" : "关", RainyAPI.imageScreen ? "9" : "0", true, 10, 34, 7);
        this.asyncLoadingButton = new TextButton(60, 395, 60, 25, () -> switchOption("asyncLoading"),
                RainyAPI.asyncLoading ? "开" : "关", RainyAPI.asyncLoading ? "9" : "0", true, 10, 34, 7);
        buttons = new TextButton[]{exitButton, shaderButton, guiSnowButton, backgroundBlurButton, imageScreenButton, asyncLoadingButton};
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
    }
}
