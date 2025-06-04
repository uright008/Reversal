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
    private MsgBoxMark mark;
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

    @Getter
    private ArrayList<Runnable> FINISH_ACTIONS = new ArrayList<>(), OK_BUTTON_ACTIONS = new ArrayList<>(), YES_BUTTON_ACTIONS = new ArrayList<>(), NO_BUTTON_ACTIONS = new ArrayList<>();

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

        width = Math.max(150, regular24Bold.width(title) + 20);
        height = regular24Bold.height() + 35;

        for (String s : message) {
            width = Math.max(width, regular18Thin.width(s) + 35);
            height += regular18Thin.height();
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

        RenderUtil.rect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0,0,0, (int) alphaAnimation.getValue() / 2));

        if (isDragging) {
            x = mouseX - deltaX;
            y = mouseY - deltaY;
        }
        this.sr = scaledResolution;

        RenderUtil.roundedRectangle(x, y, width, height, 4f, getColor(new Color(241, 243, 249)));

    //    FontManager.getAtomic(20).drawCenteredString("2", x + 10, y + 6.5, getColor(Color.WHITE).getRGB());
        regular18Thin.drawString(title, x + 5, y + 5, getColor(Color.BLACK).getRGB());

        float renderY = 23;
        RenderUtil.rect(x, y + 16, width, regular18Thin.height() * message.size() + 8, getColor(Color.WHITE));
        for (String s : message) {
            FontManager.getRegularThin(18).drawString(s, x + 25, y + renderY, getColor(Color.BLACK).getRGB());
            renderY += regular18Thin.height();
        }

        switch (style) {
            case INFO:
                if (this.mark == null) this.mark = MsgBoxMark.INFO;
                button1Animation.run(RenderUtil.isHovered(x + width - 40, y + height - 20, 35, 13, mouseX, mouseY) ? 1 : 0);
                RenderUtil.roundedRectangle(x + width - 40, y + height - 20, 35, 13, 2f, getColor(ColorUtil.colorToColor(new Color(253, 253, 253), new Color(224, 238, 249), button1Animation)));
                RenderUtil.roundedOutlineRectangle(x + width - 40, y + height - 20, 35, 13, 2f, 0.2f, getColor(ColorUtil.colorToColor(new Color(150, 150, 150), new Color(0, 120, 212), button1Animation)));
                psr16.drawString("OK", x + width - 40 + 17.5 - psr16.width("OK") / 2f, y + height - 16, getColor(Color.BLACK).getRGB());
                break;
            case CONFIRM:
                if (this.mark == null) this.mark = MsgBoxMark.ASK;
                button1Animation.run(RenderUtil.isHovered(x + width - 80, y + height - 20, 35, 13, mouseX, mouseY) ? 1 : 0);
                RenderUtil.roundedRectangle(x + width - 80, y + height - 20, 35, 13, 2f, getColor(ColorUtil.colorToColor(new Color(253, 253, 253), new Color(224, 238, 249), button1Animation)));
                RenderUtil.roundedOutlineRectangle(x + width - 80, y + height - 20, 35, 13, 2f, 0.2f, getColor(ColorUtil.colorToColor(new Color(150, 150, 150), new Color(0, 120, 212), button1Animation)));
                psr16.drawString("Yes", x + width - 80 + 17.5 - psr16.width("Yes") / 2f, y + height - 16, getColor(ColorUtil.BLACK).getRGB());

                button2Animation.run(RenderUtil.isHovered(x + width - 40, y + height - 20, 35, 13, mouseX, mouseY) ? 1 : 0);
                RenderUtil.roundedRectangle(x + width - 40, y + height - 20, 35, 13, 2f, getColor(ColorUtil.colorToColor(new Color(253, 253, 253), new Color(224, 238, 249), button2Animation)));
                RenderUtil.roundedOutlineRectangle(x + width - 40, y + height - 20, 35, 13, 2f, 0.2f, getColor(ColorUtil.colorToColor(new Color(150, 150, 150), new Color(0, 120, 212), button2Animation)));
                psr16.drawString("No", x + width - 40 + 17.5 - psr16.width("No") / 2f, y + height - 16, getColor(ColorUtil.BLACK).getRGB());
                break;
        }

        if (!message.isEmpty()) {
            FontManager.getAtomic(32).drawString(getStringForMark(mark), x + 6, y + renderY / 2 + 4, getColor(ColorUtil.BLACK).getRGB());
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (RenderUtil.isHovered(x, y, width, 16, mouseX, mouseY)) {
            isDragging = true;
            deltaX = mouseX - x;
            deltaY = mouseY - y;
        }

        switch (style) {
            case INFO:
                if (RenderUtil.isHovered(x + width - 40, y + height - 20, 35, 13, mouseX, mouseY)) {
                    onButtonClick(1);
                }
                break;
            case CONFIRM:
                if (RenderUtil.isHovered(x + width - 40, y + height - 20, 35, 13, mouseX, mouseY)) {
                    onButtonClick(3);
                }
                if (RenderUtil.isHovered(x + width - 80, y + height - 20, 35, 13, mouseX, mouseY)) {
                    onButtonClick(2);
                }
                break;
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (isDragging) isDragging = false;
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
    }

    private void onButtonClick(int id) {
        switch (id) {
            case 1:
                OK_BUTTON_ACTIONS.forEach(Runnable::run);
                OK_BUTTON_ACTIONS.clear();
                break;
            case 2:
                YES_BUTTON_ACTIONS.forEach(Runnable::run);
                YES_BUTTON_ACTIONS.clear();
                break;
            case 3:
                NO_BUTTON_ACTIONS.forEach(Runnable::run);
                NO_BUTTON_ACTIONS.clear();
        }
        close();
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
        FINISH_ACTIONS.forEach(Runnable::run);
        FINISH_ACTIONS.clear();
    }

    private Color getColor(Color color) {
        return ColorUtil.reAlpha(color, (int) alphaAnimation.getValue());
    }

    private String getStringForMark(MsgBoxMark mark) {
        switch (mark) {
            case INFO:
                return "j";
            case ERROR:
                return "k";
            case SUCCESS:
                return "l";
            case INPUT:
                return "m";
            case ASK:
                return "n";
        }
        return "";
    }

    public enum MsgBoxStyle {
        INFO,
        CONFIRM
    }

    public enum MsgBoxMark {
        INFO,
        ERROR,
        SUCCESS,
        INPUT,
        ASK
    }
}
