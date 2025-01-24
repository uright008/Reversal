package cn.stars.reversal.util.render;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.ui.theme.Theme;
import cn.stars.reversal.util.animation.simple.SimpleAnimation;
import cn.stars.reversal.util.math.MathUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import com.ibm.icu.text.NumberFormat;
import lombok.experimental.UtilityClass;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.regex.Pattern;

@UtilityClass
public final class ColorUtil {

    public static SimpleAnimation[] animation = {
            new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F),
            new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F),
            new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F),
            new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F),

            new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F),
            new SimpleAnimation(0.0F), new SimpleAnimation(0.0F), new SimpleAnimation(0.0F)
    };

    public static void glColor(final int hex) {
        final float a = (hex >> 24 & 0xFF) / 255.0F;
        final float r = (hex >> 16 & 0xFF) / 255.0F;
        final float g = (hex >> 8 & 0xFF) / 255.0F;
        final float b = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, a);
    }

    public static int hexColor(int red, int green, int blue, int alpha) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int getAlphaFromColor(int color) {
        return color >> 24 & 0xFF;
    }

    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * (float)c.getRed();
        float g = 0.003921569f * (float)c.getGreen();
        float b = 0.003921569f * (float)c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    public static int swapAlpha(int color, float alpha) {
        int f = color >> 16 & 0xFF;
        int f1 = color >> 8 & 0xFF;
        int f2 = color & 0xFF;
        return hexColor(f, f1, f2, (int) alpha);
    }

    public static Color withAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) MathUtil.clamp(0, 255, alpha));
    }

    public static Color empathyGlowColor() {
        return ModuleInstance.getModule(ClientSettings.class).empathyGlow.enabled ? ThemeUtil.getThemeColor(ThemeType.ARRAYLIST) : Color.BLACK;
    }

    public static Color empathyColor() {
        return new Color(20, 20, 20, 220);
    }

    private final Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");

    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }

    //Opacity value ranges from 0-1
    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, opacity);
    }

    public static int getOverallColorFrom(int color1, int color2, float percentTo2) {
        final int finalRed = (int) MathUtil.lerp(color1 >> 16 & 0xFF, color2 >> 16 & 0xFF, percentTo2),
                finalGreen = (int) MathUtil.lerp(color1 >> 8 & 0xFF, color2 >> 8 & 0xFF, percentTo2),
                finalBlue = (int) MathUtil.lerp(color1 & 0xFF, color2 & 0xFF, percentTo2),
                finalAlpha = (int) MathUtil.lerp(color1 >> 24 & 0xFF, color2 >> 24 & 0xFF, percentTo2);
        return new Color(finalRed, finalGreen, finalBlue, finalAlpha).getRGB();
    }

    public Color liveColorBrighter(final Color c, final float factor) {
        return brighter(c, factor);
    }

    public Color liveColorDarker(final Color c, final float factor) {
        return darker(c, factor);
    }

    public Color brighter(final Color c, final float factor) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        final int alpha = c.getAlpha();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         * Alan got this from Color.java
         */

        final int i = (int) (1.0 / (1.0 - factor));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return new Color(Math.min((int) (r / factor), 255),
                Math.min((int) (g / factor), 255),
                Math.min((int) (b / factor), 255),
                alpha);
    }

    public static Color getFontColor(int id, int alpha) {
        Color rawColor = getRawFontColor(id);
        int speed = 12;

        if(id == 1) {
            animation[12].setAnimation(rawColor.getRed(), speed);
            animation[13].setAnimation(rawColor.getGreen(), speed);
            animation[14].setAnimation(rawColor.getBlue(), speed);

            return new Color((int) animation[12].getValue(), (int) animation[13].getValue(), (int) animation[14].getValue(), alpha);
        }

        if(id == 2) {
            animation[15].setAnimation(rawColor.getRed(), speed);
            animation[16].setAnimation(rawColor.getGreen(), speed);
            animation[17].setAnimation(rawColor.getBlue(), speed);

            return new Color((int) animation[15].getValue(), (int) animation[16].getValue(), (int) animation[17].getValue(), alpha);
        }

        return rawColor;
    }

    private static Color getRawFontColor(int id) {
        Color color = new Color(0, 0, 255);
        boolean dark = Reversal.guiTheme.currentTheme == Theme.DARKMODE;

        switch(id) {
            case 1:
                if(dark) {
                    color = new Color(255, 255, 255);
                }else {
                    color = new Color(27, 27, 27);
                }
                break;
            case 2:
                if(dark) {
                    color = new Color(207, 209, 210);
                }else {
                    color = new Color(96, 97, 97);
                }
                break;
        }

        return color;
    }

    public static Color getFontColor(int id) {
        return getFontColor(id, 255);
    }

    private static Color getRawBackgroundColor(int id) {
        Color color = new Color(255, 0, 0);
        boolean dark = Reversal.guiTheme.currentTheme == Theme.DARKMODE;

        switch(id) {
            case 1:
                if(dark) {
                    color = new Color(26, 33, 42);
                }else {
                    color = new Color(232, 234, 240);
                }
                break;
            case 2:
                if(dark) {
                    color = new Color(35, 40, 46);
                }else {
                    color = new Color(239, 244, 249);
                }
                break;
            case 3:
                if(dark) {
                    color = new Color(46, 51, 57);
                }else {
                    color = new Color(247, 250, 252);
                }
                break;
            case 4:
                if(dark) {
                    color = new Color(57, 61, 67);
                }else {
                    color = new Color(253, 254, 254);
                }
                break;
        }
        return color;
    }

    public static Color getBackgroundColor(int id, int alpha) {

        int speed = 12;
        Color rawColor = getRawBackgroundColor(id);

        if(id == 1) {
            animation[0].setAnimation(rawColor.getRed(), speed);
            animation[1].setAnimation(rawColor.getGreen(), speed);
            animation[2].setAnimation(rawColor.getBlue(), speed);

            return new Color((int) animation[0].getValue(), (int) animation[1].getValue(), (int) animation[2].getValue(), alpha);
        }

        if(id == 2) {
            animation[3].setAnimation(rawColor.getRed(), speed);
            animation[4].setAnimation(rawColor.getGreen(), speed);
            animation[5].setAnimation(rawColor.getBlue(), speed);

            return new Color((int) animation[3].getValue(), (int) animation[4].getValue(), (int) animation[5].getValue(), alpha);
        }

        if(id == 3) {
            animation[6].setAnimation(rawColor.getRed(), speed);
            animation[7].setAnimation(rawColor.getGreen(), speed);
            animation[8].setAnimation(rawColor.getBlue(), speed);

            return new Color((int) animation[6].getValue(), (int) animation[7].getValue(), (int) animation[8].getValue(), alpha);
        }

        if(id == 4) {
            animation[9].setAnimation(rawColor.getRed(), speed);
            animation[10].setAnimation(rawColor.getGreen(), speed);
            animation[11].setAnimation(rawColor.getBlue(), speed);

            return new Color((int) animation[9].getValue(), (int) animation[10].getValue(), (int) animation[11].getValue(), alpha);
        }

        return rawColor;
    }

    public static Color getBackgroundColor(int id) {
        return getBackgroundColor(id, 255);
    }

    public Color darker(final Color c, final double FACTOR) {
        return new Color(Math.max((int) (c.getRed() * FACTOR), 0),
                Math.max((int) (c.getGreen() * FACTOR), 0),
                Math.max((int) (c.getBlue() * FACTOR), 0),
                c.getAlpha());
    }

    public static int darker(int color, float factor) {
        int r = (int) ((color >> 16 & 0xFF) * factor);
        int g = (int) ((color >> 8 & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        int a = color >> 24 & 0xFF;
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF | (a & 0xFF) << 24;
    }

    public static int getColor(final float hueoffset, final float saturation, final float brightness) {
        final float speed = 4500;
        final float hue = (System.currentTimeMillis() % (int) speed) / speed;

        return Color.HSBtoRGB(hue - hueoffset / 54, saturation, brightness);
    }

    public static int getStaticColor(final float hueoffset, final float saturation, final float brightness) {
        return Color.HSBtoRGB(hueoffset / 54, saturation, brightness);
    }

    public Color blend2colors(final Color color1, final Color color2, double offset) {
        final float hue = System.currentTimeMillis();

        offset += hue;

        if (offset > 1) {
            final double left = offset % 1;
            final int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;
        }
        final double inversePercent = 1 - offset;

        final int redPart = (int) (color1.getRed() * inversePercent + color2.getRed() * offset);
        final int greenPart = (int) (color1.getGreen() * inversePercent + color2.getGreen() * offset);
        final int bluePart = (int) (color1.getBlue() * inversePercent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }

    public static int getRainbow() {
        final float hue = (System.currentTimeMillis() % 10000) / 10000f;
        return Color.HSBtoRGB(hue, 0.5f, 1);
    }

    public static Color getClientColor() {
        return ThemeUtil.getThemeColor(ThemeType.LOGO);
    }

    public String stripColor(final String text) {
        return COLOR_PATTERN.matcher(text).replaceAll("");
    }

    public Color mixColors(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    public static Color blendColors(final float[] fractions, final Color[] colors, final float progress) {
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        }
        if (fractions.length == colors.length) {
            final int[] getFractionBlack = getFraction(fractions, progress);
            final float[] range = new float[]{fractions[getFractionBlack[0]], fractions[getFractionBlack[1]]};
            final Color[] colorRange = new Color[]{colors[getFractionBlack[0]], colors[getFractionBlack[1]]};
            final float max = range[1] - range[0];
            final float value = progress - range[0];
            final float weight = value / max;
            return blend(colorRange[0], colorRange[1], 1.0f - weight);
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }

    public static int[] getFraction(final float[] fractions, final float progress) {
        int startPoint;
        final int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blend(final Color color1, final Color color2, final double ratio) {
        final float r = (float) ratio;
        final float ir = 1.0f - r;
        final float[] rgb1 = new float[3];
        final float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        } catch (final IllegalArgumentException exp) {
            final NumberFormat nf = NumberFormat.getNumberInstance();
            // System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color3;
    }

    public static String getColor(int n) {
        if (n != 1) {
            if (n == 2) {
                return "\u00a7a";
            }
            if (n == 3) {
                return "\u00a73";
            }
            if (n == 4) {
                return "\u00a74";
            }
            if (n >= 5) {
                return "\u00a7e";
            }
        }
        return "\u00a7f";
    }
}
