package cn.stars.reversal.ui.atmoic.misc.component;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.music.ui.ThemeColor;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.animation.advanced.Direction;
import cn.stars.reversal.util.animation.advanced.composed.ColorAnimation;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.render.ColorUtil;
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

@Getter
@Setter
public class TextField {
    private float posX, posY;
    public float width, height, textMaxWidth, offsetX;
    public boolean focused, cursorRestored = false;
    public float radius;
    public String text;
    public String unfocusedText;
    private int cursorPosition;
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private boolean isPassword = false;

    public boolean canLoseFocus = true;

    public boolean selectedLine, searchIcon = false;

    private MFont font;
    public Color backgroundColor, outlineColor;
    private final ColorAnimation textColorAnim;
    private final ColorAnimation cursorColorAnim;
    private final Animation posAnimation = new Animation(Easing.EASE_OUT_EXPO, 200);
    private final Animation selectedLineAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);

    public TextField(float width, float height, MFont font, Color backgroundColor, Color outlineColor) {
        this.text = "";
        this.unfocusedText = "";
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

    // 【新增】添加一个方法来设置密码模式
    public void setPassword(boolean password) {
        this.isPassword = password;
    }

    // Call on GuiScreen.drawScreen()
    public void draw(float x, float y, int mouseX, int mouseY) {
        posX = x;
        posY = y;

        if (posAnimation.getValue() < posX - 1) {
            this.posAnimation.setValue(posX);
        }

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
        RoundedUtil.drawRound(posX + 3, posY + height - 1, width - 6, 0.8f, 1, ColorUtil.reAlpha(ColorUtil.PINK, (int)selectedLineAnimation.getValue()));

        // 【修改】如果处于密码模式，将显示的文本替换为星号
        String textToDisplay = this.isPassword ? this.text.replaceAll(".", "*") : this.text;

        String visibleText = font.getStringWidth(textToDisplay) > textMaxWidth - 3f - offsetX ? font.trimStringToWidth(textToDisplay, textMaxWidth - 3f - offsetX, true, false) : textToDisplay;
        float textX = posX + 2f + (searchIcon ? 15f : 0f);
        float textY = posY + height / 2f - 1.5f;
        float h = height * 0.6f;
        float cursorOffset = Math.min(cursorPosition, visibleText.length());
        posAnimation.run(textX + offsetX + font.getStringWidth(visibleText.substring(0, (int) cursorOffset)) + 1.2f);

        // Text
        if (visibleText.isEmpty() && !focused && !unfocusedText.isEmpty()) {
            font.drawString(unfocusedText, textX + 1.5f, textY - 1f, Color.GRAY.getRGB());
        }

        if (searchIcon) FontManager.getAtomic(24).drawString("3", textX - 13.5f, textY - 1.5f, new Color(250,250,250,200).getRGB());

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
        float currentX = posX + 2f + (searchIcon ? 15f : 0f);
        // 【修改】使用实际文本进行光标位置计算，而不是显示文本
        String calculationText = this.text;
        for (int i = 0; i < calculationText.length(); i++) {
            currentX += font.getStringWidth(calculationText.substring(i, i + 1));
            if (mouseX < currentX) {
                return i;
            }
        }
        return calculationText.length();
    }

    // Call on GuiScreen.keyTyped()
    public void keyTyped(char c, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) focused = false;

        if (!focused) return;

        try {
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
                        // 【修改】即使是密码模式，也复制真实内容
                        if (!isPassword) {
                            GuiScreen.setClipboardString(text.substring(start, end));
                        }
                    }
                } else if (keyCode == Keyboard.KEY_V) {
                    String clipboardText = GuiScreen.getClipboardString();
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
            } else if (keyCode == Keyboard.KEY_RETURN) {
                focused = false;
            }
        } catch (Exception e) {
            ReversalLogger.error("An error occurred while processing text field.", e);
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
                keyCode != Keyboard.KEY_F11 && keyCode != Keyboard.KEY_F12 &&
                keyCode != Keyboard.KEY_F13 && keyCode != Keyboard.KEY_RETURN;
    }

    public void setText(String text) {
        this.text = text;
        this.cursorPosition = text.length();
    }
}
