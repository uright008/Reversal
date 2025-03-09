package cn.stars.reversal.ui.clickgui.modern;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.impl.addons.SkinLayers3D;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.module.impl.client.HurtCam;
import cn.stars.reversal.module.impl.client.Optimization;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.module.impl.render.ClickGui;
import cn.stars.reversal.util.animation.advanced.impl.DecelerateAnimation;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.value.Value;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NoteValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static cn.stars.reversal.GameInstance.regular18;
import static cn.stars.reversal.GameInstance.regular20;

public class MMTClickGUI extends GuiScreen {
    public Color backgroundColor = new Color(30,30,30, 220);
    public Animation scaleAnimation = new Animation(Easing.EASE_IN_CUBIC, 150);
    private Animation animation = new Animation(Easing.LINEAR, 200);
    ScaledResolution sr;
    Category selectedCategory = Category.COMBAT;
    private static float scrollAmount, lastScrollAmount, lastLastScrollAmount;
    private static float renderScrollAmount;
    Module firstModule;
    float lastModuleY;
    NumberValue selectedSlider;
    boolean hasEditedSliders = false;
    TimeUtil timer = new TimeUtil();
    private final MFont psr18 = FontManager.getPSR(18);

    private cn.stars.reversal.util.animation.advanced.Animation windowAnim;
    private cn.stars.reversal.util.animation.advanced.Animation windowAnim2;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        sr = new ScaledResolution(mc);
        int x = width / 2 - 260;
        int y = height / 2 - 160;
    //    scaleAnimation.run(1);
    //    GlUtils.startScale(x, y, (float) scaleAnimation.getValue());

        RenderUtil.scaleStart(x + 260, y + 160, (float) windowAnim.getOutput());

        if (!windowAnim.isDone()) windowAnim2.reset();
        RenderUtil.roundedRectangle(x, y, 520, 320, 3, new Color(250,250,250,255));
        RenderUtil.roundedRectangle(x, y, 520, 25, 3, new Color(250,148,166,255));
        RenderUtil.rect(x, y + 20, 520, 5, new Color(250,148,166,255));

        RenderUtil.rect(x + 30, y + 25, 235, 295, new Color(245,245,245,255));

        RenderUtil.rect(x + 30, y + 50, 235, 20, new Color(229,240,248,255));
        RenderUtil.rect(x + 30, y + 50, 235, 0.5, new Color(203,213,239,255));
        RenderUtil.rect(x + 30, y + 70, 235, 0.5, new Color(203,213,239,255));

        RenderUtil.rect(x + 40, y + 56, 1.5f, 8, new Color(2,189,255,255));

        RenderUtil.image(new ResourceLocation("reversal/images/ba/mmt.png"), x + 8f, y + 6f, 85f,12.5f);

        // Category
        int renderSelectY = y + 25;
        RenderUtil.roundedRectangle(x, renderSelectY, 30, 295, 3,
                new Color(75,91,110,255));
        RenderUtil.rect(x, renderSelectY, 1, 294,
                new Color(75,91,110,255));
        RenderUtil.rect(x + 1, renderSelectY, 29, 295,
                new Color(75,91,110,255));

        for (final Category category : Category.values()) {
            RenderUtil.rect(x, renderSelectY, 30, 30,
                    new Color(75,91,110,255));
            if (category == selectedCategory) {
                animation.run(255);
                RenderUtil.rect(x, renderSelectY, 30, 30,
                        new Color(103,119,140,255));
                FontManager.getCur(32).drawString(getCategoryIcon(category), x + 7, renderSelectY + 10, new Color(255,255,255,255).getRGB());
            //    FontManager.getPSB(26).drawString(StringUtils.capitalize(category.name().toLowerCase()), x + 24 + animation.getValue() / 80f, renderSelectY + 6, new Color(200,200,200, 250).getRGB());
            } else {
            //    RenderUtil.roundedRectangle(x + 5, renderSelectY, 105, 20, 4, new Color(80, 80, 80, 180));
                FontManager.getCur(32).drawString(getCategoryIcon(category), x + 7, renderSelectY + 10, new Color(255,255,255,100).getRGB());
            //    FontManager.getPSB(22).drawString(StringUtils.capitalize(category.name().toLowerCase()), x + 24, renderSelectY + 6.5, new Color(160, 160, 160, 200).getRGB());
            }
            renderSelectY += 30;
        }

        // Module
        firstModule = null;
        lastModuleY = 0;
        GlStateManager.pushMatrix();
        int totalModules = 0;
        float moduleX = x + 30;
        float moduleY = y + 75 + renderScrollAmount;
        float settingX = x + 270;
        float settingY = y + 60;
        for (final Module m : Reversal.moduleManager.getModuleList()) {
            if ((m.getModuleInfo().category() == selectedCategory)) {
                totalModules++;
                m.guiX = moduleX;
                m.guiY = moduleY;
                if (firstModule == null) {
                    firstModule = m;
                }
                lastModuleY = Math.max(lastModuleY, m.guiY + m.sizeInGui);
                if (m.expanded) {
                    m.sizeInGui = 20;
                    GL11.glEnable(GL11.GL_SCISSOR_TEST);
                    RenderUtil.scissor(x + 260, y + 30, 260, 290);
                    for (final Value setting : m.getSettings()) {
                        if (!setting.isHidden()) {
                            if (setting instanceof NoteValue) {
                                psr18.drawString(psr18.autoReturn(setting.name, 240, 5), setting.guiX, setting.guiY - 12 * psr18.autoReturnCount(setting.name, 240, 5) - 3, new Color(76,91,110,255).getRGB());
                                settingY += 12 * psr18.autoReturnCount(setting.name, 240, 5);
                            //    m.sizeInGui += 12;
                            }
                            if (setting instanceof BoolValue) {
                                psr18.drawString(setting.name, setting.guiX, setting.guiY - 15, new Color(76,91,110,255).getRGB());
                                RenderUtil.roundedOutlineRectangle(setting.guiX + 5 + psr18.width(setting.name), setting.guiY - 15.5, 8, 8, 4, 0.5,new Color(100, 200, 255, 200));
                                if (((BoolValue) setting).isEnabled())
                                    RenderUtil.roundedRectangle(setting.guiX + 6.5 + psr18.width(setting.name), setting.guiY - 14, 5, 5, 2.5, new Color(100, 200, 255, 250));
                                settingY += 12;
                            //    m.sizeInGui += 12;
                            }
                            if (setting instanceof NumberValue) {
                                psr18.drawString(setting.name, setting.guiX, setting.guiY - 15, new Color(76,91,110,255).getRGB());
                                NumberValue settingValue = (NumberValue) setting;
                                float fontWidth = psr18.getWidth(setting.name) + 5;
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

                                RenderUtil.roundedRectangle(setting.guiX + fontWidth, setting.guiY - 13, 100, 2, 1, new Color(200,200,200,200));
                                RenderUtil.roundedRectangle(setting.guiX + fontWidth + settingValue.renderPercentage * 100, setting.guiY - 14.5, 5, 5, 2.5, new Color(100, 200, 255, 250));
                                psr18.drawString(value, setting.guiX + fontWidth + 109, setting.guiY - 15, new Color(76,91,110,200).getRGB());
                                settingY += 12;
                            //    m.sizeInGui += 12;
                            }
                            if (setting instanceof ModeValue) {
                                psr18.drawString(setting.name, setting.guiX, setting.guiY - 15, new Color(76,91,110,255).getRGB());
                                psr18.drawString(((ModeValue) setting).getModes().get(((ModeValue) setting).index),
                                        setting.guiX + 5 + psr18.width(setting.name), setting.guiY - 15, new Color(76,91,110,200).getRGB());
                                settingY += 12;
                            //    m.sizeInGui += 12;
                            }
                            setting.guiX = settingX;
                            setting.guiY = settingY;
                        }
                    }
                    if (settingY == y + 60) {
                        regular20.drawCenteredString("无设置。", x + 390, y + 160, new Color(114, 120, 126, 255).getRGB());
                        settingY = 0;
                    } else {
                        FontManager.getRegularBold(20).drawCenteredString(m.getModuleInfo().localizedName().isEmpty() ? m.getModuleInfo().name() : m.getModuleInfo().localizedName(), x + 390, y + 32, new Color(60, 60, 60, 255).getRGB());
                        FontManager.getRegular(16).drawCenteredString(m.getModuleInfo().localizedDescription().isEmpty() ? m.getModuleInfo().description() : m.getModuleInfo().localizedDescription(),
                                x + 390, y + 43, new Color(114, 120, 126, 255).getRGB());
                        RenderUtil.roundedOutlineRectangle(x + 267, y + 52, 245, settingY - y - 56, 3, 1, new Color(171,189,193,255));
                    }
                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                } else {
                    m.sizeInGui = 20;
                }
                m.sizeInGui += 10;

                // Keybinding
            /*    RenderUtil.roundedOutlineRectangle(m.guiX + 340 - FontManager.getPSR(16).width(Keyboard.getKeyName(m.getKeyBind())), m.guiY + 9,
                        4.5 + FontManager.getPSR(16).width(Keyboard.getKeyName(m.getKeyBind())), 12, 2, 0.7, new Color(160, 160, 160, 160));
                FontManager.getPSR(16).drawString(Keyboard.getKeyName(m.getKeyBind()), m.guiX + 342 - FontManager.getPSR(16).width(Keyboard.getKeyName(m.getKeyBind())),
                        m.guiY + 12.5, new Color(160,160,160,160).getRGB());

                if (!m.getSettings().isEmpty()) {
                    FontManager.getMi(20).drawString(m.expanded ? "h" : "i", m.guiX + 375, m.guiY + 14, new Color(160, 160, 160, 160).getRGB());
                } */
                if (windowAnim.isDone()) {
                    GL11.glEnable(GL11.GL_SCISSOR_TEST);

                    RenderUtil.scissor(x + 30, y + 70, 265, 250);
                    RenderUtil.rect(m.guiX, m.guiY, 235, m.sizeInGui, m.isEnabled() ? new Color(100, 100, 110, 50) : new Color(245, 245, 245, 255));

                    if (m != firstModule) RenderUtil.rect(m.guiX + 5, m.guiY, 225, 0.5, new Color(203, 213, 239, 255));
                    RenderUtil.rect(m.guiX + 5, m.guiY + m.sizeInGui, 225, 0.5, new Color(203, 213, 239, 255));

                    RenderUtil.roundedRectangle(m.guiX + 8, m.guiY + 12, 6, 6, 3, m.isEnabled() ? new Color(50, 255, 50, 220) : new Color(160, 160, 160, 200));

                    FontManager.getRegularBold(20).drawString((m.getModuleInfo().localizedName().isEmpty() ? m.getModuleInfo().name() : m.getModuleInfo().localizedName()) + (isSpecialModule(m) ? "*" : ""), m.guiX + 20, m.guiY + 8, new Color(60, 60, 60, 255).getRGB());
                    FontManager.getRegular(16).drawString(m.getModuleInfo().localizedDescription().isEmpty() ? m.getModuleInfo().description() : m.getModuleInfo().localizedDescription(),
                            m.guiX + 20, m.guiY + 20 + (canUseChinese(m) ? 1 : 0), new Color(114, 120, 126, 255).getRGB());

                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                }
                moduleY += m.sizeInGui;
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (settingY == y + 60) {
            regular20.drawCenteredString("请选择功能。", x + 390, y + 160, new Color(114, 120, 126, 255).getRGB());
        }

        regular20.drawString(selectedCategory.toString() + "(" + totalModules + ")", x + 40, y + 35, new Color(40,40,40,255).getRGB());
        regular18.drawString("所有功能", x + 48, y + 57.5, new Color(80,80,80,255).getRGB());

        RenderUtil.roundedRectangle(x, y, 520, 320 - 320 * windowAnim2.getOutput(), 3, new Color(250,148,166,255));

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(x, y, 520, 320 - 320 * windowAnim2.getOutput());
        RenderUtil.image(new ResourceLocation("reversal/images/ba/mmt_init.png"), width / 2f - 63.45f, height / 2f - 39.6f, 126.9f, 79.2f);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);


        if (timer.hasReached(10)) {
            timer.reset();
            for (final Module m : Reversal.moduleManager.getModuleList()) {
                m.alphaAnimation.run(m.isEnabled() ? 50 : 0);
                for (final Value s : m.getSettings()) {
                    if (s instanceof NumberValue) {
                        final NumberValue NumberValue = ((NumberValue) s);

                        if (hasEditedSliders) {
                            NumberValue.renderPercentage = (NumberValue.renderPercentage + NumberValue.percentage) / 2;
                        } else {
                            NumberValue.renderPercentage = (NumberValue.renderPercentage * 4 + NumberValue.percentage) / 5;
                        }

                    }
                }
            }

            if (firstModule != null && firstModule.guiY - y >= 75) {
                scrollAmount *= 0.86;
            }

            if (firstModule != null && lastModuleY - y < 290 && !(lastModuleY == firstModule.guiY)) {
                if (!(lastModuleY - firstModule.guiY < 290)) scrollAmount *= 0.99;
                else scrollAmount = -5;
            }

            renderScrollAmount = (float) (lastScrollAmount + (scrollAmount - lastScrollAmount) * mc.timer.renderPartialTicks);
        }

        GlStateManager.popMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    //    GlUtils.stopScale();
        RenderUtil.scaleEnd();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int x = width / 2 - 260;
        int y = height / 2 - 160;
        if (RenderUtil.isHovered(x, y + 25, 30, 30, mouseX, mouseY)) {
            setSelectedCategory(Category.COMBAT);
        }
        if (RenderUtil.isHovered(x, y + 55, 30, 30, mouseX, mouseY)) {
            setSelectedCategory(Category.MOVEMENT);
        }
        if (RenderUtil.isHovered(x, y + 85, 30, 30, mouseX, mouseY)) {
            setSelectedCategory(Category.PLAYER);
        }
        if (RenderUtil.isHovered(x, y + 115, 30, 30, mouseX, mouseY)) {
            setSelectedCategory(Category.RENDER);
        }
        if (RenderUtil.isHovered(x, y + 145, 30, 30, mouseX, mouseY)) {
            setSelectedCategory(Category.MISC);
        }
        if (RenderUtil.isHovered(x, y + 175, 30, 30, mouseX, mouseY)) {
            setSelectedCategory(Category.WORLD);
        }
        if (RenderUtil.isHovered(x, y + 205, 30, 30, mouseX, mouseY)) {
            setSelectedCategory(Category.HUD);
        }
        if (RenderUtil.isHovered(x, y + 235, 30, 30, mouseX, mouseY)) {
            setSelectedCategory(Category.ADDONS);
        }

        float moduleX = x + 30;
        float moduleY = y + 5;
        for (final Module m : Reversal.moduleManager.getModuleList()) {
            if ((m.getModuleInfo().category() == selectedCategory)) {
                if (RenderUtil.isHovered(moduleX, m.guiY, 260, 30, mouseX, mouseY)) {
                    if (m.guiY + FontManager.getRegularBold(20).height() < y + 70 || m.guiY + FontManager.getRegularBold(20).height() > y + 320) return;
                    if (mouseButton == 0) {
                        m.toggleModule();
                    }
                    else if (mouseButton == 1) {
                        for (final Module m1 : Reversal.moduleManager.getModuleList()) m1.expanded = false;
                        m.expanded = true;
                    }
                }
                for (final Value setting : m.getSettings()) {
                    if (m.expanded) {
                        if (!setting.isHidden()) {
                            if (setting instanceof NoteValue) {
                                if (RenderUtil.isHovered(setting.guiX, setting.guiY - 15, x, 12, mouseX, mouseY)) {
                                }
                            }
                            if (setting instanceof BoolValue) {
                                if (RenderUtil.isHovered(setting.guiX, setting.guiY - 15, x, 12, mouseX, mouseY)) {
                                    ((BoolValue) setting).toggle();
                                }
                            }
                            if (setting instanceof NumberValue) {
                                if (RenderUtil.isHovered(setting.guiX, setting.guiY - 15, x, 12, mouseX, mouseY)) {
                                    selectedSlider = (NumberValue) setting;
                                }
                            }
                            if (setting instanceof ModeValue) {
                                if (RenderUtil.isHovered(setting.guiX, setting.guiY - 15, x, 12, mouseX, mouseY)) {
                                    if (mouseButton == 0) ((ModeValue) setting).cycle(true);
                                    if (mouseButton == 1) ((ModeValue) setting).cycle(false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void setSelectedCategory(Category category) {
        if (selectedCategory == category) return;
        animation = new Animation(Easing.LINEAR, 200);
        selectedCategory = category;
        scrollAmount = -5;
        renderScrollAmount = -5;
        mc.getSoundHandler().playButtonPress();
    }

    private String getCategoryIcon(Category c) {
        switch (c) {
            case COMBAT: {
                return "A";
            }
            case MOVEMENT: {
                return "B";
            }
            case PLAYER: {
                return "C";
            }
            case RENDER: {
                return "D";
            }
            case MISC: {
                return "E";
            }
            case WORLD: {
                return "F";
            }
            case HUD: {
                return "G";
            }
            case ADDONS: {
                return "H";
            }
        }
        return "A";
    }

    @Override
    public void updateScreen() {
        int x = width / 2 - 260;
        int y = height / 2 - 160;
    /*    if (Mouse.hasWheel()) {
            if (firstModule == null || firstModule.guiY - y >= 20) {
                scrollAmount -= 10;
            } else if (lastModuleY - y - 320 < -40) {
                if (!(lastModuleY - firstModule.guiY < 280)) scrollAmount += 10;
                else scrollAmount = -5;
            } else {
                scrollAmount += Mouse.getDWheel() / 5;
            }
        } */
        lastLastScrollAmount = lastScrollAmount;
        lastScrollAmount = scrollAmount;

        final float wheel = Mouse.getDWheel();

        scrollAmount += wheel / (11f - ModuleInstance.getModule(ClickGui.class).scrollSpeed.getValue()) * 100;

        if (wheel == 0) {
            scrollAmount -= (lastLastScrollAmount - scrollAmount) * 0.6;
        }
    //    Reversal.showMsg(Mouse.getDWheel() / 5);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        windowAnim = new DecelerateAnimation(300, 1d);
        windowAnim2 = new DecelerateAnimation(300, 1d);
        hasEditedSliders = false;
        animation.reset();
        scaleAnimation.reset();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        selectedSlider = null;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        selectedSlider = null;
    }

    private static double round(final double value, final float places) {
        if (places < 0) throw new IllegalArgumentException();

        final double precision = 1 / places;
        return Math.round(value * precision) / precision;
    }

    public ArrayList<Module> getRelevantModules(final String search) {
        final ArrayList<Module> relevantModules = new ArrayList<>();

        if (search.isEmpty()) return relevantModules;

        for (final Module module : Reversal.moduleManager.moduleList) {
            if (module.getModuleInfo().name().toLowerCase().replaceAll(" ", "")
                    .contains(search.toLowerCase().replaceAll(" ", ""))) {
                relevantModules.add(module);
            }
        }

        return relevantModules;
    }

    public boolean canUseChinese(Module module) {
        if (ModuleInstance.getModule(ClientSettings.class).localization.isEnabled()) {
            return !module.getModuleInfo().localizedDescription().isEmpty() && !module.getModuleInfo().localizedName().isEmpty();
        }
        return false;
    }

    public boolean isSpecialModule(Module module) {
        if (module instanceof ClickGui || module instanceof PostProcessing || module instanceof ClientSettings || module instanceof SkinLayers3D || module instanceof HurtCam || module instanceof Optimization) {
            return true;
        }
        return false;
    }
}
