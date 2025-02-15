package cn.stars.reversal.ui.atmoic.mainmenu;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.ui.atmoic.island.Atomic;
import cn.stars.reversal.ui.atmoic.mainmenu.menus.MainGui;
import cn.stars.reversal.ui.atmoic.mainmenu.menus.MultiPlayerGui;
import cn.stars.reversal.ui.atmoic.mainmenu.menus.SettingsGui;
import cn.stars.reversal.ui.atmoic.mainmenu.menus.SinglePlayerGui;
import cn.stars.reversal.ui.notification.NotificationManager;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.ThemeType;
import cn.stars.reversal.util.render.ThemeUtil;
import cn.stars.reversal.util.shader.RiseShaders;
import cn.stars.reversal.util.shader.base.ShaderRenderType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class AtomicMenu extends GuiScreen implements GameInstance {
    public static ArrayList<AtomicGui> atomicGuis = new ArrayList<>();
    public static AtomicGui currentGui;

    private final Animation upperSelectionAnimation = new Animation(Easing.EASE_OUT_EXPO, 400);

    @Override
    public void initGui() {
        currentGui.initGui();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        ModuleInstance.getModule(PostProcessing.class).blurScreen();

        currentGui.drawScreen(mouseX, mouseY, partialTicks);

        RenderUtil.rect(0, 0, this.width, 25, new Color(20,20,20,200));

        upperSelectionAnimation.run(50 + atomicGuis.indexOf(currentGui) * 25);
        RenderUtil.rect(upperSelectionAnimation.getValue(), 0, 25, 25, new Color(20,20,20,100));

        for (AtomicGui atomicGui : atomicGuis) {
            if (RenderUtil.isHovered(50 + atomicGuis.indexOf(atomicGui) * 25, 0, 25, 25, mouseX, mouseY)) atomicGui.hoverAnimation.run(80);
            else atomicGui.hoverAnimation.run(0);
            RenderUtil.rect(50 + atomicGuis.indexOf(atomicGui) * 25, 0, 25, 25, new Color(20,20,20, (int) atomicGui.hoverAnimation.getValue()));
            atomicGui.drawIcon(50 + atomicGuis.indexOf(atomicGui) * 25 + 6, 8);
        }

        MODERN_BLOOM_RUNNABLES.add(() -> currentGui.drawIcon(50 + atomicGuis.indexOf(currentGui) * 25 + 6, 8));

        RenderUtil.rect(upperSelectionAnimation.getValue(), 24.2, 25, 0.8, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));

        NotificationManager.onRender2D();

        Atomic.INSTANCE.render(new ScaledResolution(GameInstance.mc));

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();

        GameInstance.clearRunnables();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (AtomicGui atomicGui : atomicGuis) {
            if (RenderUtil.isHovered(50 + atomicGuis.indexOf(atomicGui) * 25, 0, 25, 25, mouseX, mouseY)) {
                currentGui = atomicGui;
                currentGui.initGui();
                GameInstance.mc.getSoundHandler().playButtonPress();
            }
        }
        currentGui.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        currentGui.updateScreen();
        super.updateScreen();
    }

    @Override
    public void handleMouseInput() throws IOException {
        currentGui.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        currentGui.actionPerformed(button);
        super.actionPerformed(button);
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        currentGui.confirmClicked(result, id);
        super.confirmClicked(result, id);
    }

    public AtomicMenu() {
        init();
        currentGui = atomicGuis.get(0);
        currentGui.initGui();
    }

    public static void switchGui(int index) {
        init();
        currentGui = atomicGuis.get(index);
        currentGui.initGui();
        if (GameInstance.mc.currentScreen instanceof AtomicMenu) GameInstance.mc.getSoundHandler().playButtonPress();
    }

    private static void init() {
        atomicGuis.clear();
        atomicGuis.add(new MainGui());
        atomicGuis.add(new SinglePlayerGui());
        atomicGuis.add(new MultiPlayerGui());
        atomicGuis.add(new SettingsGui());
    }

}
