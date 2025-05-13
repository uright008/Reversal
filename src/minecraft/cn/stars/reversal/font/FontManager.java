package cn.stars.reversal.font;

import net.minecraft.client.Minecraft;

import java.util.HashMap;

public class FontManager {
    private static final HashMap<Integer, ModernFontRenderer> REGULAR = new HashMap<>();
    private static final HashMap<Integer, ModernFontRenderer> REGULARBOLD = new HashMap<>();
    private static final HashMap<Integer, ModernFontRenderer> RAINBOW_PARTY = new HashMap<>();

    private static final HashMap<Integer, ModernFontRenderer> MOREICONS = new HashMap<>();
    private static final HashMap<Integer, ModernFontRenderer> SPECIAL_ICON = new HashMap<>();

    private static final HashMap<Integer, ModernFontRenderer> PRODUCT_SANS_BOLD = new HashMap<>();
    private static final HashMap<Integer, ModernFontRenderer> PRODUCT_SANS_REGULAR = new HashMap<>();
    private static final HashMap<Integer, ModernFontRenderer> PRODUCT_SANS_MEDIUM = new HashMap<>();

    private static final HashMap<Integer, ModernFontRenderer> CHECK = new HashMap<>();
    private static final HashMap<Integer, ModernFontRenderer> CUR = new HashMap<>();
    private static final HashMap<Integer, ModernFontRenderer> ATOMIC = new HashMap<>();

    public static MFont getRainbowParty(int size) {
        return get(RAINBOW_PARTY,  size, "RainbowParty", true, true, false, false);
    }

    public static MFont getRegular(int size) {
        return get(REGULAR,  size, "regular", true, true, false, true);
    }

    public static MFont getRegularBold(int size) {
        return get(REGULARBOLD,  size, "regularBold", true, true, false, true);
    }

    public static MFont getCur(int size) {
        return get(CUR,  size, "curiosity", true, true);
    }

    public static MFont getAtomic(int size) {
        return get(ATOMIC,  size, "atomic", true, true);
    }


    public static MFont getCheck(final int size) {
        return get(CHECK, size, "check", true, true);
    }

    public static MFont getIcon(final int size) {
        return get(MOREICONS, size, "Moreicon", true, true);
    }

    public static MFont getSpecialIcon(final int size) {
        return get(SPECIAL_ICON, size, "special_icon", true, true);
    }

    public static MFont getPSB(final int size) {
        return get(PRODUCT_SANS_BOLD, size, "ProductSansBold", true, true, false, false);
    }

    public static MFont getPSR(final int size) {
        return get(PRODUCT_SANS_REGULAR, size, "product_sans_regular", true, true, false, false);
    }

    public static MFont getPSM(final int size) {
        return get(PRODUCT_SANS_MEDIUM, size, "ProductSansMedium", true, true, false, false);
    }

    public static net.minecraft.client.gui.FontRenderer getMinecraft() {
        return Minecraft.getMinecraft().fontRendererObj;
    }

    private static MFont get(HashMap<Integer, ModernFontRenderer> map, int size, String name, boolean fractionalMetrics, boolean AA) {
        return get(map, size, name, fractionalMetrics, AA, false, false);
    }

    private static MFont get(HashMap<Integer, ModernFontRenderer> map, int size, String name, boolean fractionalMetrics, boolean AA, boolean otf, boolean international) {
        if (!map.containsKey(size)) {
            final java.awt.Font font = FontUtil.getResource("reversal/font/" + name + (otf ? ".otf" : ".ttf"), size);

            if (font != null) {
                map.put(size, new ModernFontRenderer(font, fractionalMetrics, AA, international));
            }
        }
        return map.get(size);
    }

}