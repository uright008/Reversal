package cn.stars.reversal.ui.hud;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.module.impl.hud.Arraylist;
import cn.stars.reversal.module.impl.hud.Keystrokes;
import cn.stars.reversal.module.impl.hud.TextGui;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class Hud implements GameInstance {
    public static float positionOfLastModule;
    private static final TimeUtil timer = new TimeUtil();
    private static final KeystrokeUtil forward = new KeystrokeUtil();
    private static final KeystrokeUtil backward = new KeystrokeUtil();
    private static final KeystrokeUtil left = new KeystrokeUtil();
    private static final KeystrokeUtil right = new KeystrokeUtil();
    private static final KeystrokeUtil space = new KeystrokeUtil();
    private static final KeystrokeUtil lmb = new KeystrokeUtil();
    private static final KeystrokeUtil rmb = new KeystrokeUtil();
    private static final ModuleComparator moduleComparator = new ModuleComparator();

    public static void renderKeyStrokes() {
        Keystrokes keystrokes = ModuleInstance.getModule(Keystrokes.class);
        if (keystrokes.isEnabled()) {

            forward.setUpKey(mc.gameSettings.keyBindForward);
            forward.updateAnimations();
            forward.drawButton(keystrokes.getX() + 35, keystrokes.getY(), 26);

            backward.setUpKey(mc.gameSettings.keyBindBack);
            backward.updateAnimations();
            backward.drawButton(keystrokes.getX() + 35, keystrokes.getY() + 30, 26);

            left.setUpKey(mc.gameSettings.keyBindLeft);
            left.updateAnimations();
            left.drawButton(keystrokes.getX() + 5, keystrokes.getY() + 30, 26);

            right.setUpKey(mc.gameSettings.keyBindRight);
            right.updateAnimations();
            right.drawButton(keystrokes.getX() + 65, keystrokes.getY() + 30, 26);

            space.setUpKey(mc.gameSettings.keyBindJump);
            space.updateAnimations();
            space.drawButton(keystrokes.getX() + 33, keystrokes.getY() + 90, 26);

            lmb.setUpMouse(0);
            lmb.updateAnimationsForMouse();
            lmb.drawButtonForMouse(keystrokes.getX() + 5, keystrokes.getY() + 60, 26);

            rmb.setUpMouse(1);
            rmb.updateAnimationsForMouse();
            rmb.drawButtonForMouse(keystrokes.getX() + 50, keystrokes.getY() + 60, 26);
        }
    }

    public static class ModuleComparator implements Comparator<Object> {
        @Override
        public int compare(final Object o1, final Object o2) {

            boolean canLocalize = (ModuleInstance.getModule(ClientSettings.class).theme.getMode().equals("Simple") || ModuleInstance.getModule(ClientSettings.class).theme.getMode().equals("Minecraft") || ModuleInstance.getModule(ClientSettings.class).theme.getMode().equals("Empathy") || ModuleInstance.getModule(ClientSettings.class).theme.getMode().equals("Shader")) && ModuleInstance.getModule(ClientSettings.class).localization.isEnabled();
            final String name = canLocalize ? I18n.format(((Module) o1).getModuleInfo().localizedName()) : ((Module) o1).getModuleInfo().name();
            final String name2 = canLocalize ? I18n.format(((Module) o2).getModuleInfo().localizedName()) : ((Module) o2).getModuleInfo().name();

            switch (ModuleInstance.getModule(ClientSettings.class).theme.getMode()) {
                case "Minecraft": {
                    return Float.compare(Minecraft.getMinecraft().fontRendererObj.getStringWidth(name2), Minecraft.getMinecraft().fontRendererObj.getStringWidth(name));
                }

                case "Reversal":
                case "Shader":
                case "Simple":
                case "Modern":
                case "ThunderHack":
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

        final float arraylistX = arraylist.getX() + arraylist.getWidth();

        List<Module> modules = Reversal.moduleManager.getEnabledModules();
        modules.sort(moduleComparator);

        int moduleCount = 0;
        float finalX = 0;

        for (final Module module : modules) {

            float posOnArraylist = 6f + moduleCount * regular16.height() * (mode.equals("Empathy") ? 1.25f : 1f);

            final String name = (mode.equals("Simple") || mode.equals("Minecraft") || mode.equals("Empathy") || mode.equals("Shader")) && ModuleInstance.getModule(ClientSettings.class).localization.enabled ? I18n.format(module.getModuleInfo().localizedName()) : module.getModuleInfo().name();

            final float renderX = module.getRenderX();
            final float renderY = arraylist.getY() + module.getRenderY();
            final int finalModuleCount = moduleCount;

            if ((module.getModuleInfo().category().equals(Category.RENDER) || module.getModuleInfo().category().equals(Category.HUD)) && ModuleInstance.getModule(Arraylist.class).noRenderModules.enabled)
                continue;

            if (ModuleInstance.isSpecialModule(module))
                continue;

            switch (mode) {
                case "Minecraft": {
                    mc.fontRendererObj.drawStringWithShadow(name, renderX, renderY, arraylist.colorValue.getColor(moduleCount).getRGB());

                    finalX = arraylistX - mc.fontRendererObj.getStringWidth(name);
                    break;
                }

                case "Shader": {
                    final double stringWidth = regular16.getWidth(name);
                    final float rectX = renderX - 1;
                    final float rectY = renderY - 1.5f;
                    final float lineX = (float)(rectX + stringWidth + 2.5);
                    final Color color = arraylist.colorValue.getColor(moduleCount);

                    RenderUtil.rect(rectX, rectY, (float)(stringWidth + 2.5), regular16.height(), new Color(0,0,0,60));
                    RenderUtil.rect(lineX, rectY - 0.1f, 1, regular16.height(), color);

                    finalX = arraylistX - (float)stringWidth;
                    regular16.drawString(name, renderX, renderY + 2, color.getRGB());

                    MODERN_POST_BLOOM_RUNNABLES.add((() -> regular16.drawString(name, renderX, renderY + 2, arraylist.colorValue.getColor(finalModuleCount).getRGB())));

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.rect(rectX, rectY, (float)(stringWidth + 2.5), regular16.height(), arraylist.colorValue.getColor(finalModuleCount));
                        RenderUtil.rect(lineX, rectY - 0.1f, 1, regular16.height(), arraylist.colorValue.getColor(finalModuleCount));
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> RenderUtil.rect(rectX, rectY, (float)(stringWidth + 2.5), regular16.height(), Color.BLACK));
                    break;
                }

                case "Simple": {
                    final double stringWidth = regular16.getWidth(name);
                    final float rectX = renderX - 1;
                    final float rectY = renderY - 1.5f;
                    final float lineX = (float)(rectX + stringWidth + 2.5);
                    final Color color = arraylist.colorValue.getColor(moduleCount);

                    RenderUtil.rect(rectX, rectY, (float)(stringWidth + 2.5), regular16.height(), new Color(0,0,0,60));
                    RenderUtil.rect(lineX, rectY - 0.1f, 1, regular16.height(), color);

                    regular16.drawString(name, renderX, renderY + 2, color.getRGB());

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.rect(rectX, rectY, (float)(stringWidth + 2.5), regular16.height(), Color.BLACK);
                        RenderUtil.rect(lineX, rectY - 0.1f, 1, regular16.height(), arraylist.colorValue.getColor(finalModuleCount));
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> RenderUtil.rect(rectX, rectY, (float)(stringWidth + 2.5), regular16.height(), Color.BLACK));

                    finalX = arraylistX - (float)stringWidth;
                    break;
                }

                case "Empathy": {
                    final double nameWidth = regular16.getWidth(name);
                    final float baseX = renderX - 1;
                    final float adjY = renderY - 1.5f;
                    final Color empathyColor = ColorUtil.empathyColor();
                    final Color glowColor = ColorUtil.empathyGlowColor();

                    RenderUtil.roundedRectangle(renderX - 16.5f, adjY, (float) (nameWidth + 3.5), 11.15f, 2f, empathyColor);

                    regular16.drawString(name, renderX - 15, renderY + 2.2f, arraylist.colorValue.getColor(moduleCount).getRGB());

                    final float rightBoxX = (float) (baseX + nameWidth + 2.0 - 13);
                    RenderUtil.roundedRectangle(rightBoxX, adjY, 10, 11.15f, 2f, empathyColor);
                    FontManager.getIcon(16).drawString("j", rightBoxX + 1, renderY + 2.2f, arraylist.colorValue.getColor(moduleCount).getRGB());

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(baseX - 15, adjY, (float) (nameWidth + 2.5), 11.15f, 2f, glowColor);
                        RenderUtil.roundedRectangle(rightBoxX, adjY, 10, 11.15f, 2f, glowColor);
                    });

                    MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                        regular16.drawString(name, renderX - 15, renderY + 2.2f, arraylist.colorValue.getColor(finalModuleCount).getRGB());
                        FontManager.getIcon(16).drawString("j", rightBoxX + 1, renderY + 2.2f, arraylist.colorValue.getColor(finalModuleCount).getRGB());
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(baseX - 15, adjY, (float) (nameWidth + 2.5), 11.15f, 2f, Color.BLACK);
                        RenderUtil.roundedRectangle(rightBoxX, adjY, 10, 11.15f, 2f, Color.BLACK);
                    });

                    finalX = arraylistX - (float) nameWidth;
                    break;
                }

                case "Reversal": {
                    final double stringWidth = regular16.getWidth(name);
                    final float rectY = renderY - 1.5f;

                    RenderUtil.rect(renderX - 1, rectY, (float) (stringWidth + 1.5), regular16.getHeight(), new Color(0, 0, 0, 60));
                    RenderUtil.roundedRect((float) (renderX + stringWidth + 1), rectY, 2, regular16.getHeight(), 2.5f, ColorUtil.liveColorBrighter(new Color(0, 255, 255), 1f));

                    regular16.drawString(name, renderX, renderY + 2, arraylist.colorValue.getColor(moduleCount).getRGB());

                    finalX = arraylistX - (float) stringWidth;
                    break;
                }

                case "Modern": {
                    final double stringWidth = regular16.getWidth(name);
                    final float baseX = renderX - 2;
                    final float rectY = renderY - 1.8f;
                    final Color modernColor = arraylist.colorValue.getColor(finalModuleCount);

                    RenderUtil.rect(baseX, rectY, (float) (stringWidth + 2.5), regular16.height(), new Color(0, 0, 0, 80));
                    RenderUtil.roundedRectangle((float) (renderX + stringWidth), rectY + 0.2f, 2, 11.8f, 2.5f, ColorUtil.liveColorBrighter(new Color(0, 255, 255), 1f));

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.rect(baseX, rectY, (float) (stringWidth + 2.5), regular16.height(), modernColor);
                        RenderUtil.roundedRectangle((float) (renderX + stringWidth), rectY + 0.2f, 2, 11.8f, 2.5f, ColorUtil.liveColorBrighter(new Color(0, 255, 255), 1f));
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> RenderUtil.rect(baseX, rectY, (float) (stringWidth + 2.5), regular16.height(), Color.BLACK));

                    regular16.drawString(name, renderX - 1, renderY + 0.6f, modernColor.getRGB());

                    MODERN_POST_BLOOM_RUNNABLES.add(() -> regular16.drawString(name, renderX - 1, renderY + 0.6f, modernColor.getRGB()));

                    finalX = arraylistX - (float) stringWidth;
                    break;
                }

                case "ThunderHack": {
                    final double stringWidth = regular16.getWidth(name);
                    final float baseX = renderX - 2;
                    final float rectY = renderY - 1.8f;
                    final Color interColor = ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, finalModuleCount * 25, Color.WHITE, Color.BLACK, true);

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.rect(baseX, rectY, (float) (stringWidth + 2.5), regular16.height(), interColor);
                        RenderUtil.roundedRectangle((float) (renderX + stringWidth), rectY + 0.2f, 2, 11.8f, 2.5f, interColor);
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> RenderUtil.rect(baseX, rectY, (float) (stringWidth + 2.5), regular16.height(), Color.BLACK));

                    RenderUtil.rect(baseX, rectY, (float) (stringWidth + 2.5), regular16.height(), new Color(0, 0, 0, 150));
                    RenderUtil.roundedRectangle((float) (renderX + stringWidth), rectY + 0.2f, 2, 11.8f, 2.5f, interColor);

                    regular16.drawString(name, renderX - 1, renderY + 0.6f, interColor.getRGB());

                    MODERN_POST_BLOOM_RUNNABLES.add(() -> regular16.drawString(name, renderX - 1, renderY + 0.6f, interColor.getRGB()));

                    finalX = arraylistX - (float) stringWidth;
                    break;
                }
            }
            moduleCount++;

            if (timer.hasReached(10)) {
                switch (ModuleInstance.getModule(ClientSettings.class).listAnimation.getMode()) {
                    case "Reversal":
                        module.renderX = (module.renderX * ((float) 6 - 1) + finalX) / (float) 6;
                        module.renderY = (module.renderY * ((float) 6 - 1) + posOnArraylist) / (float) 6;

                        break;
                    case "Slide":
                        module.renderX = (module.renderX * ((float) 6 - 1) + finalX) / (float) 6;

                        if (module.renderY < positionOfLastModule) {
                            module.renderY = posOnArraylist;
                        } else {
                            module.renderY = (module.renderY * ((float) 6 - 1) + posOnArraylist) / ((float) 6);
                        }
                        break;
                }
            }

            positionOfLastModule = posOnArraylist;

        }

        if (timer.hasReached(10)) {
            timer.reset();
        }

        arraylist.setHeight((int)(moduleCount * 12 * (mode.equals("Empathy") ? 1.25 : 1)));
    }

    private static void renderClientName() {
        TextGui textGui = ModuleInstance.getModule(TextGui.class);
        if (!textGui.isEnabled()) return;
        final String mode = ModuleInstance.getModule(ClientSettings.class).theme.getMode();
        final boolean useDefaultName = !ModuleInstance.getModule(TextGui.class).custom.isEnabled();
        final float roundStrength = ModuleInstance.getModule(ClientSettings.class).roundStrength.getFloat();

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
                    regular18Bold.drawStringWithShadow("R", textGui.getX() + 7, textGui.getY() + 5, textGui.colorValue.getColor().getRGB());
                    regular18Bold.drawStringWithShadow("eversal [" + Reversal.VERSION + "]", textGui.getX() + 7 + regular18Bold.getWidth("R"), textGui.getY() + 5f, new Color(230, 230, 230, 200).getRGB());
                } else {
                    textGui.setWidth((int) (20 + regular18Bold.getWidth(customName)));
                    regular18Bold.drawStringWithShadow(String.valueOf(customName.charAt(0)), textGui.getX() + 7, textGui.getY() + 5, textGui.colorValue.getColor().getRGB());
                    // 从字符串第二个字开始获取
                    regular18Bold.drawStringWithShadow(customName.substring(1), textGui.getX() + 7.5 + regular18Bold.getWidth(String.valueOf(customName.charAt(0))), textGui.getY() + 5f, new Color(230, 230, 230, 200).getRGB());
                }
                break;
            }

            case "Simple": {
                String extraText = " | " + mc.getSession().getUsername();

                if (useDefaultName) {

                    textGui.setWidth(100 + regular18.getWidth(extraText));
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    for (int i = 0; i < name.length(); i++) {
                        final String character = String.valueOf(name.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        NORMAL_RENDER_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 4 + off1, y + 4, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 4 + off1, y + 4, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += regular18Bold.getWidth(character);
                    }

                    float finalOff = off;
                    NORMAL_RENDER_RUNNABLES.add(() -> {
                        regular18.drawString(extraText, x + 4 + finalOff, y + 4, new Color(250, 250, 250, 200).getRGB());
                    });

                    NORMAL_PRE_RENDER_RUNNABLES.add(() -> {
                        RenderUtil.rect(x, y, finalOff + regular18.getWidth(extraText) + 7, regular18Bold.height() + 1.5, new Color(0, 0, 0, 80));
                    });

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.rect(x, y, finalOff + regular18.getWidth(extraText) + 7, regular18Bold.height() + 1.5, Color.BLACK);
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RenderUtil.rect(x, y, finalOff + regular18.getWidth(extraText) + 7, regular18Bold.height() + 1.5, Color.BLACK);
                    });

                } else {
                    textGui.setWidth(20 + regular18Bold.getWidth(customName) + regular18.getWidth(extraText));
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    for (int i = 0; i < customName.length(); i++) {
                        final String character = String.valueOf(customName.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        NORMAL_RENDER_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 4 + off1, y + 4, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 4 + off1, y + 4, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += regular18Bold.getWidth(character);
                    }

                    float finalOff = off;
                    NORMAL_RENDER_RUNNABLES.add(() -> {
                        regular18.drawString(extraText, x + 4 + finalOff, y + 4, new Color(250, 250, 250, 200).getRGB());
                    });

                    NORMAL_PRE_RENDER_RUNNABLES.add(() -> {
                        RenderUtil.rect(x, y, finalOff + regular18.getWidth(extraText) + 8, regular18Bold.height() + 1.5, new Color(0, 0, 0, 80));
                    });

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.rect(x, y, finalOff + regular18.getWidth(extraText) + 8, regular18Bold.height() + 1.5, Color.BLACK);
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RenderUtil.rect(x, y, finalOff + regular18.getWidth(extraText) + 8, regular18Bold.height() + 1.5, Color.BLACK);
                    });
                }
                break;
            }

            case "Shader": {
                if (useDefaultName) {

                    textGui.setWidth(100);
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    for (int i = 0; i < name.length(); i++) {
                        final String character = String.valueOf(name.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        NORMAL_RENDER_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 16 + off1, y + 3.5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 16 + off1, y + 3.5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += regular18Bold.getWidth(character);
                    }

                    float finalOff = off;
                    NORMAL_RENDER_RUNNABLES.add(() -> {
                        FontManager.getAtomic(16).drawString("2", x + 5, y + 5.5, textGui.colorValue.getColor().getRGB());
                    });

                    NORMAL_PRE_RENDER_RUNNABLES.add(() -> {
                        RenderUtil.rectForShaderTheme(x, y, finalOff + 20.5, regular18Bold.height() + 1.5, textGui.colorValue, false);
                    });

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.rectForShaderTheme(x, y, finalOff + 20.5, regular18Bold.height() + 1.5, textGui.colorValue, true);
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x, y, finalOff + 20.5, regular18Bold.height() + 1.5, roundStrength, Color.BLACK);
                    });

                } else {
                    textGui.setWidth(20 + regular18Bold.getWidth(customName));
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    for (int i = 0; i < customName.length(); i++) {
                        final String character = String.valueOf(customName.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        NORMAL_RENDER_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 16 + off1, y + 3.5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 16 + off1, y + 3.5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += regular18Bold.getWidth(character);
                    }

                    float finalOff = off;
                    NORMAL_RENDER_RUNNABLES.add(() -> {
                        FontManager.getAtomic(16).drawString("2", x + 5, y + 5.5, textGui.colorValue.getColor().getRGB());
                    });

                    NORMAL_PRE_RENDER_RUNNABLES.add(() -> {
                        RenderUtil.rectForShaderTheme(x, y, finalOff + 20.5, regular18Bold.height() + 1.5, textGui.colorValue, false);
                    });

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.rectForShaderTheme(x, y, finalOff + 20.5, regular18Bold.height() + 1.5, textGui.colorValue, true);
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x, y, finalOff + 20.5, regular18Bold.height() + 1.5, roundStrength, Color.BLACK);
                    });
                }
                break;
            }

            case "Empathy": {
                if (useDefaultName) {

                    textGui.setWidth(100);
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    for (int i = 0; i < name.length(); i++) {
                        final String character = String.valueOf(name.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        NORMAL_RENDER_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 16 + off1, y + 3.5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 16 + off1, y + 3.5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += regular18Bold.getWidth(character);
                    }

                    float finalOff = off;
                    NORMAL_RENDER_RUNNABLES.add(() -> FontManager.getAtomic(16).drawString("2", x + 5, y + 5.5, textGui.colorValue.getColor().getRGB()));

                    NORMAL_PRE_RENDER_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x, y, finalOff + 20, regular18Bold.height() + 1.5, 3f, ColorUtil.empathyColor());
                        RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, regular18Bold.height() - 3.5, 1f, textGui.colorValue.getColor());
                    });

                    MODERN_POST_BLOOM_RUNNABLES.add(() -> FontManager.getAtomic(16).drawString("2", x + 5, y + 5.5, textGui.colorValue.getColor().getRGB()));

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x, y, finalOff + 20, regular18Bold.height() + 1.5, 3f, ColorUtil.empathyGlowColor());
                        RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, regular18Bold.height() - 3.5, 1f, textGui.colorValue.getColor());
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x, y, finalOff + 20, regular18Bold.height() + 1.5, 3f, Color.BLACK);
                    });

                } else {

                    textGui.setWidth(20 + regular18Bold.getWidth(customName));
                    int x = textGui.getX() + 5;
                    int y = textGui.getY();
                    float off = 0;

                    for (int i = 0; i < customName.length(); i++) {
                        final String character = String.valueOf(customName.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        NORMAL_RENDER_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 16 + off1, y + 3.5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 16 + off1, y + 3.5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += regular18Bold.getWidth(character);
                    }

                    float finalOff = off;
                    NORMAL_RENDER_RUNNABLES.add(() -> FontManager.getAtomic(16).drawString("2", x + 5, y + 5.5, textGui.colorValue.getColor().getRGB()));

                    NORMAL_PRE_RENDER_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x, y, finalOff + 20, regular18Bold.height() + 1.5, 3f, ColorUtil.empathyColor());
                        RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, regular18Bold.height() - 3.5, 1f, textGui.colorValue.getColor());
                    });

                    MODERN_POST_BLOOM_RUNNABLES.add(() -> FontManager.getAtomic(16).drawString("2", x + 5, y + 5.5, textGui.colorValue.getColor().getRGB()));

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x, y, finalOff + 20, regular18Bold.height() + 1.5, 3f, ColorUtil.empathyGlowColor());
                        RenderUtil.roundedRectangle(x - 0.5, y + 2.5, 1.5, regular18Bold.height() - 3.5, 1f, textGui.colorValue.getColor());
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x, y, finalOff + 20, regular18Bold.height() + 1.5, 3f, Color.BLACK);
                    });
                }
                break;
            }

            case "Modern": {
                int x = textGui.getX() + 5;
                int y = textGui.getY();
                float off = 0;
                String extraText = " | " + mc.getSession().getUsername();
                float extraWidth = regular18.getWidth(extraText);

                if (useDefaultName) {
                    for (int i = 0; i < name.length(); i++) {
                        final String character = String.valueOf(name.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        NORMAL_RENDER_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 5 + off1, y + 5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 5 + off1, y + 5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += regular18Bold.getWidth(character);
                    }

                    NORMAL_RENDER_RUNNABLES.add(() -> {
                        regular18.drawString(extraText, x + 42, y + 5f, new Color(250, 250, 250, 200).getRGB());
                    });

                    NORMAL_PRE_RENDER_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x + 1, y, 45 + extraWidth, 14, roundStrength, new Color(0, 0, 0, 80));
                        RenderUtil.roundedOutlineRectangle(x, y, 47 + extraWidth, 16, roundStrength, 1, textGui.colorValue.getColor());
                    });

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x + 1, y + 1, 45 + extraWidth, 14, roundStrength, textGui.colorValue.getColor());
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x + 1, y + 1, 45 + extraWidth, 14, roundStrength, Color.BLACK);
                    });

                    textGui.setWidth((int) (50 + extraWidth));
                } else {
                    for (int i = 0; i < customName.length(); i++) {
                        final String character = String.valueOf(customName.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        NORMAL_RENDER_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 5 + off1, y + 5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 5 + off1, y + 5, textGui.colorValue.getColor(finalI).getRGB());
                        });
                        off += regular18Bold.getWidth(character);
                    }

                    float finalOff = off;
                    NORMAL_RENDER_RUNNABLES.add(() -> {
                        regular18.drawString(extraText, x + finalOff + 5, y + 5f, new Color(250, 250, 250, 200).getRGB());
                    });

                    NORMAL_PRE_RENDER_RUNNABLES.add(() -> {
                        RoundedUtil.drawRound(x + 1, y + 1, finalOff + 8 + extraWidth, 14, roundStrength, new Color(0, 0, 0, 80));
                        RenderUtil.roundedOutlineRectangle(x, y, finalOff + 10 + extraWidth, 16, roundStrength, 1, textGui.colorValue.getColor());
                    });

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x + 1, y + 1, finalOff + 8 + extraWidth, 14, roundStrength, textGui.colorValue.getColor());
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RoundedUtil.drawRound(x + 1, y + 1, 33 + extraWidth, 14, roundStrength, Color.BLACK);
                    });

                    textGui.setWidth((int) (off + 5 + extraWidth));
                }
                break;
            }

            case "ThunderHack": {
                int x = textGui.getX() + 5;
                int y = textGui.getY();
                float off = 0;
                String extraText = " | " + mc.getSession().getUsername();
                float extraWidth = regular18.getWidth(extraText);

                if (useDefaultName) {
                    for (int i = 0; i < name.length(); i++) {
                        final String character = String.valueOf(name.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        NORMAL_RENDER_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 5 + off1, y + 3.5,
                                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, finalI * 10, Color.WHITE, Color.BLACK, true).getRGB());
                        });
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 5 + off1, y + 3.5, ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, finalI * 10, Color.WHITE, Color.BLACK, true).getRGB());
                        });
                        off += regular18Bold.getWidth(character);
                    }

                    NORMAL_RENDER_RUNNABLES.add(() -> {
                        regular18.drawString(extraText, x + 42, y + 4f, new Color(200, 200, 200, 240).getRGB());
                    });

                    NORMAL_PRE_RENDER_RUNNABLES.add(() -> {
                        RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, 47 + extraWidth, 15, roundStrength,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                        RoundedUtil.drawRound(x, y, 46 + extraWidth, 14, roundStrength, new Color(0, 0, 0, 220));
                    });

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, 47 + extraWidth, 15, roundStrength,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, 47 + extraWidth, 15, roundStrength,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                        RoundedUtil.drawRound(x, y, 46 + extraWidth, 14, 3, new Color(0, 0, 0, 220));
                    });

                    textGui.setWidth((int) (102 + extraWidth));
                } else {
                    for (int i = 0; i < customName.length(); i++) {
                        final String character = String.valueOf(customName.charAt(i));

                        final float off1 = off;
                        int finalI = i;
                        NORMAL_RENDER_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 5 + off1, y + 4,
                                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, finalI * 10, Color.WHITE, Color.BLACK, true).getRGB());
                        });
                        MODERN_POST_BLOOM_RUNNABLES.add(() -> {
                            regular18Bold.drawString(character, x + 5 + off1, y + 4, ColorUtils.INSTANCE.interpolateColorsBackAndForth(5, finalI * 10, Color.WHITE, Color.BLACK, true).getRGB());
                        });
                        off += regular18Bold.getWidth(character);
                    }

                    float finalOff = off;
                    NORMAL_RENDER_RUNNABLES.add(() -> {
                        regular18.drawString(extraText, x + finalOff + 5f, y + 4f, new Color(200, 200, 200, 240).getRGB());
                    });

                    NORMAL_PRE_RENDER_RUNNABLES.add(() -> {
                        RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, finalOff + 10 + extraWidth, 15, roundStrength,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                        RoundedUtil.drawRound(x, y, finalOff + 9 + extraWidth, 14, roundStrength, new Color(0, 0, 0, 220));
                    });

                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, finalOff + 10 + extraWidth, 15, roundStrength,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                    });

                    MODERN_BLUR_RUNNABLES.add(() -> {
                        RoundedUtil.drawGradientRound(x - 0.5f, y - 0.5f, finalOff + 10 + extraWidth, 15, roundStrength,
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                                ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                        RoundedUtil.drawRound(x, y, finalOff + 9 + extraWidth, 14, roundStrength, new Color(0, 0, 0, 220));
                    });

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
