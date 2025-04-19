package cn.stars.reversal.ui.hud;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.module.impl.hud.Arraylist;
import cn.stars.reversal.module.impl.hud.Keystrokes;
import cn.stars.reversal.module.impl.hud.TextGui;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.*;
import cn.stars.reversal.value.impl.ModeValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Hud implements GameInstance {
    public static float positionOfLastModule;
    public static String key;
    public static List<Module> modules;
    private static final TimeUtil timer = new TimeUtil();
    public static final KeystrokeUtil forward = new KeystrokeUtil();
    public static final KeystrokeUtil backward = new KeystrokeUtil();
    public static final KeystrokeUtil left = new KeystrokeUtil();
    public static final KeystrokeUtil right = new KeystrokeUtil();
    public static final KeystrokeUtil space = new KeystrokeUtil();
    public static final KeystrokeUtil lmb = new KeystrokeUtil();
    public static final KeystrokeUtil rmb = new KeystrokeUtil();
    static ModuleComparator moduleComparator = new ModuleComparator();

    public static void renderKeyStrokes() {
        Keystrokes keystrokes = ModuleInstance.getModule(Keystrokes.class);
        if (keystrokes.isEnabled()) {

            final int x = keystrokes.getX() + 35;
            final int y = keystrokes.getY();

            final int distanceBetweenButtons = 30;
            final int width = 26;

            forward.setUpKey(mc.gameSettings.keyBindForward);
            forward.updateAnimations();
            forward.drawButton(x, y, width);

            backward.setUpKey(mc.gameSettings.keyBindBack);
            backward.updateAnimations();
            backward.drawButton(x, y + distanceBetweenButtons, width);

            left.setUpKey(mc.gameSettings.keyBindLeft);
            left.updateAnimations();
            left.drawButton(x - distanceBetweenButtons, y + distanceBetweenButtons, width);

            right.setUpKey(mc.gameSettings.keyBindRight);
            right.updateAnimations();
            right.drawButton(x + distanceBetweenButtons, y + distanceBetweenButtons, width);

            space.setUpKey(mc.gameSettings.keyBindJump);
            space.updateAnimations();
            space.drawButton(x - 2, y + distanceBetweenButtons * 3, width);

            lmb.setUpMouse(0);
            lmb.updateAnimationsForMouse();
            lmb.drawButtonForMouse(x - distanceBetweenButtons, y + distanceBetweenButtons * 2, width);

            rmb.setUpMouse(1);
            rmb.updateAnimationsForMouse();
            rmb.drawButtonForMouse(x + distanceBetweenButtons - 15, y + distanceBetweenButtons * 2, width);
        }
    }

    public static class ModuleComparator implements Comparator<Object> {
        @Override
        public int compare(final Object o1, final Object o2) {
            ModeValue setting = ModuleInstance.getModule(ClientSettings.class).theme;

            if (setting == null) return 1;

            final String mode = setting.getMode();
//
//            final String name = o1 instanceof Module ? ((Module) o1).getModuleInfo().name() : ((Script) o1).getName();
//            final String name2 = o2 instanceof Module ? ((Module) o2).getModuleInfo().name() : ((Script) o2).getName();

            boolean canLocalize = (mode.equals("Simple") || mode.equals("Minecraft") || mode.equals("Empathy")) && ModuleInstance.getModule(ClientSettings.class).localization.isEnabled();
            final String name = canLocalize ? I18n.format(((Module) o1).getModuleInfo().localizedName()) : ((Module) o1).getModuleInfo().name();
            final String name2 = canLocalize ? I18n.format(((Module) o2).getModuleInfo().localizedName()) : ((Module) o2).getModuleInfo().name();

            switch (mode) {
                case "Minecraft": {
                    return Float.compare(Minecraft.getMinecraft().fontRendererObj.getStringWidth(name2), Minecraft.getMinecraft().fontRendererObj.getStringWidth(name));
                }

                case "Reversal": {
                    return Float.compare(gs.getWidth(name2), gs.getWidth(name));
                }

                case "Modern":
                case "ThunderHack": {
                    return Float.compare(psm17.getWidth(name2), psm17.getWidth(name));
                }

                case "Simple":
                default: {
                    return Float.compare(regular16.getWidth(name2), regular16.getWidth(name));
                }
            }
        }
    }

    private static void renderArrayList() {
        Arraylist arraylist = ModuleInstance.getModule(Arraylist.class);
        if (!arraylist.isEnabled()) return;
        final String mode = ModuleInstance.getModule(ClientSettings.class).theme.getMode();

        final float offset = 6;

        final float arraylistX = arraylist.getX() + arraylist.getWidth();

        modules = new ArrayList<>();
        modules.addAll(Reversal.moduleManager.getEnabledModules());
        modules.sort(moduleComparator);

        int moduleCount = 0;

        for (final Module module : modules) {

            float posOnArraylist = offset + moduleCount * 10.8f * (mode.equals("Empathy") ? 1.25f : 1f);
            
            final String name = (mode.equals("Simple") || mode.equals("Minecraft") || mode.equals("Empathy")) && ModuleInstance.getModule(ClientSettings.class).localization.enabled ? I18n.format(module.getModuleInfo().localizedName()) : module.getModuleInfo().name();

            float finalX = 0;
            final float speed = 6;

            final float renderX = module.getRenderX();
            final float renderY = arraylist.getY() + module.getRenderY();

            if ((module.getModuleInfo().category().equals(Category.RENDER) || module.getModuleInfo().category().equals(Category.HUD)) && ModuleInstance.getModule(Arraylist.class).noRenderModules.isEnabled())
                continue;

            if (ModuleInstance.isSpecialModule(module))
                continue;

            final int finalModuleCount = moduleCount;

            switch (mode) {
                case "Minecraft": {
                    finalX = arraylistX - mc.fontRendererObj.getStringWidth(name);

                    mc.fontRendererObj.drawStringWithShadow(name, renderX, renderY, arraylist.colorValue.getColor(moduleCount).getRGB());

                    break;
                }

                case "Simple": {
                    final int offsetY = 2;
                    final int offsetX = 1;

                    final double stringWidth = regular16.getWidth(name);

                    RenderUtil.rect(renderX - offsetX, renderY - offsetY + 0.5, stringWidth + offsetX * 1.5 + 1, 8.8 + offsetY, new Color(0, 0, 0, 60));
                    RenderUtil.rect(renderX - offsetX + stringWidth + offsetX * 1.5 + 1, renderY - offsetY + 0.4, 1, 8.8 + offsetY, arraylist.colorValue.getColor(moduleCount));

                    finalX = arraylistX - regular16.getWidth(name);

                    regular16.drawString(name, renderX, renderY + 2, arraylist.colorValue.getColor(moduleCount).getRGB());

                    final int mC = moduleCount;
                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RenderUtil.rect(renderX - offsetX, renderY - offsetY + 0.5, stringWidth + offsetX * 1.5 + 1, 8.8 + offsetY, Color.BLACK);
                            RenderUtil.rect(renderX - offsetX + stringWidth + offsetX * 1.5 + 1, renderY - offsetY + 0.4, 1, 8.8 + offsetY, arraylist.colorValue.getColor(mC));
                        });
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RenderUtil.rect(renderX - offsetX, renderY - offsetY + 0.5, stringWidth + offsetX * 1.5 + 1, 8.8 + offsetY, Color.BLACK);
                        });

                    }
                }
                break;

                case "Empathy": {
                    final int offsetY = 2;
                    final int offsetX = 1;

                    final double nameWidth = regular16.getWidth(name);

                    RenderUtil.roundedRectangle(renderX - offsetX - 15.5, renderY - offsetY + 0.5, nameWidth + offsetX * 1.5 + 2, 9.15 + offsetY, 2f, ColorUtil.empathyColor());
                    //    RenderUtil.rect(renderX - offsetX + nameWidth + offsetX * 1.5 - 2, renderY - offsetY - 1, 1, (8.8 + offsetY) * 1.3, ThemeUtil.getThemeColor(moduleCount, ThemeType.ARRAYLIST));

                    finalX = arraylistX - regular16.getWidth(name);

                    regular16.drawString(name, renderX - 15, renderY + 2.2, arraylist.colorValue.getColor(moduleCount).getRGB());

                    RenderUtil.roundedRectangle(renderX - offsetX + nameWidth + offsetX * 1.5 + 0.5 - 13, renderY - offsetY + 0.5, 10, 9.15 + offsetY, 2f, ColorUtil.empathyColor());
                    FontManager.getIcon(16).drawString("j", renderX - offsetX + nameWidth + offsetX * 1.5 + 0.5 - 12, renderY + 2.2, arraylist.colorValue.getColor(moduleCount).getRGB());

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(renderX - offsetX - 15, renderY - offsetY + 0.5, nameWidth + offsetX * 1.5 + 1, 9.15 + offsetY, 2f, ColorUtil.empathyGlowColor());
                        RenderUtil.roundedRectangle(renderX - offsetX + nameWidth + offsetX * 1.5 + 0.5 - 13, renderY - offsetY + 0.5, 10, 9.15 + offsetY, 2f, ColorUtil.empathyGlowColor());
                        //    RenderUtil.rect(renderX - offsetX + nameWidth + offsetX * 1.5 - 2, renderY - offsetY - 1, 1, (8.8 + offsetY) * 1.3, ThemeUtil.getThemeColor(mC, ThemeType.ARRAYLIST));
                    });

                    MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                        regular16.drawString(name, renderX - 15, renderY + 2.2, arraylist.colorValue.getColor(finalModuleCount).getRGB());
                        FontManager.getIcon(16).drawString("j", renderX - offsetX + nameWidth + offsetX * 1.5 + 0.5 - 12, renderY + 2.2, arraylist.colorValue.getColor(finalModuleCount).getRGB());
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(renderX - offsetX - 15, renderY - offsetY + 0.5, nameWidth + offsetX * 1.5 + 1, 9.15 + offsetY, 2f, Color.BLACK);
                        RenderUtil.roundedRectangle(renderX - offsetX + nameWidth + offsetX * 1.5 + 0.5 - 13, renderY - offsetY + 0.5, 10, 9.15 + offsetY, 2f, Color.BLACK);
                    });

                }
                break;

                case "Reversal": {
                    final int offsetY = 2;
                    final int offsetX = 1;

                    final double stringWidth = gs.getWidth(name);
                    posOnArraylist = offset + moduleCount * (gs.getHeight() + 1.25f);

                    RenderUtil.rect(renderX - offsetX, renderY - offsetY + 0.5, stringWidth + offsetX * 1.5, gs.getHeight() + offsetY - 0.7, new Color(0, 0, 0, 60));
                    RenderUtil.roundedRect(renderX + stringWidth, renderY - offsetY + 0.5, 2, gs.getHeight() + offsetY - 0.6, 2.5, ColorUtil.liveColorBrighter(new Color(0,255,255), 1f));

                    finalX = arraylistX - gs.getWidth(name);

                    gs.drawString(name, renderX, renderY + 2, arraylist.colorValue.getColor(moduleCount).getRGB());
                }
                break;

                case "Modern": {
                    final int offsetY = 2;
                    final int offsetX = 1;

                    final double stringWidth = psm17.getWidth(name);

                    RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5, new Color(0, 0, 0, 80));
                    RenderUtil.roundedRectangle(renderX + stringWidth, renderY - offsetY, 2, 10.3 + offsetY - 0.5, 2.5, ColorUtil.liveColorBrighter(new Color(0, 255, 255), 1f));

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5, arraylist.colorValue.getColor(finalModuleCount));
                        RenderUtil.roundedRectangle(renderX + stringWidth, renderY - offsetY, 2, 10.3 + offsetY - 0.5, 2.5, ColorUtil.liveColorBrighter(new Color(0, 255, 255), 1f));
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5, Color.BLACK));

                    psm17.drawString(name, renderX - 1, renderY + 0.6, arraylist.colorValue.getColor(finalModuleCount).getRGB());

                    MODERN_POST_BLOOM_RUNNABLES.add(() -> psm17.drawString(name, renderX - 1, renderY + 0.6, arraylist.colorValue.getColor(finalModuleCount).getRGB()));

                    finalX = arraylistX - psm17.getWidth(name);
                }
                break;

                case "ThunderHack": {
                    final int offsetY = 2;
                    final int offsetX = 1;

                    final double stringWidth = psm17.getWidth(name);

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, finalModuleCount * 25, Color.WHITE, Color.BLACK, true));
                        RenderUtil.roundedRectangle(renderX + stringWidth, renderY - offsetY, 2, 10.3 + offsetY - 0.5, 2.5, ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, finalModuleCount * 25, Color.WHITE, Color.BLACK, true));
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5, Color.BLACK));

                    RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5, new Color(0, 0, 0, 150));
                    RenderUtil.roundedRectangle(renderX + stringWidth, renderY - offsetY, 2, 10.3 + offsetY - 0.5, 2.5, ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, moduleCount * 25, Color.WHITE, Color.BLACK, true));
                    psm17.drawString(name, renderX - 1, renderY + 0.6, ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, moduleCount * 25, Color.WHITE, Color.BLACK, true).getRGB());

                    MODERN_POST_BLOOM_RUNNABLES.add(() -> psm17.drawString(name, renderX - 1, renderY + 0.6, ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, finalModuleCount * 25, Color.WHITE, Color.BLACK, true).getRGB()));

                    finalX = arraylistX - psm17.getWidth(name);
                }
                break;

            }

            moduleCount++;

            final String animationMode = ModuleInstance.getModule(ClientSettings.class).listAnimation.getMode();
            
            if (timer.hasReached(1000 / 100)) {
                switch (animationMode) {
                    case "Reversal":
                        module.renderX = (module.renderX * (speed - 1) + finalX) / speed;
                        module.renderY = (module.renderY * (speed - 1) + posOnArraylist) / speed;

                        break;
                    case "Slide":
                        module.renderX = (module.renderX * (speed - 1) + finalX) / speed;

                        if (module.renderY < positionOfLastModule) {
                            module.renderY = posOnArraylist;
                        } else {
                            module.renderY = (module.renderY * (speed - 1) + posOnArraylist) / (speed);
                        }
                        break;
                }
            }

            positionOfLastModule = posOnArraylist;

        }

        if (timer.hasReached(1000 / 100)) {
            timer.reset();
        }

        arraylist.setHeight((int)(moduleCount * 12 * (mode.equals("Empathy") ? 1.25 : 1)));
    }
    
    private static void renderClientName() {
        TextGui textGui = ModuleInstance.getModule(TextGui.class);
        if (!textGui.isEnabled()) return;
        final String mode = ModuleInstance.getModule(ClientSettings.class).theme.getMode();
        final boolean useDefaultName = !ModuleInstance.getModule(TextGui.class).custom.isEnabled();

        String name = Reversal.NAME, customName = ThemeUtil.getCustomClientName();

        if (customName.isEmpty()) customName = "Use \".clientname <name>\" to set custom name.";
        switch (mode) {
            case "Minecraft": {
                if (useDefaultName) {
                    textGui.setWidth(75);
                    float off = 0;

                    for (int i = 0; i < name.length(); i++) {
                        final String character = String.valueOf(name.charAt(i));

                        mc.fontRendererObj.drawStringWithShadow(character, textGui.getX() + 1 + off, textGui.getY(), textGui.colorValue.getColor(i).getRGB());

                        off += mc.fontRendererObj.getStringWidth(character);
                    }

                } else {
                    textGui.setWidth((int) (20 + mc.fontRendererObj.getStringWidth(customName) * 1.5));
                    float off = 0;

                    for (int i = 0; i < customName.length(); i++) {
                        final String character = String.valueOf(customName.charAt(i));
                        mc.fontRendererObj.drawStringWithShadow(character, textGui.getX() + 1 + off, textGui.getY(), textGui.colorValue.getColor(i).getRGB());

                        off += mc.fontRendererObj.getStringWidth(character);
                    }
                }
                break;
            }

            case "Reversal": {
                if (useDefaultName) {
                    textGui.setWidth(100);
                    gsTitle.drawStringWithShadow("R", textGui.getX() + 7, textGui.getY() + 5, textGui.colorValue.getColor().getRGB());
                    gsTitle.drawStringWithShadow("eversal [" + Reversal.VERSION + "]", textGui.getX() + 7 + gsTitle.getWidth("R"), textGui.getY() + 4.9f, new Color(230, 230, 230, 200).getRGB());
                } else {
                    textGui.setWidth((int) (20 + gsTitle.getWidth(customName)));
                    gsTitle.drawStringWithShadow(String.valueOf(customName.charAt(0)), textGui.getX() + 7, textGui.getY() + 5, textGui.colorValue.getColor().getRGB());
                    // 从字符串第二个字开始获取
                    gsTitle.drawStringWithShadow(customName.substring(1), textGui.getX() + 7 + gsTitle.getWidth(String.valueOf(customName.charAt(0))), textGui.getY() + 4.9f, new Color(230, 230, 230, 200).getRGB());
                }
                break;
            }

            case "Simple": {
                String extraText = " | " + mc.getSession().getUsername();

                if (useDefaultName) {
                    final String clientName = "REVERSAL CLIENT";

                    textGui.setWidth(100 + psm18.getWidth(extraText));
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    RenderUtil.rect(x, y, psb20.getWidth(clientName) + psm18.getWidth(extraText) + 7, psb20.height() + 1.5, new Color(0, 0, 0, 80));

                    for (int i = 0; i < clientName.length(); i++) {
                        final String character = String.valueOf(clientName.charAt(i));

                        final float off1 = off;
                        psb20.drawString(character, x + 4 + off1, y + 3, textGui.colorValue.getColor(i).getRGB());
                        int finalI = i;
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            psb20.drawString(character, x + 4 + off1, y + 3, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += psb20.getWidth(character);
                    }
                    
                    psm18.drawString(extraText, x + 3.5 + psb20.getWidth(clientName), y + 4,  new Color(250, 250, 250, 200).getRGB());

                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RenderUtil.rect(x, y, psb20.getWidth(clientName) + psm18.getWidth(extraText) + 7, psb20.height() + 1.5, Color.BLACK);
                        });
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RenderUtil.rect(x, y, psb20.getWidth(clientName) + psm18.getWidth(extraText) + 7, psb20.height() + 1.5, Color.BLACK);
                        });
                    }

                } else {
                    final String clientName = customName;

                    textGui.setWidth(20 + psb20.getWidth(clientName) + psm18.getWidth(extraText));
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    RenderUtil.rect(x, y, psb20.getWidth(clientName) + psm18.getWidth(extraText) + 8, psb20.height() + 1.5, new Color(0, 0, 0, 80));

                    for (int i = 0; i < clientName.length(); i++) {
                        final String character = String.valueOf(clientName.charAt(i));

                        final float off1 = off;
                        psb20.drawString(character, x + 4 + off1, y + 3, textGui.colorValue.getColor(i).getRGB());
                        int finalI = i;
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            psb20.drawString(character, x + 4 + off1, y + 3, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += psb20.getWidth(character);
                    }

                    psm18.drawString(extraText, x + 4.5 + off, y + 4, new Color(250, 250, 250, 200).getRGB());

                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RenderUtil.rect(x, y, psb20.getWidth(clientName) + psm18.getWidth(extraText) + 8, psb20.height() + 1.5, Color.BLACK);
                        });
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RenderUtil.rect(x, y, psb20.getWidth(clientName) + psm18.getWidth(extraText) + 8, psb20.height() + 1.5, Color.BLACK);
                        });
                    }
                }
                break;
            }

            case "Empathy": {
                if (useDefaultName) {
                    final String clientName = "Reversal";

                    textGui.setWidth(100);
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    RenderUtil.roundedRectangle(x, y, psb20.getWidth(clientName) + 20, psb20.height() + 1.5, 3f, ColorUtil.empathyColor());
                    RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, psb20.height() - 3.5, 1f, textGui.colorValue.getColor());

                    FontManager.getAtomic(16).drawString("2", x + 5, y + 5.5, textGui.colorValue.getColor().getRGB());
                    for (int i = 0; i < clientName.length(); i++) {
                        final String character = String.valueOf(clientName.charAt(i));

                        final float off1 = off;
                        psb20.drawString(character, x + 16 + off1, y + 3, textGui.colorValue.getColor(i).getRGB());
                        int finalI = i;
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            psb20.drawString(character, x + 16 + off1, y + 3, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += psb20.getWidth(character);
                    }

                    MODERN_POST_BLOOM_RUNNABLES.add(() -> FontManager.getAtomic(16).drawString("2", x + 5, y + 5.5, textGui.colorValue.getColor().getRGB()));

                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RenderUtil.roundedRectangle(x, y, psb20.getWidth(clientName) + 20, psb20.height() + 1.5, 3f, ColorUtil.empathyGlowColor());
                            RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, psb20.height() - 3.5, 1f, textGui.colorValue.getColor());
                        });
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RenderUtil.roundedRectangle(x, y, psb20.getWidth(clientName) + 20, psb20.height() + 1.5, 3f, Color.BLACK);
                        });
                    }

                } else {
                    final String clientName = customName;

                    textGui.setWidth(20 + psb20.getWidth(clientName));
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    RenderUtil.roundedRectangle(x, y, psb20.getWidth(clientName) + 20, psb20.height() + 1.5, 3f, ColorUtil.empathyColor());
                    RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, psb20.height() - 3.5, 1f, textGui.colorValue.getColor());

                    FontManager.getAtomic(16).drawString("2", x + 5, y + 5.5, textGui.colorValue.getColor().getRGB());
                    for (int i = 0; i < clientName.length(); i++) {
                        final String character = String.valueOf(clientName.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        psb20.drawString(character, x + 16 + off1, y + 3, textGui.colorValue.getColor(i).getRGB());
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            psb20.drawString(character, x + 16 + off1, y + 3, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += psb20.getWidth(character);
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RenderUtil.roundedRectangle(x, y, psb20.getWidth(clientName) + 20, psb20.height() + 1.5, 3f, ColorUtil.empathyGlowColor());
                            RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, psb20.height() - 3.5, 1f, textGui.colorValue.getColor());
                        });
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RenderUtil.roundedRectangle(x, y, psb20.getWidth(clientName) + 20, psb20.height() + 1.5, 3f, Color.BLACK);
                        });
                    }
                }
                break;
            }

            case "Modern": {
                int x = textGui.getX() + 5;
                int y = textGui.getY();
                float off = 0;
                String extraText = " | " + mc.getSession().getUsername();
                float extraWidth = psm18.getWidth(extraText);

                if (useDefaultName) {
                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RoundedUtil.drawRound(x + 1, y + 1, 33 + extraWidth, 12, 4, Color.BLACK);
                    });

                    RoundedUtil.drawRound(x + 1, y + 1, 48 + extraWidth, 12, 4, new Color(0, 0, 0, 80));
                    RenderUtil.roundedOutlineRectangle(x, y, 50 + extraWidth, 14, 3, 1, textGui.colorValue.getColor());

                    MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                        RoundedUtil.drawRound(x + 1, y + 1, 48 + extraWidth, 12, 4, textGui.colorValue.getColor());
                    });

                    for (int i = 0; i < name.length(); i++) {
                        final String character = String.valueOf(name.charAt(i));

                        final float off1 = off;
                        psb20.drawString(character, x + 5 + off1, y + 3.5, textGui.colorValue.getColor(i).getRGB());
                        int finalI = i;
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            psb20.drawString(character, x + 5 + off1, y + 3.5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += psb20.getWidth(character);
                    }
                    psm18.drawString(extraText, x + 45, y + 4f, new Color(250, 250, 250, 200).getRGB());

                    textGui.setWidth((int) (50 + extraWidth));
                } else {
                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RoundedUtil.drawRound(x + 1, y + 1, 33 + extraWidth, 12, 4, Color.BLACK);
                    });

                    RoundedUtil.drawRound(x + 1, y + 1, psb20.getWidth(ThemeUtil.getCustomClientName()) + 8 + extraWidth, 12, 4, new Color(0, 0, 0, 80));
                    RenderUtil.roundedOutlineRectangle(x, y, psb20.getWidth(ThemeUtil.getCustomClientName()) + 10 + extraWidth, 14, 3, 1, textGui.colorValue.getColor());

                    MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                        RoundedUtil.drawRound(x + 1, y + 1, psb20.getWidth(ThemeUtil.getCustomClientName()) + 8 + extraWidth, 12, 4, textGui.colorValue.getColor());
                    });

                    for (int i = 0; i < customName.length(); i++) {
                        final String character = String.valueOf(customName.charAt(i));

                        final float off1 = off;
                        psb20.drawString(character, x + 5 + off1, y + 3.5, textGui.colorValue.getColor(i).getRGB());
                        int finalI = i;
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            psb20.drawString(character, x + 5 + off1, y + 3.5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += psb20.getWidth(character);
                    }
                    psm18.drawString(extraText, x + off + 5, y + 4f, new Color(250, 250, 250, 200).getRGB());

                    textGui.setWidth((int) (off + 5 + extraWidth));
                }
                break;
            }

            case "ThunderHack": {
                int x = textGui.getX() + 5;
                int y = textGui.getY();
                float off = 0;
                String extraText = " | " + mc.getSession().getUsername();
                float extraWidth = psm18.getWidth(extraText);

                if (useDefaultName) {
                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, 94 + extraWidth, 15, 3,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                        RoundedUtil.drawRound(x, y, 80 + extraWidth, 14, 3, new Color(0, 0, 0, 220));
                    });

                    //    RoundedUtil.drawRound(x, y, 35 + extraWidth, 14, 4, new Color(0, 0, 0, 80));
                    RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, 94 + extraWidth, 15, 3,
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    RoundedUtil.drawRound(x, y, 93 + extraWidth, 14, 3, new Color(0, 0, 0, 220));

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, 94 + extraWidth, 15, 3,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    });

                    name = "REVERSAL CLIENT";

                    for (int i = 0; i < name.length(); i++) {
                        final String character = String.valueOf(name.charAt(i));

                        final float off1 = off;
                        psb20.drawString(character, x + 5 + off1, y + 3.5,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, i * 10, Color.WHITE, Color.BLACK, true).getRGB());
                        int finalI = i;
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            psb20.drawString(character, x + 5 + off1, y + 3.5, ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, finalI * 10, Color.WHITE, Color.BLACK, true).getRGB());
                        });
                        off += psb20.getWidth(character);
                    }

                    psm18.drawString(extraText, x + 90, y + 4f, new Color(200, 200, 200, 240).getRGB());

                    textGui.setWidth((int) (102 + extraWidth));
                } else {
                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, psb20.getWidth(ThemeUtil.getCustomClientName()) + 11 + extraWidth, 15, 3,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                        RoundedUtil.drawRound(x, y, psb20.getWidth(ThemeUtil.getCustomClientName()) + 10 + extraWidth, 14, 3, new Color(0, 0, 0, 220));
                    });

                    //    RoundedUtil.drawRound(x, y, 35 + extraWidth, 14, 4, new Color(0, 0, 0, 80));
                    RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, psb20.getWidth(ThemeUtil.getCustomClientName()) + 11 + extraWidth, 15, 3,
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    RoundedUtil.drawRound(x, y, psb20.getWidth(ThemeUtil.getCustomClientName()) + 10 + extraWidth, 14, 3, new Color(0, 0, 0, 220));

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, psb20.getWidth(ThemeUtil.getCustomClientName()) + 11 + extraWidth, 15, 3,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    });

                    for (int i = 0; i < customName.length(); i++) {
                        final String character = String.valueOf(customName.charAt(i));

                        final float off1 = off;
                        psb20.drawString(character, x + 5 + off1, y + 3.5,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, i * 10, Color.WHITE, Color.BLACK, true).getRGB());
                        int finalI = i;
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            psb20.drawString(character, x + 5 + off1, y + 3.5, ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, finalI * 10, Color.WHITE, Color.BLACK, true).getRGB());
                        });
                        off += psb20.getWidth(character);
                    }

                    psm18.drawString(extraText, x + off + 6f, y + 4f, new Color(200, 200, 200, 240).getRGB());

                    textGui.setWidth((int) (off + 6f + extraWidth));
                }
                break;
            }
        }
    }

    public static void renderHud() {
        if (ModuleInstance.canDrawHUD()) {
            renderKeyStrokes();
            renderClientName();
            renderArrayList();
        }
    }
}
