package cn.stars.reversal.ui.atmoic.mainmenu;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.ui.atmoic.island.Atomic;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.*;
import cn.stars.reversal.ui.notification.NotificationManager;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.player.SkinUtil;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.ThemeType;
import cn.stars.reversal.util.render.ThemeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AtomicMenu extends GuiScreen implements GameInstance {
    public static ArrayList<AtomicGui> atomicGuis = new ArrayList<>();
    public static AtomicGui currentGui;
    public static int lastGuiIndex;
    private final MFont psm16 = FontManager.getPSM(16);
    private final MFont atomic24 = FontManager.getAtomic(24);
    private final LocalDateTime initTime;

    private final Animation upperSelectionAnimation = new Animation(Easing.EASE_OUT_EXPO, 500);
    private final Animation initAnimation = new Animation(Easing.LINEAR, 200);

    private final Animation subHoverAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation subPosAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private boolean subMenu = false;

    public static int announcementIndex = 0;
    public static float anPosX = 60, anPosY = 80;

    @Override
    public void initGui() {
        currentGui.initGui();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        ColorUtil.updateColorAnimation();

        // Background Blur
        initAnimation.run(currentGui == atomicGuis.get(0) ? 0 : 255);
        ModuleInstance.getPostProcessing().drawElementWithBlur(() -> RenderUtil.rect(0,0,width,height, new Color(0,0,0, (int) initAnimation.getValue())), 2, 2);

        // Current AtomicGui
        currentGui.drawScreen(mouseX, mouseY, partialTicks);

        // Upper part
        RenderUtil.rect(0, 0, this.width, 25, new Color(20,20,20,200));

        upperSelectionAnimation.run(50 + atomicGuis.indexOf(currentGui) * 25);
        RenderUtil.rect(upperSelectionAnimation.getValue(), 0, 25, 25, new Color(20,20,20,100));

        for (AtomicGui atomicGui : atomicGuis) {
            if (RenderUtil.isHovered(50 + atomicGuis.indexOf(atomicGui) * 25, 0, 25, 25, mouseX, mouseY)) atomicGui.hoverAnimation.run(80);
            else atomicGui.hoverAnimation.run(0);
            RenderUtil.rect(50 + atomicGuis.indexOf(atomicGui) * 25, 0, 25, 25, new Color(20,20,20, (int) atomicGui.hoverAnimation.getValue()));
            RenderUtil.roundedRectangle(60 - psm16.width(atomicGui.name) / 2f + atomicGuis.indexOf(atomicGui) * 25, 27, psm16.width(atomicGui.name) + 5, psm16.height() + 2, 2, new Color(20, 20, 20, (int) atomicGui.hoverAnimation.getValue() * 3));
            psm16.drawString(atomicGui.name, 62.5 - psm16.width(atomicGui.name) / 2f + atomicGuis.indexOf(atomicGui) * 25, 30, new Color(255,255,255, (int) atomicGui.hoverAnimation.getValue() * 3).getRGB());
            atomicGui.drawIcon(50 + atomicGuis.indexOf(atomicGui) * 25 + 6, 8, Color.WHITE.getRGB());
        }

        // Player & Time
        try {
            RenderUtil.image(SkinUtil.getResourceLocation(SkinUtil.SkinType.AVATAR, SkinUtil.uuidOf(GameInstance.mc.session.getUsername()), 15), width - 130, 5, 15, 15);
        } catch (Exception e) {
            RenderUtil.rect(width - 130, 5, 15, 15, Color.WHITE);
        }
        psm18.drawString(GameInstance.mc.session.getUsername(), width - 130 - psm18.getStringWidth(GameInstance.mc.session.getUsername()) - 5, 10, Color.WHITE.getRGB());
        RenderUtil.rect(width - 107, 5, 1, 15, Color.WHITE);
        RenderUtil.rect(width - 30, 5, 1, 15, Color.WHITE);

        psm18.drawString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " " + (LocalDateTime.now().getHour() > 12 ? "PM" : "AM"),
                width - 100, 6, Color.WHITE.getRGB());
        Duration duration = Duration.between(initTime, LocalDateTime.now());
        psm16.drawString("running " + String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes() % 60, duration.getSeconds() % 60), width - 100, 14, Color.WHITE.getRGB());

        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RenderUtil.rect(upperSelectionAnimation.getValue(), 24.2, 25, 0.8, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));
            currentGui.drawIcon(50 + atomicGuis.indexOf(currentGui) * 25 + 6, 8, ColorUtil.whiteAnimation.getOutput().getRGB());
            RenderUtil.rect(width - 130, 5, 15, 15, ColorUtil.whiteAnimation.getOutput());
        }, 2, 2);

        RenderUtil.rect(upperSelectionAnimation.getValue(), 24.2, 25, 0.8, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));

        // Other
        psm16.drawString(Minecraft.getDebugFPS() + " FPS", 1, 1, Color.WHITE.getRGB());
        psm16.drawString(currentGui.name, 1, 8, Color.WHITE.getRGB());

        // Gui Elements
        NotificationManager.onRender2D();

        Atomic.INSTANCE.render(new ScaledResolution(GameInstance.mc));

        TEMP_TEXT_BUTTON_RUNNABLES.forEach(i -> {
            ModuleInstance.getModule(PostProcessing.class).drawElementWithBlur(i, 2,2);
            ModuleInstance.getModule(PostProcessing.class).drawElementWithBloom(i, 2, 2);
        });
        TEMP_TEXT_BUTTON_RUNNABLES.clear();

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();

        GameInstance.clearRunnables();

        // Sub Menu
        drawSubMenu(mouseX, mouseY, partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawSubMenu(int mouseX, int mouseY, float partialTicks) {
        subHoverAnimation.run(RenderUtil.isHovered(width - 25, 0, 25, 25, mouseX, mouseY) ? 80 : 0);
        subPosAnimation.run(subMenu ? 200 : 0);
        RenderUtil.rect(width - subPosAnimation.getValue(), 0, subPosAnimation.getValue(), height, new Color(20,20,20,240));
        RenderUtil.rect(width - 25, 0, 25, 25, new Color(20,20,20,(int) subHoverAnimation.getValue()));
        atomic24.drawString("4", width - 18, 9, Color.WHITE.getRGB());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(width - subPosAnimation.getValue(), 0, subPosAnimation.getValue(), height);

        psb20.drawString("Sub Menu", width - subPosAnimation.getValue() + 6, 10, new Color(255,255,255, (int) (subPosAnimation.getValue() * 1.25)).getRGB());

        psm18.drawString("Change background", width - subPosAnimation.getValue() + 8, 30, new Color(255,255,255, (int) (subPosAnimation.getValue() * 1.25)).getRGB());

        int c1 = RenderUtil.isHovered(width - subPosAnimation.getValue() + 18, 43, 15, 15, mouseX, mouseY) ? 255 : 150;
        int c2 = RenderUtil.isHovered(width - subPosAnimation.getValue() + 63, 43, 15, 15, mouseX, mouseY) ? 255 : 150;

        psm24.drawString("←", width - subPosAnimation.getValue() + 20, 45, new Color(c1,c1,c1, (int) (subPosAnimation.getValue() * 1.25)).getRGB());
        psm24.drawString("[" + RainyAPI.backgroundId + "]", width - subPosAnimation.getValue() + 40, 45, new Color(255,255,255, (int) (subPosAnimation.getValue() * 1.25)).getRGB());
        psm24.drawString("→", width - subPosAnimation.getValue() + 65, 45, new Color(c2,c2,c2, (int) (subPosAnimation.getValue() * 1.25)).getRGB());

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (AtomicGui atomicGui : atomicGuis) {
            if (RenderUtil.isHovered(50 + atomicGuis.indexOf(atomicGui) * 25, 0, 25, 25, mouseX, mouseY)) {
                lastGuiIndex = atomicGuis.indexOf(currentGui);
                currentGui = atomicGui;
                currentGui.initGui();
                GameInstance.mc.getSoundHandler().playButtonPress();
            }
        }
        if (RenderUtil.isHovered(width - 25, 0, 25, 25, mouseX, mouseY)) {
            subMenu = !subMenu;
            GameInstance.mc.getSoundHandler().playButtonPress();
        } else if (subMenu && !RenderUtil.isHovered(width - subPosAnimation.getValue(), 0, subPosAnimation.getValue(), height, mouseX, mouseY)) {
            subMenu = false;
            GameInstance.mc.getSoundHandler().playButtonPress();
        }
        if (subMenu) {
            if (RenderUtil.isHovered(width - subPosAnimation.getValue() + 18, 43, 15, 15, mouseX, mouseY)) {
                changeMenuBackground(true);
                GameInstance.mc.getSoundHandler().playButtonPress();
            }
            if (RenderUtil.isHovered(width - subPosAnimation.getValue() + 63, 43, 15, 15, mouseX, mouseY)) {
                changeMenuBackground(false);
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

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        currentGui.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        currentGui.mouseReleased(mouseX, mouseY, state);
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        currentGui.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_ESCAPE) {
            switchGui(lastGuiIndex);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        currentGui.onGuiClosed();
        super.onGuiClosed();
    }

    public AtomicMenu() {
        init();
        currentGui = atomicGuis.get(0);
        currentGui.initGui();
        initTime = LocalDateTime.now();
        lastGuiIndex = 0;
        atomicGuis.set(8, new MiscGui());
    }

    public static void switchGui(int index) {
    //    reinit();
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
        atomicGuis.add(new ReversalSettingsGui());
        atomicGuis.add(new AnnouncementGui());
        atomicGuis.add(new SponsorGui());
        atomicGuis.add(new ViaVersionGui());
        atomicGuis.add(new MiscGui());
    }


}
