package cn.stars.reversal.util.render;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.value.impl.ColorValue;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public final class ThemeUtil implements GameInstance {

    @Getter
    @Setter
    private String customClientName = "";
    private Color color;
    private String colorType;

    private final TimeUtil timer = new TimeUtil();

    public Color getThemeColor(final ThemeType type) {
        return getThemeColor(0, type, 1);
    }

    public int getThemeColorInt(final ThemeType type) {
        return getThemeColor(type).getRGB();
    }

    public int getThemeColorInt(final float colorOffset, final ThemeType type) {
        return getThemeColor(colorOffset, type, 1).getRGB();
    }

    public int getThemeColorInt(final float colorOffset, final ThemeType type, final float timeMultiplier) {
        return getThemeColor(colorOffset, type, timeMultiplier).getRGB();
    }

    public Color getThemeColor(final float colorOffset, final ThemeType type) {
        return getThemeColor(colorOffset, type, 1);
    }

    public static Color getThemeColor(final ThemeType type, final float colorOffset) {
        return getThemeColor(colorOffset, type, 1);
    }

    public Color getThemeColor(float colorOffset, final ThemeType type, final float timeMultiplier) {
        if (timer.hasReached(50 * 5)) {
            timer.reset();
            try {
                colorType = ModuleInstance.getClientSettings().colorType.getMode();
            } catch (Exception e) {
                colorType = "Rainbow";
            }
            color = Reversal.CLIENT_THEME_COLOR;
        }

        if (color == null || colorType == null) return color;

        float colorOffsetMultiplier = 1;

        if (type == ThemeType.GENERAL) {
            if (colorType.equals("Rainbow")) {
                colorOffsetMultiplier = 5f;
            } else if (colorType.equals("Fade")) {
                colorOffsetMultiplier = 2.2f;
            }
        }

        colorOffsetMultiplier *= ModuleInstance.getClientSettings().indexTimes.getFloat();
        colorOffset *= colorOffsetMultiplier;
        float speed = ModuleInstance.getClientSettings().indexSpeed.getFloat();

        final double timer = (System.currentTimeMillis() / 1E+8 * timeMultiplier) * 4E+5;

        switch (type) {
            case GENERAL:
            case ARRAYLIST: {
                switch (colorType) {
                    case "Rainbow":
                        color = new Color(ColorUtil.getColor(-(1 + colorOffset * 1.7f), 0.7f, 1));
                        break;
                    case "Fade":
                        final float offset1 = (float) (Math.abs(Math.sin(timer * 0.5 * speed + colorOffset * 0.45)) / 2.2f) + 1f;
                        color = ColorUtil.liveColorBrighter(Reversal.CLIENT_THEME_COLOR_BRIGHT, offset1);
                        break;
                    case "Double":
                        color = ColorUtils.INSTANCE.interpolateColorsBackAndForth((int) (20 * (1 / speed)), (int) colorOffset * 4, Reversal.CLIENT_THEME_COLOR, Reversal.CLIENT_THEME_COLOR_2, true);
                        break;
                    default:
                        color = Reversal.CLIENT_THEME_COLOR;
                        break;
                }
                break;
            }

            case LOGO: {
                switch (colorType) {
                    case "Rainbow":
                        color = new Color(ColorUtil.getColor(1 + colorOffset * 1.4f, 0.5f, 1));
                        break;
                    case "Fade":
                        color = Reversal.CLIENT_THEME_COLOR_BRIGHT;
                        break;
                    case "Double":
                        color = ColorUtils.INSTANCE.interpolateColorsBackAndForth(4, 1, Reversal.CLIENT_THEME_COLOR_BRIGHT, Reversal.CLIENT_THEME_COLOR_BRIGHT_2, true);
                        break;
                    default:
                        color = Reversal.CLIENT_THEME_COLOR;
                        break;
                }
                break;
            }

            case FLAT_COLOR:
            default:
                color = Reversal.CLIENT_THEME_COLOR;
        }

        if (ModuleInstance.getClientSettings().customAlpha.enabled) color = ColorUtil.reAlpha(color, ModuleInstance.getClientSettings().alpha.getInt());

        return color;
    }
}