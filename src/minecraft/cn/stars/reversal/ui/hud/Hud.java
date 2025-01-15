package cn.stars.reversal.ui.hud;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.impl.hud.*;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Hud implements GameInstance {
    public static float ticks, ticksSinceClickgui;
    public static float positionOfLastModule;
    public static String key;
    public static List<Module> modules;
    private static final TimeUtil timer2 = new TimeUtil();
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
        Keystrokes keystrokes = (Keystrokes) ModuleInstance.getModule(Keystrokes.class);
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

            boolean chinese = mode.equals("Simple") || mode.equals("Minecraft") || mode.equals("Empathy");
            final String name = chinese && ModuleInstance.getModule(Arraylist.class).chinese.isEnabled() && !((Module) o1).getModuleInfo().chineseName().isEmpty() ? ((Module) o1).getModuleInfo().chineseName() : ((Module) o1).getModuleInfo().name();
            final String name2 = chinese
                    && ModuleInstance.getModule(Arraylist.class).chinese.isEnabled() && !((Module) o2).getModuleInfo().chineseName().isEmpty() ? ((Module) o2).getModuleInfo().chineseName() : ((Module) o2).getModuleInfo().name();

            switch (mode) {
                case "Minecraft": {
                    return Float.compare(Minecraft.getMinecraft().fontRendererObj.getStringWidth(name2), Minecraft.getMinecraft().fontRendererObj.getStringWidth(name));
                }

                case "Reversal": {
                    return Float.compare(gs.getWidth(name2), gs.getWidth(name));
                }

                case "Modern": {
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
            
            final String name = (mode.equals("Simple") || mode.equals("Minecraft") || mode.equals("Empathy")) && ModuleInstance.getModule(Arraylist.class).chinese.isEnabled() && !module.getModuleInfo().chineseName().isEmpty() ? module.getModuleInfo().chineseName() : module.getModuleInfo().name();

            float finalX = 0;
            final float speed = 6;

            final float renderX = module.getRenderX();
            final float renderY = arraylist.getY() + module.getRenderY();

            if (module.getModuleInfo().category().equals(Category.RENDER) && ModuleInstance.getModule(Arraylist.class).noRenderModules.isEnabled())
                continue;

            if (ModuleInstance.isSpecialModule(module))
                continue;

            switch (mode) {
                case "Minecraft": {
                    finalX = arraylistX - mc.fontRendererObj.getStringWidth(name);

                    mc.fontRendererObj.drawStringWithShadow(name, renderX, renderY, ThemeUtil.getThemeColorInt(moduleCount, ThemeType.ARRAYLIST));

                    break;
                }

                case "Simple": {
                    final int offsetY = 2;
                    final int offsetX = 1;

                    final double stringWidth = regular16.getWidth(name);

                    RenderUtil.rect(renderX - offsetX, renderY - offsetY + 0.5, stringWidth + offsetX * 1.5 + 1, 8.8 + offsetY, new Color(0, 0, 0, 60));
                    RenderUtil.rect(renderX - offsetX + stringWidth + offsetX * 1.5 + 1, renderY - offsetY + 0.4, 1, 8.8 + offsetY, ThemeUtil.getThemeColor(moduleCount, ThemeType.ARRAYLIST));

                    finalX = arraylistX - regular16.getWidth(name);

                    regular16.drawString(name, renderX, renderY + 2, ThemeUtil.getThemeColorInt(moduleCount, ThemeType.ARRAYLIST));

                    final int mC = moduleCount;
                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RenderUtil.rect(renderX - offsetX, renderY - offsetY + 0.5, stringWidth + offsetX * 1.5 + 1, 8.8 + offsetY, Color.BLACK);
                            RenderUtil.rect(renderX - offsetX + stringWidth + offsetX * 1.5 + 1, renderY - offsetY + 0.4, 1, 8.8 + offsetY, ThemeUtil.getThemeColor(mC, ThemeType.ARRAYLIST));
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

                    regular16.drawString(name, renderX - 15, renderY + 2.2, ThemeUtil.getThemeColorInt(moduleCount, ThemeType.ARRAYLIST));

                    RenderUtil.roundedRectangle(renderX - offsetX + nameWidth + offsetX * 1.5 + 0.5 - 13, renderY - offsetY + 0.5, 10, 9.15 + offsetY, 2f, ColorUtil.empathyColor());
                    FontManager.getIcon(16).drawString("j", renderX - offsetX + nameWidth + offsetX * 1.5 + 0.5 - 12, renderY + 2.2, ThemeUtil.getThemeColorInt(moduleCount, ThemeType.ARRAYLIST));

                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RenderUtil.roundedRectangle(renderX - offsetX - 15, renderY - offsetY + 0.5, nameWidth + offsetX * 1.5 + 1, 9.15 + offsetY, 2f, ColorUtil.empathyGlowColor());
                            RenderUtil.roundedRectangle(renderX - offsetX + nameWidth + offsetX * 1.5 + 0.5 - 13, renderY - offsetY + 0.5, 10, 9.15 + offsetY, 2f, ColorUtil.empathyGlowColor());
                        //    RenderUtil.rect(renderX - offsetX + nameWidth + offsetX * 1.5 - 2, renderY - offsetY - 1, 1, (8.8 + offsetY) * 1.3, ThemeUtil.getThemeColor(mC, ThemeType.ARRAYLIST));
                        });
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RenderUtil.roundedRectangle(renderX - offsetX - 15, renderY - offsetY + 0.5, nameWidth + offsetX * 1.5 + 1, 9.15 + offsetY, 2f, Color.BLACK);
                            RenderUtil.roundedRectangle(renderX - offsetX + nameWidth + offsetX * 1.5 + 0.5 - 13, renderY - offsetY + 0.5, 10, 9.15 + offsetY, 2f, Color.BLACK);
                        });

                    }
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

                    gs.drawString(name, renderX, renderY + 2, ThemeUtil.getThemeColorInt(moduleCount, ThemeType.ARRAYLIST));
                }
                break;

                case "Modern": {
                    final int offsetY = 2;
                    final int offsetX = 1;

                    final double stringWidth = psm17.getWidth(name);
                    final int mC = moduleCount;

                    if (!ModuleInstance.getModule(ClientSettings.class).thunderHack.isEnabled()) {
                        Runnable shadowRunnable = () -> {
                            RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5,
                                    ModuleInstance.getModule(Arraylist.class).glowShadow.enabled ? ThemeUtil.getThemeColor(mC, ThemeType.ARRAYLIST) : Color.BLACK);
                            RenderUtil.roundedRectangle(renderX + stringWidth, renderY - offsetY, 2, 10.3 + offsetY - 0.5, 2.5, ColorUtil.liveColorBrighter(new Color(0, 255, 255), 1f));
                        };

                        Runnable blurRunnable = () -> {
                            RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5, Color.BLACK);
                        };

                        RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5, new Color(0, 0, 0, 80));
                        RenderUtil.roundedRectangle(renderX + stringWidth, renderY - offsetY, 2, 10.3 + offsetY - 0.5, 2.5, ColorUtil.liveColorBrighter(new Color(0, 255, 255), 1f));

                        if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                            MODERN_BLOOM_RUNNABLES.add(shadowRunnable);
                        }

                        if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                            MODERN_BLUR_RUNNABLES.add(blurRunnable);
                        }

                        psm17.drawString(name, renderX - 1, renderY + 0.6, ThemeUtil.getThemeColorInt(mC, ThemeType.ARRAYLIST));
                    } else {
                        Runnable shadowRunnable = () -> {
                            RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5,
                                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, mC * 25, Color.WHITE, Color.BLACK, true));
                            RenderUtil.roundedRectangle(renderX + stringWidth, renderY - offsetY, 2, 10.3 + offsetY - 0.5, 2.5, ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, mC * 25, Color.WHITE, Color.BLACK, true));
                        };

                        Runnable blurRunnable = () -> {
                            RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5, Color.BLACK);
                        };

                        if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                            MODERN_BLOOM_RUNNABLES.add(shadowRunnable);
                        }

                        if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                            MODERN_BLUR_RUNNABLES.add(blurRunnable);
                        }

                        RenderUtil.rect(renderX - offsetX - 1, renderY - offsetY + 0.2, stringWidth + offsetX * 1.5 + 1, 10.3 + offsetY - 1.5, new Color(0, 0, 0, 150));
                        RenderUtil.roundedRectangle(renderX + stringWidth, renderY - offsetY, 2, 10.3 + offsetY - 0.5, 2.5, ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, moduleCount * 25, Color.WHITE, Color.BLACK, true));
                        psm17.drawString(name, renderX - 1, renderY + 0.6, ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, moduleCount * 25, Color.WHITE, Color.BLACK, true).getRGB());
                    }

                    finalX = arraylistX - psm17.getWidth(name);
                }
                break;
            }

            moduleCount++;

            final String animationMode = ModuleInstance.getModule(ClientSettings.class).listAnimation.getMode();
            
            if (timer2.hasReached(1000 / 100)) {
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

        arraylist.setHeight((int)(moduleCount * 12 * (mode.equals("Empathy") ? 1.25 : 1)));

        // Resetting timer
        if (timer2.hasReached(1000 / 100)) {
            timer2.reset();
        }

        if (timer.hasReached(1000 / 60)) {
            timer.reset();

            if (mc.ingameGUI != null && !(mc.currentScreen instanceof GuiChat)) {
                if (ticksSinceClickgui <= 5)
                    ticksSinceClickgui++;
            } else {
                if (ticksSinceClickgui >= 1)
                    ticksSinceClickgui--;
            }

            forward.updateAnimations();
            backward.updateAnimations();
            left.updateAnimations();
            right.updateAnimations();
            space.updateAnimations();
        }
    }
    
    private static void renderClientName() {
        TextGui textGui = (TextGui) ModuleInstance.getModule(TextGui.class);
        if (!textGui.isEnabled()) return;
        final String mode = ModuleInstance.getModule(ClientSettings.class).theme.getMode();
        final boolean useDefaultName = !ModuleInstance.getModule(TextGui.class).custom.isEnabled();

        final float offset;
        String name = Reversal.NAME, customName = ThemeUtil.getCustomClientName();

        if (customName.isEmpty()) customName = "Use \".clientname <name>\" to set custom name.";
        switch (mode) {
            case "Minecraft": {
                if (useDefaultName) {
                    textGui.setWidth(75);
                    float off = 0;

                    for (int i = 0; i < name.length(); i++) {
                        final String character = String.valueOf(name.charAt(i));

                        mc.fontRendererObj.drawStringWithShadow(character, textGui.getX() + 1 + off, textGui.getY(), ThemeUtil.getThemeColorInt(i, ThemeType.ARRAYLIST));

                        off += mc.fontRendererObj.getStringWidth(character);
                    }

                    off = mc.fontRendererObj.getStringWidth(name);
                } else {
                    textGui.setWidth((int) (20 + mc.fontRendererObj.getStringWidth(customName) * 1.5));
                    float off = 0;

                    for (int i = 0; i < customName.length(); i++) {
                        final String character = String.valueOf(customName.charAt(i));
                        mc.fontRendererObj.drawStringWithShadow(character, textGui.getX() + 1 + off, textGui.getY(), ThemeUtil.getThemeColorInt(i, ThemeType.ARRAYLIST));

                        off += mc.fontRendererObj.getStringWidth(character);
                    }
                }
                break;
            }

            case "Reversal": {
                if (useDefaultName) {
                    textGui.setWidth(100);
                    gsTitle.drawStringWithShadow("S", textGui.getX() + 7, textGui.getY() + 5, ThemeUtil.getThemeColorInt(ThemeType.ARRAYLIST));
                    gsTitle.drawStringWithShadow("tarX [" + Reversal.VERSION + "]", textGui.getX() + 7 + gsTitle.getWidth("S"), textGui.getY() + 4.9f, new Color(230, 230, 230, 200).getRGB());
                } else {
                    textGui.setWidth((int) (20 + gsTitle.getWidth(customName)));
                    // FOOLISH
                    gsTitle.drawStringWithShadow(String.valueOf(customName.charAt(0)), textGui.getX() + 7, textGui.getY() + 5, ThemeUtil.getThemeColorInt(ThemeType.ARRAYLIST));
                    // 从字符串第二个字开始获取
                    gsTitle.drawStringWithShadow(customName.substring(1), textGui.getX() + 5 + gsTitle.getWidth(String.valueOf(customName.charAt(0))), textGui.getY() + 4.9f, new Color(230, 230, 230, 200).getRGB());
                }
                break;
            }

            case "Simple": {
                String extraText = " | " + mc.getSession().getUsername();

                if (useDefaultName) {
                    final String clientName = "REVERSAL CLIENT";

                    textGui.setWidth((int) (100 + regular18.width(extraText)));
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    RenderUtil.rect(x, y, regular20Bold.width(clientName) + regular18.width(extraText) + 6, regular20Bold.height() + 1.5, new Color(0, 0, 0, 80));

                    for (int i = 0; i < clientName.length(); i++) {
                        final String character = String.valueOf(clientName.charAt(i));

                        final float off1 = off;
                        regular20Bold.drawString(character, x + 4 + off1, y + 3.5, ThemeUtil.getThemeColorInt(i / 2, ThemeType.ARRAYLIST));
                        int finalI = i;
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            regular20Bold.drawString(character, x + 4 + off1, y + 3.5, ThemeUtil.getThemeColorInt(finalI / 2, ThemeType.ARRAYLIST));
                        });
                        off += regular20Bold.width(character);
                    }
                    
                    regular18.drawString(extraText, x + 4 + regular20Bold.width(clientName), y + 4,  new Color(250, 250, 250, 200).getRGB());

                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RenderUtil.rect(x, y, regular20Bold.width(clientName) + regular18.width(extraText) + 6, regular20Bold.height() + 1.5, Color.BLACK);
                        });
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RenderUtil.rect(x, y, regular20Bold.width(clientName) + regular18.width(extraText) + 6, regular20Bold.height() + 1.5, Color.BLACK);
                        });
                    }

                } else {
                    final String clientName = customName;

                    textGui.setWidth((int) (20 + regular20Bold.width(clientName) + regular18.width(extraText)));
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    RenderUtil.rect(x, y, regular20Bold.width(clientName) + regular18.width(extraText) + 6, regular20Bold.height() + 1.5, new Color(0, 0, 0, 80));

                    for (int i = 0; i < clientName.length(); i++) {
                        final String character = String.valueOf(clientName.charAt(i));

                        final float off1 = off;
                        regular20Bold.drawString(character, x + 4 + off1, y + 3.5, ThemeUtil.getThemeColorInt(i / 2, ThemeType.ARRAYLIST));
                        int finalI = i;
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            regular20Bold.drawString(character, x + 4 + off1, y + 3.5, ThemeUtil.getThemeColorInt(finalI / 2, ThemeType.ARRAYLIST));
                        });
                        off += regular20Bold.width(character);
                    }

                    regular18.drawString(extraText, x + 4 + off, y + 4, new Color(250, 250, 250, 200).getRGB());

                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RenderUtil.rect(x, y, regular20Bold.width(clientName) + regular18.width(extraText) + 6, regular20Bold.height() + 1.5, Color.BLACK);
                        });
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RenderUtil.rect(x, y, regular20Bold.width(clientName) + regular18.width(extraText) + 6, regular20Bold.height() + 1.5, Color.BLACK);
                        });
                    }
                }
                break;
            }

            case "Empathy": {
                if (useDefaultName) {
                    final String clientName = "★ REVERSAL";

                    textGui.setWidth(100);
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    RenderUtil.roundedRectangle(x, y, regular20Bold.width(clientName) + 8, regular20Bold.height() + 1.5, 3f, ColorUtil.empathyColor());
                    RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, regular20Bold.height() - 3.5, 1f, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));

                    for (int i = 0; i < clientName.length(); i++) {
                        final String character = String.valueOf(clientName.charAt(i));

                        final float off1 = off;
                        regular20Bold.drawString(character, x + 4 + off1, y + 3.5, ThemeUtil.getThemeColorInt(i / 2, ThemeType.ARRAYLIST));
                        int finalI = i;
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            regular20Bold.drawString(character, x + 4 + off1, y + 3.5, ThemeUtil.getThemeColorInt(finalI / 2, ThemeType.ARRAYLIST));
                        });
                        off += regular20Bold.width(character);
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RenderUtil.roundedRectangle(x, y, regular20Bold.width(clientName) + 8, regular20Bold.height() + 1.5, 3f, ColorUtil.empathyGlowColor());
                            RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, regular20Bold.height() - 3.5, 1f, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));
                        });
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RenderUtil.roundedRectangle(x, y, regular20Bold.width(clientName) + 8, regular20Bold.height() + 1.5, 3f, Color.BLACK);
                        });
                    }

                } else {
                    final String clientName = "★ " + customName;

                    textGui.setWidth((int) (20 + regular20Bold.width(clientName)));
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    RenderUtil.roundedRectangle(x, y, regular20Bold.width(clientName) + 8, regular20Bold.height() + 1.5, 3f, ColorUtil.empathyColor());
                    RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, regular20Bold.height() - 3.5, 1f, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));

                    for (int i = 0; i < clientName.length(); i++) {
                        final String character = String.valueOf(clientName.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        regular20Bold.drawString(character, x + 4 + off1, y + 3.5, ThemeUtil.getThemeColorInt(i / 2, ThemeType.ARRAYLIST));
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            regular20Bold.drawString(character, x + 4 + off1, y + 3.5, ThemeUtil.getThemeColorInt(finalI / 2, ThemeType.ARRAYLIST));
                        });
                        off += regular20Bold.width(character);
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RenderUtil.roundedRectangle(x, y, regular20Bold.width(clientName) + 8, regular20Bold.height() + 1.5, 3f, ColorUtil.empathyGlowColor());
                            RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, regular20Bold.height() - 3.5, 1f, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));
                        });
                    }

                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RenderUtil.roundedRectangle(x, y, regular20Bold.width(clientName) + 8, regular20Bold.height() + 1.5, 3f, Color.BLACK);
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

                if (!ModuleInstance.getModule(ClientSettings.class).thunderHack.isEnabled()) {
                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RoundedUtil.drawRound(x + 1, y + 1, 33 + extraWidth, 12, 4, Color.BLACK);
                        });
                    }

                    RoundedUtil.drawRound(x + 1, y + 1, 46 + extraWidth, 12, 4, new Color(0, 0, 0, 80));
                    RenderUtil.roundedOutlineRectangle(x, y, 48 + extraWidth, 14, 3, 1, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));
                //    RoundedUtil.drawRoundOutline(x, y, 35 + extraWidth, 14, 3, 0.1f, new Color(0, 0, 0, 80),
                //            ModuleInstance.getBool("TextGui", "Rainbow").isEnabled() ? ThemeUtil.getThemeColor(ThemeType.ARRAYLIST) : new Color(250, 250, 250, 200));
                    psm18.drawString(extraText, x + 43.5, y + 4f, new Color(250, 250, 250, 200).getRGB());

                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            psm18.drawString(extraText, x + 43.5, y + 4f, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST).getRGB());
                            RoundedUtil.drawRound(x + 1, y + 1, 46 + extraWidth, 12, 4, ThemeUtil.getThemeColor(ThemeType.ARRAYLIST));
                        });
                    }

                    textGui.setWidth((int) (47 + extraWidth));
                    for (int i = 0; i < name.length(); i++) {
                        final String character = String.valueOf(name.charAt(i));

                        final float off1 = off;
                        psb20.drawString(character, x + 4 + off1, y + 3.5, ThemeUtil.getThemeColorInt(i, ThemeType.ARRAYLIST));
                        int finalI = i;
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            psb20.drawString(character, x + 4 + off1, y + 3.5, ThemeUtil.getThemeColorInt(finalI, ThemeType.ARRAYLIST));
                        });
                        off += psb20.getWidth(character);
                    }
                } else {

                    if (ModuleInstance.getModule(PostProcessing.class).blur.enabled) {
                        MODERN_BLUR_RUNNABLES.add(() -> {
                            RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, 94 + extraWidth, 15, 3,
                                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                            RoundedUtil.drawRound(x, y, 80 + extraWidth, 14, 3, new Color(0, 0, 0, 220));
                        });
                    }

                    //    RoundedUtil.drawRound(x, y, 35 + extraWidth, 14, 4, new Color(0, 0, 0, 80));
                    RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, 94 + extraWidth, 15, 3,
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                            ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    RoundedUtil.drawRound(x, y, 93 + extraWidth, 14, 3, new Color(0, 0, 0, 220));

                    psm18.drawString(extraText, x + 90, y + 4f, new Color(200, 200, 200, 240).getRGB());

                    if (ModuleInstance.getModule(PostProcessing.class).bloom.enabled) {
                        MODERN_BLOOM_RUNNABLES.add(() -> {
                            RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, 94 + extraWidth, 15, 3,
                                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                        });
                    }

                    textGui.setWidth((int) (102 + extraWidth));
                    name = "REVERSAL CLIENT";

                    for (int i = 0; i < name.length(); i++) {
                        final String character = String.valueOf(name.charAt(i));

                        final float off1 = off;
                        psb20.drawString(character, x + 4 + off1, y + 3.5,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, i * 10, Color.WHITE, Color.BLACK, true).getRGB());
                        off += psb20.getWidth(character);
                    }
                }
                break;
            }
        }
    }

    public static void renderGameOverlay() {
        if (ModuleInstance.canDrawHUD()) {
            renderKeyStrokes();
            renderClientName();
            renderArrayList();
        }
    }
}
