package cn.stars.reversal.ui.modern;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.music.ui.ThemeColor;
import cn.stars.reversal.util.animation.advanced.Direction;
import cn.stars.reversal.util.animation.advanced.composed.ColorAnimation;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

@Getter
@Setter
public class TextField {
    private float posX, posY;
    public float width, height, textMaxWidth, offsetX;
    public boolean focused, cursorRestored = false;
    public float radius;
    public String text;
    private int cursorPosition;
    private int selectionStart = -1;
    private int selectionEnd = -1;

    public boolean canLoseFocus = true;

    public boolean selectedLine = false;

    private MFont font;
    public Color backgroundColor, outlineColor;
    private final ColorAnimation textColorAnim;
    private final ColorAnimation cursorColorAnim;
    private final Animation posAnimation = new Animation(Easing.EASE_OUT_EXPO, 200);
    private final Animation selectedLineAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);

    public TextField(float width, float height, MFont font, Color backgroundColor, Color outlineColor) {
        this.text = "";
        this.width = width;
        this.textMaxWidth = width - 2f;
        this.height = height;
        this.font = font;
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;
        this.textColorAnim = new ColorAnimation(Color.WHITE, ThemeColor.greyColor, 100);
        this.cursorColorAnim = new ColorAnimation(Color.WHITE, new Color(255, 255, 255, 0), 500);
        this.radius = 3f;
        this.posAnimation.setValue(0);
        this.cursorPosition = 0;
    }

    // Call on GuiScreen.drawScreen()
    public void draw(float x, float y, int mouseX, int mouseY) {
        posX = x;
        posY = y;

        if (!canLoseFocus) focused = true;

        if (focused) {
            Keyboard.enableRepeatEvents(true);
        }

        if (focused && textColorAnim.getDirection() == Direction.FORWARDS) {
            textColorAnim.changeDirection();
        } else if (!focused && textColorAnim.getDirection() == Direction.BACKWARDS) {
            textColorAnim.changeDirection();
        }

        if (focused && cursorColorAnim.isFinished()) {
            cursorColorAnim.changeDirection();
        }

        // Windows cursor change
        if (RenderUtil.isHovered(posX, posY, width, height, mouseX, mouseY)) {
            GLFW.glfwSetCursor(Display.getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR));
            cursorRestored = false;
        } else if (!cursorRestored) {
            GLFW.glfwSetCursor(Display.getWindow(), MemoryUtil.NULL);
            cursorRestored = true;
        }

        font = GameInstance.regular16;

        // Border
        RoundedUtil.drawRoundOutline(posX, posY, width, height, radius, 0.04f, backgroundColor, outlineColor);

        selectedLineAnimation.run(selectedLine ? (focused ? 250 : RenderUtil.isHovered(posX, posY, width, height, mouseX, mouseY) ? 125 : 0) : 0);
        RoundedUtil.drawRound(posX + 3, posY + height - 1, width - 6, 0.8f, 1, new Color(0,80,250,(int) selectedLineAnimation.getValue()));

        String visibleText = font.getStringWidth(text) > textMaxWidth - 3f - offsetX ? font.trimStringToWidth(text, textMaxWidth - 3f - offsetX, true, false) : text;
        float textX = posX + 2f;
        float textY = posY + height / 2f - 1.5f;
        float h = height * 0.6f;
        float cursorOffset = Math.min(cursorPosition, visibleText.length());
        posAnimation.run(textX + offsetX + font.getStringWidth(visibleText.substring(0, (int) cursorOffset)) + 1.2f);

        // Text
        font.drawString(visibleText, textX + 1.5f, textY - 1f, textColorAnim.getOutput().getRGB());

        // Cursor
        if (focused) {
            RoundedUtil.drawRound((float) posAnimation.getValue(), posY + height / 2 - h / 2, 0.5f, h, 1f, cursorColorAnim.getOutput());
        }

        // Selection (if selected)
        if (focused && selectionStart != -1 && selectionEnd != -1 && selectionStart != selectionEnd) {
            int start = Math.min(selectionStart, selectionEnd);
            int end = Math.max(selectionStart, selectionEnd);
            if (start < 0) start = 0;
            if (end > visibleText.length()) end = visibleText.length();
            float selectionXStart = textX + font.getStringWidth(visibleText.substring(0, start));
            float selectionXEnd = textX + font.getStringWidth(visibleText.substring(0, end));
            RoundedUtil.drawRound(selectionXStart + 1.5f, posY + height / 2 - h / 2, selectionXEnd - selectionXStart - 0.5f, h, 1f, new Color(250, 250, 250, 100));
        }
    }

    // Call on GuiScreen.mouseClicked()
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovering = RenderUtil.isHovered(posX, posY, width, height, mouseX, mouseY);
        if (hovering && button == 0) {
            focused = true;
            cursorPosition = calculateCursorPosition(mouseX);
            selectionStart = cursorPosition;
            selectionEnd = cursorPosition;
        }
        if (!hovering) focused = false;
    }

    // Call on GuiScreen.mouseClickMove()
    public void mouseDragged(int mouseX, int mouseY, int button) {
        if (focused && button == 0) {
            selectionEnd = calculateCursorPosition(mouseX);
        }
    }

    private int calculateCursorPosition(int mouseX) {
        float currentX = posX + 2f;
        for (int i = 0; i < text.length(); i++) {
            currentX += font.getStringWidth(text.substring(i, i + 1));
            if (mouseX < currentX) {
                return i;
            }
        }
        return text.length();
    }

    // Call on GuiScreen.keyTyped()
    public void keyTyped(char c, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) focused = false;

        if (!focused) return;

        // Ctrl things
        if (GuiScreen.isCtrlKeyDown()) {
            if (keyCode == Keyboard.KEY_A) {
                if (!text.isEmpty()) {
                    selectionStart = 0;
                    selectionEnd = text.length();
                }
            } else if (keyCode == Keyboard.KEY_C) {
                if (selectionStart != -1 && selectionEnd != -1) {
                    int start = Math.min(selectionStart, selectionEnd);
                    int end = Math.max(selectionStart, selectionEnd);
                    if (start < 0) start = 0;
                    if (end > text.length()) end = text.length();
                    copyToClipboard(text.substring(start, end));
                }
            } else if (keyCode == Keyboard.KEY_V) {
                String clipboardText = getClipboardText();
                if (clipboardText != null) {
                    if (selectionStart != -1 && selectionEnd != -1) {
                        int start = Math.min(selectionStart, selectionEnd);
                        int end = Math.max(selectionStart, selectionEnd);
                        if (start < 0) start = 0;
                        if (end > text.length()) end = text.length();
                        text = text.substring(0, start) + clipboardText + text.substring(end);
                        cursorPosition = start + clipboardText.length();
                        selectionStart = -1;
                        selectionEnd = -1;
                    } else {
                        text = text.substring(0, cursorPosition) + clipboardText + text.substring(cursorPosition);
                        cursorPosition += clipboardText.length();
                    }
                }
            } else if (keyCode == Keyboard.KEY_Z) {
                // not implement yet > <
            }
            return;
        }

        if (keyCode == Keyboard.KEY_BACK) {
            if (selectionStart != -1 && selectionEnd != -1 && selectionStart != selectionEnd) {
                int start = Math.min(selectionStart, selectionEnd);
                int end = Math.max(selectionStart, selectionEnd);
                if (start < 0) start = 0;
                if (end > text.length()) end = text.length();
                text = text.substring(0, start) + text.substring(end);
                cursorPosition = start;
                selectionStart = -1;
                selectionEnd = -1;
            } else if (cursorPosition > 0) {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
            }
        } else if (keyCode == Keyboard.KEY_LEFT) {
            if (cursorPosition > 0) {
                cursorPosition--;
                selectionStart = -1;
                selectionEnd = -1;
            }
        } else if (keyCode == Keyboard.KEY_RIGHT) {
            if (cursorPosition < text.length()) {
                cursorPosition++;
                selectionStart = -1;
                selectionEnd = -1;
            }
        } else if (isPrintableKey(keyCode)) {
            if (c == '\u0000') return;
            if (selectionStart != -1 && selectionEnd != -1 && selectionStart != selectionEnd) {
                int start = Math.min(selectionStart, selectionEnd);
                int end = Math.max(selectionStart, selectionEnd);
                if (start < 0) start = 0;
                if (end > text.length()) end = text.length();
                text = text.substring(0, start) + c + text.substring(end);
                cursorPosition = start + 1;
                selectionStart = -1;
                selectionEnd = -1;
            } else {
                text = text.substring(0, cursorPosition) + c + text.substring(cursorPosition);
                cursorPosition++;
            }
        }
    }

    private boolean isPrintableKey(int keyCode) {
        // Check if 'c' is undefined characters
        return keyCode != Keyboard.KEY_LSHIFT && keyCode != Keyboard.KEY_RSHIFT &&
                keyCode != Keyboard.KEY_LCONTROL && keyCode != Keyboard.KEY_RCONTROL &&
                keyCode != Keyboard.KEY_LMENU && keyCode != Keyboard.KEY_RMENU &&
                keyCode != Keyboard.KEY_TAB && keyCode != Keyboard.KEY_ESCAPE &&
                keyCode != Keyboard.KEY_F1 && keyCode != Keyboard.KEY_F2 &&
                keyCode != Keyboard.KEY_F3 && keyCode != Keyboard.KEY_F4 &&
                keyCode != Keyboard.KEY_F5 && keyCode != Keyboard.KEY_F6 &&
                keyCode != Keyboard.KEY_F7 && keyCode != Keyboard.KEY_F8 &&
                keyCode != Keyboard.KEY_F9 && keyCode != Keyboard.KEY_F10 &&
                keyCode != Keyboard.KEY_F11 && keyCode != Keyboard.KEY_F12;
    }

    private void copyToClipboard(String str) {
        StringSelection stringSelection = new StringSelection(str);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private String getClipboardText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setText(String text) {
        this.text = text;
        this.cursorPosition = text.length();
    }
}

