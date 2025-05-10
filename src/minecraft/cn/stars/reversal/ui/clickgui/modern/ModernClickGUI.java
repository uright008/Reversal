package cn.stars.reversal.ui.clickgui.modern;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.ValueChangedEvent;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.module.impl.render.ClickGui;
import cn.stars.reversal.ui.atmoic.misc.GUIBubble;
import cn.stars.reversal.ui.atmoic.misc.component.TextField;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.*;
import cn.stars.reversal.value.Value;
import cn.stars.reversal.value.impl.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import static cn.stars.reversal.GameInstance.*;

public class ModernClickGUI extends GuiScreen {
    public final Color backgroundColor = new Color(20,20,20,255);
    public Animation scaleAnimation = new Animation(Easing.EASE_OUT_EXPO, 300);
    private final Animation sideAnimation = new Animation(Easing.EASE_OUT_EXPO, 300);
    private Category selectedCategory = Category.COMBAT;
    private static float scrollAmount;
    private final ArrayList<GUIBubble> bubbles = new ArrayList<>();

    // Values
    private NumberValue selectedSlider;
    private ColorValue selectedColor;
    private TextValue selectedText;
    private boolean hueFlag = false;
    private boolean themeColorFlag = false;
    private boolean hasEditedSliders = false;

    private boolean isEditingKey = false;
    private Module editingModule;

    private final TimeUtil timer = new TimeUtil();
    private float wheel = Mouse.getDWheel();
    private final TextField searchField = new TextField(150, 15, GameInstance.regular16, backgroundColor, new Color(100,100,100,100));
    private TextField textField = new TextField(300, 12, GameInstance.regular16, backgroundColor, new Color(100,100,100,100));

    // Drag & Click
    private int addX, addY, deltaX, deltaY = 0;
    private boolean isDragging;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scaleAnimation.run(1.0);

        int x = width / 2 - 260 + addX;
        int y = height / 2 - 180 + addY;

        boolean localization = ModuleInstance.getClientSettings().localization.enabled;
        Color valueColor = ModuleInstance.getModule(ClickGui.class).customColor.enabled ? ModuleInstance.getModule(ClickGui.class).colorValue.getColor() : new Color(100, 200, 255, 250);

        if (isDragging) {
            addX = mouseX - deltaX;
            addY = mouseY - deltaY;
            sideAnimation.finishNow();
        }

    //    GlUtils.startScale(x, y, (float) scaleAnimation.getValue());
        RenderUtil.scaleStart(x + 260, y + 180, (float) scaleAnimation.getValue());

        // Background
        RoundedUtil.drawRound(x, y, 520, 360, 5, backgroundColor);

        // Client Name
        atomic24.drawString("2", x + 8, y + 14, new Color(200,200,200,250).getRGB());
        psm30.drawString("REVERSAL", x + 25, y + 12, new Color(200,200,200,250).getRGB());
        psr16.drawString(Reversal.VERSION, x + 82, y + 25, new Color(200,200,200,200).getRGB());

        // Line
        RenderUtil.rectangle(x + 115, y, 0.8, 360, new Color(100,100,100,100));
    //    RenderUtil.rectangle(x + 5, y + 62, 105, 0.7, new Color(100,100,100,100));

        // Shadow
        if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled && scaleAnimation.isFinished()) {
            MODERN_BLOOM_RUNNABLES.add(() -> RoundedUtil.drawRound(x, y, 520, 360, 5, backgroundColor));
        }

        // Category
        psm16.drawString(localization ? I18n.format("category.title") : "CATEGORIES", x + 3, y + 40, new Color(200,200,200,200).getRGB());
        int renderSelectY = y + 50;
        for (final Category category : Category.values()) {
            if (category == selectedCategory) {
                sideAnimation.run(renderSelectY);
                RenderUtil.roundedRectangle(x + 5, (float) sideAnimation.getValue() + 1, 100, 18, 5, ColorUtil.reAlpha(valueColor, 120));
                cur26.drawString(Category.getCategoryIcon(category), x + 10, renderSelectY + 7, new Color(200,200,200, 240).getRGB());
                regular20.drawString(localization ? I18n.format("category." + category.name() + ".name") : StringUtils.capitalize(category.name().toLowerCase()), x + 28, renderSelectY + 7, new Color(240,240,240, 240).getRGB());
            } else {
                if (RenderUtil.isHovered(x + 5, renderSelectY, 100, 20, mouseX, mouseY)) {
                    category.alphaAnimation.run(80);
                } else {
                    category.alphaAnimation.run(0);
                }
                RoundedUtil.drawRound(x + 5, renderSelectY + 1, 100, 18, 5, new Color(50,50,50,(int)category.alphaAnimation.getValue()));
                cur26.drawString(Category.getCategoryIcon(category), x + 10, renderSelectY + 7, new Color(160, 160, 160, 200).getRGB());
                regular20.drawString(localization ? I18n.format("category." + category.name() + ".name") : StringUtils.capitalize(category.name().toLowerCase()), x + 28, renderSelectY + 7, new Color(200,200,200, 200).getRGB());
            }
            renderSelectY += 25;

            if (isDragging) category.alphaAnimation.finishNow();
        }

        // Module
        Module firstModule = null;
        float lastModuleY = 0;

        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        float moduleX = x + 120;
        float moduleY = y + 35 + scrollAmount;
        float settingX = x + 125;
        float settingY = y + 55 + scrollAmount;
        RenderUtil.scissor(moduleX, y, 400, 360);
        for (final Module m : Reversal.moduleManager.getModuleList()) {
            if ((m.getModuleInfo().category() == selectedCategory && searchField.text.isEmpty()) || getRelevantModules(searchField.text).contains(m)) {
                m.guiX = moduleX;
                m.guiY = moduleY;
                if (firstModule == null) {
                    firstModule = m;
                }
                lastModuleY = Math.max(lastModuleY, m.guiY + m.sizeInGui);
                if (m.yAnimation.getValue() >= y && m.yAnimation.getValue() + m.sizeAnimation.getValue() <= y + 360) {
                    RenderUtil.scissor(m.guiX - 1, m.yAnimation.getValue(), 391, m.sizeAnimation.getValue() - 5);
                } else {
                    RenderUtil.scissor(moduleX, y, 400, 360);
                }
                if (localization) {
                    if (ModuleInstance.getModule(ClientSettings.class).isNonAlphabeticLanguage()) {
                        regular24Bold.drawString(I18n.format(m.getModuleInfo().localizedName()), m.guiX + 20 + m.posAnimation.getValue(), m.yAnimation.getValue() + 5.5, m.isEnabled() ? new Color(240, 240, 240, 240).getRGB() : new Color(160, 160, 160, 200).getRGB());
                        regular16.drawString(I18n.format(m.getModuleInfo().localizedDescription()),
                                m.guiX + 20 + m.posAnimation.getValue(), m.yAnimation.getValue() + 21, new Color(160, 160, 160, 160).getRGB());
                    } else {
                        psm24.drawString(I18n.format(m.getModuleInfo().localizedName()), m.guiX + 20 + m.posAnimation.getValue(), m.yAnimation.getValue() + 6, m.isEnabled() ? new Color(240, 240, 240, 240).getRGB() : new Color(160, 160, 160, 200).getRGB());
                        psm16.drawString(I18n.format(m.getModuleInfo().localizedDescription()),
                                m.guiX + 20 + m.posAnimation.getValue(), m.yAnimation.getValue() + 20, new Color(160, 160, 160, 160).getRGB());
                    }
                } else {
                    psm24.drawString(m.getModuleInfo().name(), m.guiX + 20 + m.posAnimation.getValue(), m.yAnimation.getValue() + 6, m.isEnabled() ? new Color(240, 240, 240, 240).getRGB() : new Color(160, 160, 160, 200).getRGB());
                    psm16.drawString(m.getModuleInfo().description(),
                            m.guiX + 20 + m.posAnimation.getValue(), m.yAnimation.getValue() + 20, new Color(160, 160, 160, 160).getRGB());
                }
                if (m.expanded || (!m.sizeAnimation.isFinished() && m.yAnimation.isFinished())) {
                    m.sizeInGui = 20;
                    settingY += m.sizeInGui;
                    if (m != firstModule) settingY += 15; // IDK why
                    else settingY -= 5;
                    
                    
                    for (final Value setting : m.getSettings()) {
                        if (!setting.isHidden()) {
                            String settingName = localization ? I18n.format(setting.localizedName) : setting.name;
                            int addY = localization && setting.localizedName.startsWith("value.") && ModuleInstance.getClientSettings().isNonAlphabeticLanguage() ? 1 : 0;
                            if (setting instanceof NoteValue) {
                                psr18.drawString(settingName, setting.guiX, setting.yAnimation.getValue() - 15, new Color(150, 150, 150, 150).getRGB());
                                settingY += 12;
                                m.sizeInGui += 12;
                            }
                            if (setting instanceof CustomValue) {
                                CustomValue customValue = (CustomValue) setting;
                                customValue.hoverAnimation.run(RenderUtil.isHovered(setting.guiX, setting.yAnimation.getValue() - 15, psr18.width(settingName) + 10, 12, mouseX, mouseY) ? 40 : 0);
                                psr18.drawString(settingName, setting.guiX, setting.yAnimation.getValue() - 15,
                                        new Color(200 + (int) customValue.hoverAnimation.getValue(), 200 + (int) customValue.hoverAnimation.getValue(), 200 + (int) customValue.hoverAnimation.getValue(), 200 + (int) customValue.hoverAnimation.getValue()).getRGB());
                                settingY += 12;
                                m.sizeInGui += 12;
                            }
                            if (setting instanceof TextValue) {
                                TextValue textValue = (TextValue) setting;
                                psr18.drawString(settingName, setting.guiX, setting.yAnimation.getValue() - 15, new Color(200, 200, 200, 200).getRGB());
                                if (selectedText == setting) {
                                    textField.draw(setting.guiX + 5 + psr18.width(settingName), (float) setting.yAnimation.getValue() - 17.5f, mouseX, mouseY);
                                    new ValueChangedEvent(m, setting).call();
                                } else {
                                    psr18.drawString(textValue.text, setting.guiX + 5 + psr18.width(settingName), setting.yAnimation.getValue() - 15 + addY, new Color(240, 240, 240, 240).getRGB());
                                }
                                settingY += 12;
                                m.sizeInGui += 12;
                            }
                            if (setting instanceof BoolValue) {
                                psr18.drawString(settingName, setting.guiX, setting.yAnimation.getValue() - 15, new Color(200, 200, 200, 200).getRGB());
                                RenderUtil.roundedOutlineRectangle(setting.guiX + 5 + psr18.width(settingName), setting.yAnimation.getValue() - 15.5 + addY, 8, 8, 4, 0.5, valueColor);
                                if (((BoolValue) setting).isEnabled())
                                    RenderUtil.roundedRectangle(setting.guiX + 6.5 + psr18.width(settingName), setting.yAnimation.getValue() - 14 + addY, 5, 5, 2.5, valueColor);
                                settingY += 12;
                                m.sizeInGui += 12;
                            }
                            if (setting instanceof NumberValue) {
                                psr18.drawString(settingName, setting.guiX, setting.yAnimation.getValue() - 15, new Color(200, 200, 200, 200).getRGB());
                                NumberValue settingValue = (NumberValue) setting;
                                float fontWidth = psr18.getWidth(settingName) + 5;
                                if (selectedSlider == setting) {

                                    final double percent = (mouseX - (setting.guiX + fontWidth)) / (double) (100);
                                    double value = settingValue.minimum - percent * (settingValue.minimum - settingValue.maximum);

                                    if (value > settingValue.maximum) value = settingValue.maximum;
                                    if (value < settingValue.minimum) value = settingValue.minimum;

                                    settingValue.value = value;

                                    if (settingValue.getIncrement() != 0)
                                        selectedSlider.value = round(value, (float) settingValue.increment);
                                    else settingValue.value = value;

                                    hasEditedSliders = true;
                                    new ValueChangedEvent(m, setting).call();
                                }

                                settingValue.percentage = (((NumberValue) setting).value - ((NumberValue) setting).minimum) / (((NumberValue) setting).maximum - ((NumberValue) setting).minimum);

                                String value = String.valueOf((float) round(settingValue.value, (float) settingValue.increment));

                                if (settingValue.increment == 1) {
                                    value = value.replace(".0", "");
                                }

                                if (settingValue.getReplacements() != null) {
                                    for (final String replacement : settingValue.getReplacements()) {
                                        final String[] split = replacement.split("-");
                                        value = value.replace(split[0], split[1]);
                                    }
                                }

                                RenderUtil.roundedRectangle(setting.guiX + fontWidth, setting.yAnimation.getValue() - 13 + addY, 100, 2, 1, new Color(200, 200, 200, 200));
                                RenderUtil.roundedRectangle(setting.guiX + fontWidth, setting.yAnimation.getValue() - 13 + addY, settingValue.renderPercentage * 100 + 1, 2, 1, valueColor);
                                RenderUtil.roundedRectangle(setting.guiX + fontWidth + settingValue.renderPercentage * 100, setting.yAnimation.getValue() - 14.5 + addY, 5, 5, 2.5, valueColor);
                                psr18.drawString(value, setting.guiX + fontWidth + 109, setting.yAnimation.getValue() - 15 + addY, new Color(240, 240, 240, 240).getRGB());
                                settingY += 12;
                                m.sizeInGui += 12;
                            }
                            if (setting instanceof ColorValue) {
                                ColorValue colorValue = (ColorValue) setting;
                                float fontWidth = psr18.getWidth(settingName) + 5;
                                if (selectedColor == setting) {
                                    psr18.drawString(settingName, setting.guiX, setting.yAnimation.getValue() - (colorValue.isDontShowThemeColor() ? 70 : 80), new Color(200, 200, 200, 200).getRGB());
                                    float[] hsb = {colorValue.getHue(), colorValue.getSaturation(), colorValue.getBrightness()};
                                    float gradientX = setting.guiX + fontWidth;
                                    float gradientY = (float) (setting.yAnimation.getValue() - (colorValue.isDontShowThemeColor() ? 70 : 80)) + addY;
                                    float gradientWidth = 97;
                                    float gradientHeight = 50;
                                    Gui.drawRect2(gradientX, gradientY, gradientWidth, gradientHeight, Color.getHSBColor(hsb[0], 1, 1).getRGB());
                                    Gui.drawGradientRectSideways2(gradientX, gradientY, gradientWidth, gradientHeight, Color.getHSBColor(hsb[0], 0, 1).getRGB(),
                                            ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 0, 1).getRGB(), 0));

                                    Gui.drawGradientRect2(gradientX, gradientY, gradientWidth, gradientHeight,
                                            ColorUtil.applyOpacity(Color.getHSBColor(hsb[0], 1, 0).getRGB(), 0), Color.getHSBColor(hsb[0], 1, 0).getRGB());

                                    float pickerY = gradientY + (gradientHeight * (1 - hsb[2]));
                                    float pickerX = gradientX + (gradientWidth * hsb[1] - 1);
                                    pickerY = Math.max(Math.min(gradientY + gradientHeight - 2, pickerY), gradientY);
                                    pickerX = Math.max(Math.min(gradientX + gradientWidth - 2, pickerX), gradientX);

                                    GL11.glEnable(GL11.GL_BLEND);
                                    RenderUtil.color(-1);
                                    mc.getTextureManager().bindTexture(new ResourceLocation("reversal/images/colorpicker.png"));
                                    Gui.drawModalRectWithCustomSizedTexture(pickerX, pickerY, 0, 0, 4, 4, 4, 4);


                                    GlStateManager.color(1, 1, 1, 1);
                                    Gui.drawRect2(gradientX + gradientWidth + 5, gradientY, 5, gradientHeight, colorValue.getColor().getRGB());

                                    psr16.drawString("(" + String.format("#%02X%02X%02X", colorValue.getColor().getRed(), colorValue.getColor().getGreen(), colorValue.getColor().getBlue()) + ")",
                                            gradientX + gradientWidth + 13, gradientY + 2, colorValue.getColor().getRGB());


                                    // Hue bar
                                    RenderUtil.color(-1);
                                    mc.getTextureManager().bindTexture(new ResourceLocation("reversal/images/hue.png"));
                                    Gui.drawModalRectWithCustomSizedTexture(gradientX + 0.5f, gradientY + gradientHeight + 4.5f, 0, 0, gradientWidth + 10, 4, gradientWidth + 10, 4);
                                    GlStateManager.color(1, 1, 1, 1);

                                    //Hue slider
                                    Gui.drawRect2(gradientX + ((gradientWidth + 10) * hsb[0]) + 0.5, gradientY + gradientHeight + 3.5, 1, 6, -1);

                                    if (!colorValue.isDontShowThemeColor()) {
                                        psr18.drawString("Follow Theme", gradientX, gradientY + gradientHeight + 14, new Color(200, 200, 200, 200).getRGB());
                                        RenderUtil.roundedOutlineRectangle(gradientX + psr18.width("Follow Theme") + 5, gradientY + gradientHeight + 13, 8, 8, 4, 0.5, valueColor);
                                        if (colorValue.isThemeColor())
                                            RenderUtil.roundedRectangle(gradientX + psr18.width("Follow Theme") + 6.5, gradientY + gradientHeight + 14.5, 5, 5, 2.5, valueColor);
                                    }

                                    if (Mouse.isButtonDown(0)) {
                                        if (RenderUtil.isHovered(gradientX, gradientY + gradientHeight + 3, (gradientWidth + 10), 6, mouseX, mouseY)) {
                                            hueFlag = true;
                                        } if (RenderUtil.isHovered(gradientX, gradientY, gradientWidth, gradientHeight, mouseX, mouseY)) {
                                            hueFlag = false;
                                        }

                                        if (RenderUtil.isHovered(gradientX, gradientY + gradientHeight + 13, 100, psr18.height(), mouseX, mouseY) && !colorValue.isDontShowThemeColor()) {
                                            if (!themeColorFlag) colorValue.setThemeColor(!colorValue.isThemeColor());
                                            themeColorFlag = true;
                                        } else if (RenderUtil.isHovered(gradientX - 10, gradientY - 10, 200, gradientHeight + 24, mouseX, mouseY)) {
                                            if (hueFlag) {
                                                colorValue.setHue(Math.min(1, Math.max(0, (mouseX - gradientX - 0.5f) / (gradientWidth + 10))));
                                            } else {
                                                colorValue.setBrightness(Math.min(1, Math.max(0, 1 - ((mouseY - gradientY) / gradientHeight))));
                                                colorValue.setSaturation(Math.min(1, Math.max(0, (mouseX - gradientX) / gradientWidth)));
                                            }
                                        }
                                        new ValueChangedEvent(m, setting).call();
                                    } else {
                                        themeColorFlag = false;
                                    }

                                    settingY += (colorValue.isDontShowThemeColor() ? 70 : 80);
                                    m.sizeInGui += (colorValue.isDontShowThemeColor() ? 70 : 80);
                                } else {
                                    psr18.drawString(settingName, setting.guiX, setting.yAnimation.getValue() - 15, new Color(200, 200, 200, 200).getRGB());
                                    RenderUtil.roundedRectangle(setting.guiX + fontWidth, setting.yAnimation.getValue() - 16 + addY, 8, 8, 2, colorValue.getColor());
                                    psr16.drawString("(" + String.format("#%02X%02X%02X", colorValue.getColor().getRed(), colorValue.getColor().getGreen(), colorValue.getColor().getBlue()) + ")",
                                            setting.guiX + fontWidth + 12, setting.yAnimation.getValue() - 14.5 + addY, colorValue.getColor().getRGB());
                                    settingY += 12;
                                    m.sizeInGui += 12;
                                }
                            }
                            if (setting instanceof ModeValue) {
                                ModeValue modeValue = (ModeValue) setting;
                                float offset = modeValue.expanded ? modeValue.modes.size() * 12 + 10 : 0;
                                float fontWidth = psr18.getWidth(settingName) + 5;
                                if (modeValue.expanded) {
                                    float maxWidth = 0;
                                    double posY = setting.yAnimation.getValue() - offset;
                                    for (String s : modeValue.getModes()) {
                                        if (Mouse.isButtonDown(0) && setting.yAnimation.isFinished()) {
                                            if (RenderUtil.isHovered(setting.guiX - 10, posY, 100, 12, mouseX, mouseY)) {
                                                modeValue.setIndex(modeValue.getModes().indexOf(s));
                                                new ValueChangedEvent(m, setting).call();
                                            }
                                        }
                                        psr18.drawString(s, setting.guiX + fontWidth, posY + 1, new Color(200, 200, 200, 200).getRGB());
                                        RenderUtil.roundedOutlineRectangle(setting.guiX + fontWidth - 12, posY, 8, 8, 4, 0.5, valueColor);
                                        if (s.equals(modeValue.getMode())) {
                                            RenderUtil.roundedRectangle(setting.guiX + fontWidth - 10.5, posY + 1.5, 5, 5, 2.5, valueColor);
                                        }
                                        maxWidth = Math.max(maxWidth, psr18.width(s) + 25);
                                        posY += 12;
                                    }
                                    settingY += offset;
                                    m.sizeInGui += offset;
                                }
                                psr18.drawString(settingName, setting.guiX, setting.yAnimation.getValue() - 15 - offset, new Color(200, 200, 200, 200).getRGB());
                                psr18.drawString(modeValue.getModes().get(modeValue.index),
                                        setting.guiX + 5 + psr18.width(settingName), setting.yAnimation.getValue() - 15 + addY - offset, new Color(240, 240, 240, 240).getRGB());
                                icon16.drawString(modeValue.expanded ? "h" : "i", setting.guiX + 370, setting.yAnimation.getValue() - 12.5 - offset, new Color(200,200,200,200).getRGB());
                                settingY += 12;
                                m.sizeInGui += 12;
                            }
                            setting.guiX = settingX;
                            setting.guiY = settingY;
                            setting.yAnimation.run(setting.guiY);
                        }
                        if (isDragging) {
                            setting.yAnimation.finishNow();
                        }
                    }
                }
                if (!m.expanded) {
                    m.sizeInGui = 20;
                }
                m.sizeInGui += 15;
                m.sizeAnimation.run(m.sizeInGui);
                m.yAnimation.run(m.guiY);

                // Keybinding
                String keyName = isEditingKey && editingModule == m ? "LISTENING..." : Keyboard.getKeyName(m.getKeyBind());
                RenderUtil.roundedOutlineRectangle(m.guiX + 340 - psr16.width(keyName) / 2f, m.yAnimation.getValue() + 9,
                        4.5 + psr16.width(keyName), 12, 2, 0.7, new Color(160, 160, 160, 160));
                psr16.drawString(keyName, m.guiX + 342 - psr16.width(keyName) / 2f,
                        m.yAnimation.getValue() + 12.5, new Color(160,160,160,160).getRGB());

                if (!m.getSettings().isEmpty()) {
                    icon20.drawString(m.expanded ? "h" : "i", m.guiX + 375, m.yAnimation.getValue() + 14, new Color(160, 160, 160, 160).getRGB());
                }

            //    RenderUtil.roundedRectangle(m.guiX - 0.5, m.yAnimation.getValue() + 10, 1, 10, 1, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));
                RenderUtil.roundedRectangle(m.guiX, m.yAnimation.getValue(), 390, m.sizeAnimation.getValue() - 5, 3, new Color(80,80,80, (int) (40 + m.alphaAnimation.getValue())));
                RenderUtil.roundedRectangle(m.guiX + 8, m.yAnimation.getValue() + 12, 6, 6, 3, m.isEnabled() ? valueColor : new Color(160, 160, 160, 200));

                settingY = moduleY + m.sizeInGui;
                moduleY += m.sizeInGui;

                try {
                    if (ModuleInstance.getModule(ClickGui.class).bubbles.enabled) {
                        for (GUIBubble bubble : bubbles) {
                            if (bubble.shouldRemove()) {
                                bubbles.remove(bubble);
                            } else {
                                if (bubble.x > m.guiX - 1 && bubble.x < m.guiX + 390 && bubble.y > m.yAnimation.getValue() && bubble.y < m.yAnimation.getValue() + m.sizeAnimation.getValue() - 5)
                                    bubble.render();
                            }
                        }
                    } else {
                        bubbles.clear();
                    }
                } catch (ConcurrentModificationException ignored) {}

                if (isDragging) {
                    m.alphaAnimation.finishNow();
                    m.yAnimation.finishNow();
                    m.posAnimation.finishNow();
                    m.sizeAnimation.finishNow();
                }
            }
        }

        RenderUtil.scissor(x, y, 520, 360);
        RenderUtil.rect(x + 120, y - 1, 398, 30, backgroundColor);
        String titleText = searchField.text.isEmpty() ? localization ? I18n.format("category." + selectedCategory.name() + ".name") : StringUtils.capitalize(selectedCategory.name().toLowerCase()) : "\"" + psm20.trimStringToWidth(searchField.text, 160, false,true) + "\"";
        psm20.drawString(titleText, x + 125, y + 10, new Color(200,200,200,240).getRGB());
        icon16.drawString("i", x + 126 + psm24.width(titleText), y + 13, new Color(200,200,200,240).getRGB());
        RenderUtil.rectangle(x + 116, y + 25, 404, 0.8, new Color(100,100,100,100));
        searchField.draw(x + 360, y + 5, mouseX, mouseY);
        searchField.setSelectedLine(true);

        RenderUtil.renderPlayerModelTexture(x + 6, y + 334, 3, 3, 3, 3, 20, 20, 24, 24, mc.thePlayer);
        psr16.drawString(mc.session.getUsername(), x + 30, y + 337, new Color(200,200,200,240).getRGB());
        psr16.drawString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")), x + 30, y + 347, new Color(200,200,200,200).getRGB());

        if (timer.hasReached(20)) {
            timer.reset();
            for (final Module m : Reversal.moduleManager.getModuleList()) {
                if (m.isEnabled()) {
                    m.alphaAnimation.run(80);
                } else if (RenderUtil.isHovered(m.guiX, m.yAnimation.getValue(), 390, m.sizeAnimation.getValue() - 5, mouseX, mouseY)) {
                    m.alphaAnimation.run(40);
                } else {
                    m.alphaAnimation.run(0);
                }
                if (m.expanded && !m.getSettings().isEmpty()) m.posAnimation.run(5);
                else m.posAnimation.run(0);
                for (final Value s : m.getSettings()) {
                    if (s instanceof NumberValue) {
                        final NumberValue value = (NumberValue) s;

                        if (hasEditedSliders) {
                            value.renderPercentage = (value.renderPercentage + value.percentage) / 2;
                        } else {
                            value.renderPercentage = (value.renderPercentage * 4 + value.percentage) / 5;
                        }

                    }
                    if (s instanceof TextValue) {
                        if (selectedText == s) {
                            final TextValue value = (TextValue) s;
                            value.text = textField.text;
                        }
                    }
                }
            }

            if (selectedText != null && !textField.isFocused()) {
                selectedText = null;
            }

            if (firstModule != null && firstModule.guiY - y >= 40) {
                scrollAmount *= 0.86f;
            }

            if (firstModule != null && lastModuleY - y - 320 < -40 && !(lastModuleY == firstModule.guiY)) {
                if (!(lastModuleY - firstModule.guiY < 280)) scrollAmount *= 0.99f;
                else scrollAmount = -5;
            }
        }

        GlStateManager.popMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    //    GlUtils.stopScale();

        RenderUtil.scaleEnd();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        searchField.mouseDragged(mouseX, mouseY, clickedMouseButton);
        if (selectedText != null) {
            textField.mouseDragged(mouseX, mouseY, clickedMouseButton);
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int x = width / 2 - 260 + addX;
        int y = height / 2 - 160 + addY;
        boolean localization = ModuleInstance.getClientSettings().localization.enabled;

        searchField.mouseClicked(mouseX, mouseY, mouseButton);

        if (selectedText != null) {
            textField.mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (RenderUtil.isHovered(x, y - 20, 520, 25, mouseX, mouseY) && !searchField.focused) {
            isDragging = true;
            deltaX = mouseX - addX;
            deltaY = mouseY - addY;
        }

        if (RenderUtil.isHovered(x + 5, y + 30, 105, 20, mouseX, mouseY)) {
            setSelectedCategory(Category.COMBAT);
        }
        if (RenderUtil.isHovered(x + 5, y + 55, 105, 20, mouseX, mouseY)) {
            setSelectedCategory(Category.MOVEMENT);
        }
        if (RenderUtil.isHovered(x + 5, y + 80, 105, 20, mouseX, mouseY)) {
            setSelectedCategory(Category.PLAYER);
        }
        if (RenderUtil.isHovered(x + 5, y + 105, 105, 20, mouseX, mouseY)) {
            setSelectedCategory(Category.RENDER);
        }
        if (RenderUtil.isHovered(x + 5, y + 130, 105, 20, mouseX, mouseY)) {
            setSelectedCategory(Category.MISC);
        }
        if (RenderUtil.isHovered(x + 5, y + 155, 105, 20, mouseX, mouseY)) {
            setSelectedCategory(Category.WORLD);
        }
        if (RenderUtil.isHovered(x + 5, y + 180, 105, 20, mouseX, mouseY)) {
            setSelectedCategory(Category.HUD);
        }
        if (RenderUtil.isHovered(x + 5, y + 205, 105, 20, mouseX, mouseY)) {
            setSelectedCategory(Category.ADDONS);
        }
        if (RenderUtil.isHovered(x + 5, y + 230, 105, 20, mouseX, mouseY)) {
            setSelectedCategory(Category.CLIENT);
        }

        float moduleX = x + 120;
        for (final Module m : Reversal.moduleManager.getModuleList()) {
            if ((m.getModuleInfo().category() == selectedCategory && searchField.text.isEmpty()) || getRelevantModules(searchField.text).contains(m)) {
                if (RenderUtil.isHovered(moduleX, m.guiY, 390, 30, mouseX, mouseY)) {
                    if (m.guiY + psm24.height() < y || m.guiY + psm24.height() > y + 360) return;
                    if (mouseButton == 1) {
                        if (!RenderUtil.isHovered(moduleX + 335 - psr16.width(Keyboard.getKeyName(m.getKeyBind())) / 2f, m.guiY, psr16.width(Keyboard.getKeyName(m.getKeyBind())) + 10, 30, mouseX, mouseY)) {
                            m.expanded = !m.expanded;
                        }
                    }
                    if (mouseButton == 0) {
                        if (!RenderUtil.isHovered(moduleX + 335 - psr16.width(Keyboard.getKeyName(m.getKeyBind())) / 2f, m.guiY, psr16.width(Keyboard.getKeyName(m.getKeyBind())) + 10, 30, mouseX, mouseY)) {
                            if (RenderUtil.isHovered(moduleX + 370, m.guiY, 20, 30, mouseX, mouseY)) m.expanded = !m.expanded;
                            else m.toggleModule();
                        }
                    }
                    if (RenderUtil.isHovered(moduleX + 335 - psr16.width(Keyboard.getKeyName(m.getKeyBind())) / 2f, m.guiY, psr16.width(Keyboard.getKeyName(m.getKeyBind())) + 10, 30, mouseX, mouseY)) {
                        if (isEditingKey) {
                            m.setKeyBind(Keyboard.KEY_NONE);
                            isEditingKey = false;
                        } else {
                            isEditingKey = true;
                        }
                        editingModule = m;
                    }
                }
                if (RenderUtil.isHovered(moduleX, m.guiY, 390, m.sizeAnimation.getValue() - 5, mouseX, mouseY)) {
                    bubbles.add(new GUIBubble(mouseX, mouseY, 10, 15));
                }
                for (final Value setting : m.getSettings()) {
                    if (m.expanded) {
                        if (!setting.isHidden()) {
                            String settingName = localization ? I18n.format(setting.localizedName) : setting.name;
                            if (setting instanceof NoteValue) {
                                if (RenderUtil.isHovered(setting.guiX + psr18.width(settingName), setting.guiY - 15, 300, 12, mouseX, mouseY)) {
                                    new ValueChangedEvent(m, setting).call();
                                }
                            }
                            if (setting instanceof CustomValue) {
                                if (RenderUtil.isHovered(setting.guiX, setting.guiY - 15, psr18.width(settingName) + 10, 12, mouseX, mouseY)) {
                                    ((CustomValue) setting).runnable.run();
                                    new ValueChangedEvent(m, setting).call();
                                }
                            }
                            if (setting instanceof TextValue) {
                                if (RenderUtil.isHovered(setting.guiX + psr18.width(settingName), setting.guiY - 15, 300, 12, mouseX, mouseY)) {
                                    if (selectedText != setting) {
                                        selectedText = (TextValue) setting;
                                        textField = new TextField(370 - psr18.width(settingName), 12, GameInstance.regular16, backgroundColor, new Color(100, 100, 100, 100));
                                        textField.setText(((TextValue) setting).text);
                                        textField.setFocused(true);
                                        textField.setSelectedLine(true);
                                    }
                                    new ValueChangedEvent(m, setting).call();
                                }
                            }
                            if (setting instanceof BoolValue) {
                                if (RenderUtil.isHovered(setting.guiX + psr18.width(settingName), setting.guiY - 15, 200, 12, mouseX, mouseY) && mouseButton == 0) {
                                    ((BoolValue) setting).toggle();
                                    new ValueChangedEvent(m, setting).call();
                                }
                            }
                            if (setting instanceof NumberValue) {
                                if (RenderUtil.isHovered(setting.guiX + psr18.width(settingName), setting.guiY - 15, 110, 12, mouseX, mouseY) && mouseButton == 0) {
                                    selectedSlider = (NumberValue) setting;
                                    new ValueChangedEvent(m, setting).call();
                                }
                            }
                            if (setting instanceof ColorValue) {
                                if (selectedColor == setting) {
                                    if (RenderUtil.isHovered(setting.guiX + psr18.width(settingName), setting.guiY - 80, 300, 80, mouseX, mouseY) && mouseButton == 1) {
                                        selectedColor = null;
                                        return;
                                    }
                                }
                                if (RenderUtil.isHovered(setting.guiX + psr18.width(settingName), setting.guiY - 15, 300, 12, mouseX, mouseY) && mouseButton == 1) {
                                    selectedColor = (ColorValue) setting;
                                    new ValueChangedEvent(m, setting).call();
                                }
                            }
                            if (setting instanceof ModeValue) {
                                ModeValue modeValue = (ModeValue) setting;
                                float offset = modeValue.expanded ? modeValue.modes.size() * 12 + 10 : 0;
                                if (RenderUtil.isHovered(setting.guiX + psr18.width(settingName), setting.guiY - 15 - offset, 100 + psr18.width(modeValue.getMode()), 12, mouseX, mouseY)) {
                                    if (mouseButton == 0) modeValue.cycle(true);
                                    if (mouseButton == 1) modeValue.cycle(false);
                                    new ValueChangedEvent(m, setting).call();
                                } else if (RenderUtil.isHovered(setting.guiX + 360, setting.guiY - 15 - offset, 20, 12, mouseX, mouseY)) {
                                    modeValue.expanded = !modeValue.expanded;
                                }
                            }
                        }
                    }
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void setSelectedCategory(Category category) {
        if (selectedCategory == category) return;
        selectedCategory = category;
        scrollAmount = -5;
        mc.getSoundHandler().playButtonPress();
    }

    @Override
    public void updateScreen() {
        wheel = Mouse.getDWheel();
        scrollAmount += wheel / (11f - ModuleInstance.getModule(ClickGui.class).scrollSpeed.getFloat()) * 200f;
        super.updateScreen();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (isEditingKey && editingModule != null) {
            ModuleInstance.getModule(editingModule.getModuleInfo().name()).setKeyBind(keyCode);
            isEditingKey = false;
            editingModule = null;
        }
        if (isCtrlKeyDown() && keyCode == Keyboard.KEY_F) {
            searchField.focused = true;
        }
        if (selectedText != null) {
            if (keyCode == Keyboard.KEY_RETURN) {
                selectedText = null;
            } else {
                textField.keyTyped(typedChar, keyCode);
            }
        }
        if (keyCode == Keyboard.KEY_ESCAPE && scaleAnimation.getDestinationValue() == 1d) {
            scaleAnimation.run(0);
            Keyboard.enableRepeatEvents(false);
        }
        searchField.keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        scaleAnimation = new Animation(Easing.EASE_OUT_EXPO, 300);
        Keyboard.enableRepeatEvents(true);
        hasEditedSliders = false;
        sideAnimation.reset();
        scaleAnimation.run(1.0d);
        wheel = Mouse.getDWheel();
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        selectedSlider = null;
        selectedColor = null;
        selectedText = null;
        Reversal.saveAll();
        super.onGuiClosed();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        selectedSlider = null;
        isDragging = false;
    }

    private static double round(final double value, final float places) {
        if (places < 0) throw new IllegalArgumentException();

        final double precision = 1 / places;
        return Math.round(value * precision) / precision;
    }

    private ArrayList<Module> getRelevantModules(final String search) {
        final ArrayList<Module> relevantModules = new ArrayList<>();

        if (search.isEmpty()) return relevantModules;

        for (final Module module : Reversal.moduleManager.moduleList) {
            if (ModuleInstance.getModule(ClientSettings.class).localization.enabled) {
                if (I18n.format(module.getModuleInfo().localizedName()).toLowerCase().replaceAll(" ", "")
                        .contains(search.toLowerCase().replaceAll(" ", ""))) {
                    relevantModules.add(module);
                }
            } else {
                if (module.getModuleInfo().name().toLowerCase().replaceAll(" ", "")
                        .contains(search.toLowerCase().replaceAll(" ", ""))) {
                    relevantModules.add(module);
                }
            }
        }

        return relevantModules;
    }
    
    private final MFont psm30 = FontManager.getPSM(30);
    private final MFont psr18 = FontManager.getPSR(18);
    private final MFont cur26 = FontManager.getCur(26);
    private final MFont psm20 = FontManager.getPSM(20);
    private final MFont icon16 = FontManager.getIcon(16);
    private final MFont icon20 = FontManager.getIcon(20);
}
