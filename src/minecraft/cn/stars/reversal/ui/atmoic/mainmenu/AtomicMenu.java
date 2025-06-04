package cn.stars.reversal.ui.atmoic.mainmenu;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.ui.atmoic.island.Atomic;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.*;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.misc.MsLoginGui;
import cn.stars.reversal.ui.atmoic.misc.GUIBubble;
import cn.stars.reversal.ui.atmoic.msgbox.AtomicMsgBox;
import cn.stars.reversal.ui.notification.NotificationManager;
import cn.stars.reversal.util.animation.advanced.composed.CustomAnimation;
import cn.stars.reversal.util.animation.advanced.impl.SmoothStepAnimation;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.player.SkinUtil;
import cn.stars.reversal.util.render.*;
import cn.stars.reversal.util.render.video.VideoUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class AtomicMenu extends GuiScreen implements GameInstance {
    public static ArrayList<AtomicGui> atomicGuis = new ArrayList<>();
    private final ArrayList<GUIBubble> bubbles = new ArrayList<>();
    public static AtomicGui currentGui;
    public static int lastGuiIndex;
    private final MFont atomic24 = FontManager.getAtomic(24);
    private final LocalDateTime initTime;
    private ResourceLocation headImage;

    private final Animation upperSelectionAnimation = new Animation(Easing.EASE_OUT_EXPO, 500);
    private final Animation upperScrollAnimation = new Animation(Easing.EASE_OUT_EXPO, 750);
    private final Animation alphaAnimation = new Animation(Easing.LINEAR, 200);
    private final Animation bgAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation subHoverAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation subPosAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation displayNameAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final CustomAnimation cursorAnimation = new CustomAnimation(SmoothStepAnimation.class, 1000, -5, 5);
    private boolean subMenu = false;
    private final TimeUtil clickTimer = new TimeUtil();

    public static int announcementIndex = 0;
    public static float anPosX = 60, anPosY = 80;

    public static ArrayList<Runnable> PRE_POSTPROCESSING_QUEUE = new ArrayList<>();
    public static ArrayList<Runnable> POST_POSTPROCESSING_QUEUE = new ArrayList<>();

    @Override
    public void initGui() {
        currentGui.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (RainyAPI.backgroundId == 9) {
            bgAnimation.run(currentGui instanceof BocchiMainGui && !BocchiMainGui.isPulledUp ? 0 : 30);
            VideoUtil.render((float) -bgAnimation.getValue(), (float) -bgAnimation.getValue(), width + (float) bgAnimation.getValue(), height + (float) bgAnimation.getValue());
        } else {
            drawDefaultBackground();
        }

        if (!atomicGuis.contains(currentGui)) switchGui(0);

        ColorUtil.updateColorAnimation();

        // Background Blur
        alphaAnimation.run(currentGui == atomicGuis.get(0) && !BocchiMainGui.isPulledUp ? 0 : 255);
        ModuleInstance.getPostProcessing().drawElementWithBlur(() -> RenderUtil.rect(0,0,width,height, new Color(255,255,255, (int) alphaAnimation.getValue())), 2, 2);

        if (RainyAPI.menuBubble) {
            try {
                for (GUIBubble bubble : bubbles) {
                    if (bubble.shouldRemove()) {
                        bubbles.remove(bubble);
                    } else {
                        bubble.render();
                    }
                }
            } catch (ConcurrentModificationException ignored) {
            }
        } else bubbles.clear();

        if (!PRE_POSTPROCESSING_QUEUE.isEmpty()) ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            PRE_POSTPROCESSING_QUEUE.forEach(Runnable::run);
        }, 2, 2);

        // Current AtomicGui
        currentGui.drawScreen(mouseX, mouseY, partialTicks);

        // Name
        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            if (!currentGui.displayName.isEmpty()) {
                RenderUtil.drawRightTrapezoid(50, 33, (float) displayNameAnimation.getValue(), 23, 10, 0, Color.BLACK);
            }
        }, 2, 2);

        // Upper part
        upperScrollAnimation.run(RenderUtil.isHovered(0,0, width, 100, mouseX, mouseY) || !clickTimer.hasReached(3000L) ? 0 : -25);

        RenderUtil.rect(0, 0 + upperScrollAnimation.getValue(), this.width, 25, new Color(20,20,20,200));

        upperSelectionAnimation.run(50 + atomicGuis.indexOf(currentGui) * 25);
        RenderUtil.rect(upperSelectionAnimation.getValue(), 0 + upperScrollAnimation.getValue(), 25, 25, new Color(20,20,20,100));

        if (!currentGui.displayName.isEmpty()) {
            displayNameAnimation.run(26 + FontManager.getRainbowParty(40).width(currentGui.displayName));
            RenderUtil.drawRightTrapezoid(50, 33, (float) displayNameAnimation.getValue(), 23, 10, 0, new Color(20, 20, 20, 160));
            RoundedUtil.drawRound(58, 43, 4, 4, 1.5f, Color.WHITE);
            RenderUtils.drawLoadingCircle3(60, 45, 5, Color.WHITE);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(50, 33, (float) displayNameAnimation.getValue() + 2, 23);
            FontManager.getRainbowParty(40).drawString(currentGui.displayName, 75, 35, Color.WHITE.getRGB());
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            displayNameAnimation.run(0);
        }

        for (AtomicGui atomicGui : atomicGuis) {
            if (RenderUtil.isHovered(50 + atomicGuis.indexOf(atomicGui) * 25, 0, 25, 25, mouseX, mouseY)) atomicGui.hoverAnimation.run(80);
            else atomicGui.hoverAnimation.run(0);
            RenderUtil.rect(50 + atomicGuis.indexOf(atomicGui) * 25, 0 + upperScrollAnimation.getValue(), 25, 25, new Color(20,20,20, (int) atomicGui.hoverAnimation.getValue()));
            RenderUtil.roundedRectangle(60 - regular16.width(atomicGui.name) / 2f + atomicGuis.indexOf(atomicGui) * 25, 27 + upperScrollAnimation.getValue(), regular16.width(atomicGui.name) + 5, regular16.height() + 2, 2, new Color(20, 20, 20, (int) atomicGui.hoverAnimation.getValue() * 2));
            regular16.drawString(atomicGui.name, 62.5 - regular16.width(atomicGui.name) / 2f + atomicGuis.indexOf(atomicGui) * 25, 30 + upperScrollAnimation.getValue(), new Color(255,255,255, (int) atomicGui.hoverAnimation.getValue() * 3).getRGB());
            atomicGui.drawIcon(50 + atomicGuis.indexOf(atomicGui) * 25 + 6, 8 + (float)upperScrollAnimation.getValue(), ColorUtil.WHITE.getRGB());
        }

        // Player & Time
        this.drawPlayerImage();
        regular18.drawString(GameInstance.mc.session.getUsername(), width - 150 - regular18.getStringWidth(GameInstance.mc.session.getUsername()) - 5, 10 + upperScrollAnimation.getValue(), Color.WHITE.getRGB());
        RenderUtil.rect(width - 127, 5 + upperScrollAnimation.getValue(), 1, 15, Color.WHITE);
        RenderUtil.rect(width - 30, 5 + upperScrollAnimation.getValue(), 1, 15, Color.WHITE);

        regular18.drawString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " " + (LocalDateTime.now().getHour() > 12 ? "PM" : "AM"),
                width - 100, 6 + upperScrollAnimation.getValue(), Color.WHITE.getRGB());
        Duration duration = Duration.between(initTime, LocalDateTime.now());
        regular16.drawString("running " + String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes() % 60, duration.getSeconds() % 60), width - 100, 14 + upperScrollAnimation.getValue(), Color.WHITE.getRGB());

        RenderUtil.clock(width - 120, 5 + upperScrollAnimation.getValue(), 15, 2.4,2,1.2, Color.WHITE);

        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RenderUtil.rect(upperSelectionAnimation.getValue(), 24.2 + upperScrollAnimation.getValue(), 25, 0.8, ColorUtil.PINK);
            currentGui.drawIcon(50 + atomicGuis.indexOf(currentGui) * 25 + 6, 8 + (float)upperScrollAnimation.getValue(), ColorUtil.whiteAnimation.getOutput().getRGB());
            this.drawPlayerImage();

            if (!currentGui.displayName.isEmpty()) {
                RoundedUtil.drawRound(58, 43, 4, 4, 1.5f, Color.WHITE);
                RenderUtils.drawLoadingCircle3(60, 45, 5, Color.WHITE);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.scissor(50, 33, (float) displayNameAnimation.getValue() + 2, 23);
                FontManager.getRainbowParty(40).drawString(currentGui.displayName, 75, 35, Color.WHITE.getRGB());
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        }, 2, 2);

        RenderUtil.rect(upperSelectionAnimation.getValue(), 24.2 + upperScrollAnimation.getValue(), 25, 0.8, ColorUtil.PINK);

        if (cursorAnimation.getAnimation().finished(cursorAnimation.getAnimation().getDirection())) cursorAnimation.changeDirection();

        if (atomicGuis.indexOf(currentGui) != 0) {
            RenderUtil.image(new ResourceLocation("reversal/images/music/arrow-left.png"), 15 + cursorAnimation.getOutput().floatValue(), height / 2f - 12f, 24,24,
                    RenderUtil.isHovered(8, height / 2f - 15, 40, 30, mouseX, mouseY) ? new Color(255, 255, 255, 255) : new Color(255, 255, 255, 150));
        }

        if (atomicGuis.indexOf(currentGui) != atomicGuis.size() - 1) {
            RenderUtil.image(new ResourceLocation("reversal/images/music/arrow-right.png"), width - 39 - cursorAnimation.getOutput().floatValue(), height / 2f - 12f, 24,24,
                    RenderUtil.isHovered(width - 48, height / 2f - 15, 40, 30, mouseX, mouseY) ? new Color(255, 255, 255, 255) : new Color(255, 255, 255, 150));
        }

      /*  // Other
        regular16.drawString(Minecraft.getDebugFPS() + " FPS", 1, 1, Color.WHITE.getRGB());
        regular16.drawString(currentGui.name, 1, 8, Color.WHITE.getRGB()); */

        // Gui Elements
        NotificationManager.onRender2D();

        Atomic.INSTANCE.render(new ScaledResolution(GameInstance.mc));

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();

        GameInstance.clearRunnables();

        // Sub Menu
        drawSubMenu(mouseX, mouseY, partialTicks);

        if (!POST_POSTPROCESSING_QUEUE.isEmpty()) ModuleInstance.getPostProcessing().drawElementWithBloom(() -> POST_POSTPROCESSING_QUEUE.forEach(Runnable::run), 2, 2);

        PRE_POSTPROCESSING_QUEUE.clear();
        POST_POSTPROCESSING_QUEUE.clear();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawSubMenu(int mouseX, int mouseY, float partialTicks) {
        subHoverAnimation.run(RenderUtil.isHovered(width - 25, 0, 25, 25, mouseX, mouseY) ? 80 : 0);
        subPosAnimation.run(subMenu ? 200 : 0);
        RenderUtil.rect(width - subPosAnimation.getValue(), 0, subPosAnimation.getValue(), height, new Color(20,20,20,240));
        RenderUtil.rect(width - 25, 0, 25, 25, new Color(20,20,20,(int) subHoverAnimation.getValue()));
        atomic24.drawString("4", width - 18, 9 + upperScrollAnimation.getValue(), Color.WHITE.getRGB());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(width - subPosAnimation.getValue(), 0, subPosAnimation.getValue(), height);

        regular24Bold.drawString("Sub Menu", width - subPosAnimation.getValue() + 6, 10, new Color(255,255,255, (int) (subPosAnimation.getValue() * 1.25)).getRGB());

        regular18.drawString("Change background", width - subPosAnimation.getValue() + 8, 30, new Color(255,255,255, (int) (subPosAnimation.getValue() * 1.25)).getRGB());

        int c1 = RenderUtil.isHovered(width - subPosAnimation.getValue() + 18, 43, 15, 15, mouseX, mouseY) ? 255 : 150;
        int c2 = RenderUtil.isHovered(width - subPosAnimation.getValue() + 63, 43, 15, 15, mouseX, mouseY) ? 255 : 150;

        regular20Bold.drawString("←", width - subPosAnimation.getValue() + 20, 45, new Color(c1,c1,c1, (int) (subPosAnimation.getValue() * 1.25)).getRGB());
        regular20Bold.drawString("[" + RainyAPI.backgroundId + "]", width - subPosAnimation.getValue() + 40, 45, new Color(255,255,255, (int) (subPosAnimation.getValue() * 1.25)).getRGB());
        regular20Bold.drawString("→", width - subPosAnimation.getValue() + 65, 45, new Color(c2,c2,c2, (int) (subPosAnimation.getValue() * 1.25)).getRGB());

        regular18.drawString("Click Bubble", width - subPosAnimation.getValue() + 8, 70, new Color(255,255,255, (int) (subPosAnimation.getValue() * 1.25)).getRGB());

        int c3 = RenderUtil.isHovered(width - subPosAnimation.getValue() + 68, 70, 15, 15, mouseX, mouseY) ? 255 : 150;

        regular20Bold.drawString(RainyAPI.menuBubble ? "✓" : "×", width - subPosAnimation.getValue() + 70, 70, new Color(c3,c3,c3, (int) (subPosAnimation.getValue() * 1.25)).getRGB());

        int c4 = RenderUtil.isHovered(width - subPosAnimation.getValue() + 8, 90, 100, 15, mouseX, mouseY) ? 255 : 150;

        regular18.drawString("Microsoft Login", width - subPosAnimation.getValue() + 8, 90, new Color(c4, c4, c4, (int) (subPosAnimation.getValue() * 1.25)).getRGB());

        int c5 = RenderUtil.isHovered(width - subPosAnimation.getValue() + 8, 110, 100, 15, mouseX, mouseY) ? 255 : 150;

        regular18.drawString("Atomic Message Box", width - subPosAnimation.getValue() + 8, 110, new Color(c5, c5, c5, (int) (subPosAnimation.getValue() * 1.25)).getRGB());

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        clickTimer.reset();
        if (RenderUtil.isHovered(width - 25, 0, 25, 25, mouseX, mouseY)) {
            subMenu = !subMenu;
            uiClick();
        } else if (subMenu && !RenderUtil.isHovered(width - subPosAnimation.getValue(), 0, subPosAnimation.getValue(), height, mouseX, mouseY)) {
            subMenu = false;
            uiClick();
        }
        if (subMenu) {
            if (RenderUtil.isHovered(width - subPosAnimation.getValue() + 18, 43, 15, 15, mouseX, mouseY)) {
                changeMenuBackground(true);
                uiClick();
            }
            if (RenderUtil.isHovered(width - subPosAnimation.getValue() + 63, 43, 15, 15, mouseX, mouseY)) {
                changeMenuBackground(false);
                uiClick();
            }
            if (RenderUtil.isHovered(width - subPosAnimation.getValue() + 68, 70, 15, 15, mouseX, mouseY)) {
                RainyAPI.menuBubble = !RainyAPI.menuBubble;
                uiClick();
            }
            if (RenderUtil.isHovered(width - subPosAnimation.getValue() + 8, 90, 100, 15, mouseX, mouseY)) {
                setMiscGui(new MsLoginGui());
                switchGui(8);
                uiClick();
            }
            if (RenderUtil.isHovered(width - subPosAnimation.getValue() + 8, 110, 100, 15, mouseX, mouseY)) {
                if (Reversal.atomicMsgBox == null) {
                    Reversal.atomicMsgBox = new AtomicMsgBox("Test");
                    Reversal.atomicMsgBox.FACTORY.addLine("这是第一行");
                    Reversal.atomicMsgBox.FACTORY.addLine("这是第二行 @w@");
                    Reversal.atomicMsgBox.FACTORY.addLine(RainyAPI.getRandomTitle());
                    Reversal.atomicMsgBox.FACTORY.setStyle(AtomicMsgBox.MsgBoxStyle.CONFIRM);
                }
                else Reversal.atomicMsgBox.close();
                uiClick();
            }
            return;
        }
        for (AtomicGui atomicGui : atomicGuis) {
            if (RenderUtil.isHovered(50 + atomicGuis.indexOf(atomicGui) * 25, 0, 25, 25, mouseX, mouseY)) {
                lastGuiIndex = !atomicGuis.contains(currentGui) ? 0 : atomicGuis.indexOf(currentGui);
                currentGui = atomicGui;
                atomicGuis.get(lastGuiIndex).onGuiClosed();
                currentGui.initGui();
                uiClick();
            }
        }
        if (atomicGuis.indexOf(currentGui) != 0) {
            if (RenderUtil.isHovered(8, height / 2f - 15, 40, 30, mouseX, mouseY)) {
                switchGui(atomicGuis.indexOf(currentGui) - 1);
                uiClick();
            }
        }
        if (atomicGuis.indexOf(currentGui) != atomicGuis.size() - 1) {
            if (RenderUtil.isHovered(width - 48, height / 2f - 15, 40, 30, mouseX, mouseY)) {
                switchGui(atomicGuis.indexOf(currentGui) + 1);
                uiClick();
            }
        }
        currentGui.mouseClicked(mouseX, mouseY, mouseButton);
        bubbles.add(new GUIBubble(mouseX, mouseY, 10, 50));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void drawPlayerImage() {
        try {
            RenderUtil.image(headImage, width - 150, 5 + (float)upperScrollAnimation.getValue(), 15, 15);
        } catch (Exception e) {
            RenderUtil.rect(width - 150, 5 + upperScrollAnimation.getValue(), 15, 15, Color.WHITE);
        }
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
        Keyboard.enableRepeatEvents(false);
        currentGui.onGuiClosed();
        super.onGuiClosed();
    }

    public AtomicMenu() {
        init();
        currentGui = atomicGuis.get(0);
        currentGui.initGui();
        initTime = LocalDateTime.now();
        lastGuiIndex = 0;
        setMiscGui(new MiscGui());

        Reversal.threadPoolExecutor.execute(() -> {
            try {
                headImage = SkinUtil.getResourceLocation(SkinUtil.uuidOf(GameInstance.mc.session.getUsername()));

                if (headImage == null || SkinUtil.uuidOf(GameInstance.mc.session.getUsername()).isEmpty()) {
                    headImage = SkinUtil.getResourceLocation(SkinUtil.uuidOf("Steve"));
                }

            } catch (Exception e) {
                headImage = SkinUtil.getResourceLocation(SkinUtil.uuidOf("Steve"));
            }
        });
    }

    public static void switchGui(int index) {
        lastGuiIndex = !atomicGuis.contains(currentGui) ? 0 : atomicGuis.indexOf(currentGui);
        currentGui = atomicGuis.get(index);
        atomicGuis.get(lastGuiIndex).onGuiClosed();
        currentGui.initGui();
        if (GameInstance.mc.currentScreen instanceof AtomicMenu) uiClick();
    }

    public static void setMiscGui(MiscGui miscGui) {
        atomicGuis.set(8, miscGui);
    }

    public static void setGui(int index, AtomicGui gui) {
        atomicGuis.set(index, gui);
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