package cn.stars.reversal.ui.atmoic.msgbox;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.RenderUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

@Setter
public class AtomicMsgBox implements GameInstance {
    private MsgBoxStyle style;
    private String title;
    @Getter
    private final ArrayList<String> message;
    public final MsgBoxFactory FACTORY = new MsgBoxFactory(this);

    private ScaledResolution sr = new ScaledResolution(mc);
    private float x, y, deltaX, deltaY;
    private float width, height;

    private Animation alphaAnimation = new Animation(Easing.EASE_OUT_EXPO, 250);
    private Animation button1Animation = new Animation(Easing.EASE_OUT_EXPO, 500);
    private Animation button2Animation = new Animation(Easing.EASE_OUT_EXPO, 500);

    private boolean isDragging, isClosing, isLoaded;

    public AtomicMsgBox(MsgBoxStyle style, String title) {
        this.style = style;
        this.title = title;
        this.message = new ArrayList<>();

        mc.getSoundHandler().playUISound();
    }

    public AtomicMsgBox(String title) {
        this.style = MsgBoxStyle.INFO;
        this.title = title;
        this.message = new ArrayList<>();

    }

    private void init() {
        mc.getSoundHandler().playUISound();

        width = Math.max(100, regular24Bold.width(title) + 20);
        height = regular24Bold.height() + 30;

        for (String s : message) {
            width = Math.max(width, regular18.width(s) + 35);
            height += regular18.height();
        }

        this.x = sr.getScaledWidth() / 2f - width / 2;
        this.y = sr.getScaledHeight() / 2f - height / 2;

        isLoaded = true;
    }

    public void render(int mouseX, int mouseY, ScaledResolution scaledResolution) {
        if (!isLoaded) {
            init();
        }
        if (isClosing) {
            alphaAnimation.run(0);
            if (alphaAnimation.isFinished() && alphaAnimation.getDestinationValue() == 0) Reversal.atomicMsgBox = null;
        } else {
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(new GuiIngameMenu());
            }
            alphaAnimation.run(250);
        }

        if (isDragging) {
            x = mouseX - deltaX;
            y = mouseY - deltaY;
        }
        this.sr = scaledResolution;
        float finalWidth = width;
        float finalHeight = height;
        ModuleInstance.getPostProcessing().drawElementWithBlur(() -> RenderUtil.roundedRectangle(x, y, finalWidth, finalHeight, 4f, getColor(Color.BLACK)));
        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> RenderUtil.roundedRectangle(x, y, finalWidth, finalHeight, 4f, getColor(ColorUtil.empathyColor())));

        RenderUtil.roundedRectangle(x, y, width, height, 4f, getColor(ColorUtil.empathyColor()));

        FontManager.getAtomic(20).drawCenteredString("2", x + 10, y + 6.5, getColor(Color.WHITE).getRGB());
        regular20Bold.drawString(title, x + 18, y + 4.5, getColor(Color.WHITE).getRGB());

        float renderY = 23;
        for (String s : message) {
            regular18.drawString(s, x + 25, y + renderY, getColor(Color.WHITE).getRGB());
            renderY += regular18.height();
        }

        switch (style) {
            case INFO:
                if (!message.isEmpty()) {
                    FontManager.getIcon(36).drawString("t", x + 4, y + renderY / 2 + 3.5, getColor(Color.WHITE).getRGB());
                }
                button1Animation.run(RenderUtil.isHovered(x + width - 30, y + height - regular18.height() - 5, 25, regular18.height(), mouseX, mouseY) ? 250 : 150);
                RenderUtil.roundedRectangle(x + width - 30, y + height - regular18.height() - 5, 25, regular18.height(), 2f, getColor(new Color(30, 30, 30, 255), button1Animation));
                RenderUtil.roundedOutlineRectangle(x + width - 30, y + height - regular18.height() - 5, 25, regular18.height(), 3f, 0.6f, getColor(Color.WHITE, button1Animation));
                regular18.drawString("Yes", x + width - 25, y + height - regular18.height() - 2, getColor(Color.WHITE).getRGB());
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (RenderUtil.isHovered(x, y, width, 20, mouseX, mouseY)) {
            isDragging = true;
            deltaX = mouseX - x;
            deltaY = mouseY - y;
        }

        if (RenderUtil.isHovered(x + width - 30, y + height - regular18.height() - 5, 25, regular18.height(), mouseX, mouseY)) {
            close();
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (isDragging) isDragging = false;
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
    }

    protected void keyTyped(char typedChar, int keyCode)
    {
        if (keyCode == 1)
        {
            close();
        }
    }

    private int eventButton;
    private long lastMouseEvent;

    public void handleInput() throws IOException
    {
        if (Mouse.isCreated())
        {
            while (Mouse.next())
            {
                this.handleMouseInput();
            }
        }

        if (Keyboard.isCreated())
        {
            while (Keyboard.next())
            {
                this.handleKeyboardInput();
            }
        }
    }

    public void handleMouseInput() {
        int i = Mouse.getEventX() * sr.getScaledWidth() / mc.displayWidth;
        int j = sr.getScaledHeight() - Mouse.getEventY() * sr.getScaledHeight() / mc.displayHeight - 1;
        int k = Mouse.getEventButton();

        if (Mouse.getEventButtonState())
        {
            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
        }
        else if (k != -1)
        {
            this.eventButton = -1;
            this.mouseReleased(i, j, k);
        }
        else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
        {
            long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
        }
    }

    public void handleKeyboardInput()
    {
        char eventCharacter = Keyboard.getEventCharacter();
        int eventKey = Keyboard.getEventKey();
        if (Keyboard.getEventKeyState() || eventCharacter >= ' ' && eventKey == 0)
        {
            this.keyTyped(eventCharacter, eventKey);
        }

        mc.dispatchKeypresses();
    }

    public void close() {
        mc.getSoundHandler().playUISound();
        isClosing = true;
        if (mc.currentScreen == null) {
            mc.setIngameFocus();
        }
    }

    private Color getColor(Color color) {
        return ColorUtil.reAlpha(color, (int) alphaAnimation.getValue());
    }

    private Color getColor(Color color, Animation animation) {
        if (isClosing) return ColorUtil.reAlpha(color, Math.min((int) alphaAnimation.getValue(), (int) animation.getValue()));
        return ColorUtil.reAlpha(color, (int) animation.getValue());
    }

    public enum MsgBoxStyle {
        INFO,
        INPUT,
        CONFIRM
    }
}
