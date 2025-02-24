package cn.stars.reversal.font;

import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.Transformer;
import cn.stars.reversal.util.render.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.optifine.CustomColors;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * @author Patrick, Hazsi
 * @since 11/03/2021
 */
public class ModernFontRenderer extends MFont {

    private static final String ALPHABET = "ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String COLOR_CODE_CHARACTERS = "0123456789abcdefklmnor";
    private static final Color TRANSPARENT_COLOR = new Color(255, 255, 255, 0);
    private static final float SCALE = 0.5f;
    private static final float SCALE_INVERSE = 1 / SCALE;
    private static final char COLOR_INVOKER = '\247';
    private static final int[] COLOR_CODES = new int[32];
    private static final int LATIN_MAX_AMOUNT = 256;
    private static final int INTERNATIONAL_MAX_AMOUNT = 65535;
    private static final int MARGIN_WIDTH = 4;
    private static final int MASK = 0xFF;

    private final java.awt.Font font;
    private final boolean fractionalMetrics;
    private final float fontHeight;
    private final FontCharacter[] defaultCharacters = new FontCharacter[LATIN_MAX_AMOUNT];
    private final FontCharacter[] internationalCharacters = new FontCharacter[INTERNATIONAL_MAX_AMOUNT];
    private final FontCharacter[] boldCharacters = new FontCharacter[LATIN_MAX_AMOUNT];
    private boolean antialiasing = true, international = false;

    public ModernFontRenderer(java.awt.Font font, boolean fractionalMetrics, boolean antialiasing, boolean international) {
        this.antialiasing = antialiasing;
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;
        this.fontHeight = (float) (font.getStringBounds(ALPHABET, new FontRenderContext(new AffineTransform(), antialiasing, fractionalMetrics)).getHeight() / 2f);
        this.international = international;
    }

    public ModernFontRenderer(final java.awt.Font font, final boolean fractionalMetrics, final boolean antialiasing) {
        this.antialiasing = antialiasing;
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;
        this.fontHeight = (float) (font.getStringBounds(ALPHABET, new FontRenderContext(new AffineTransform(), antialiasing, fractionalMetrics)).getHeight() / 2);
        this.fillCharacters(this.defaultCharacters, java.awt.Font.PLAIN);
        this.fillCharacters(this.boldCharacters, java.awt.Font.BOLD);
    }

    public ModernFontRenderer(final java.awt.Font font, final boolean fractionalMetrics) {
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;
        this.fontHeight = (float) (font.getStringBounds(ALPHABET, new FontRenderContext(new AffineTransform(), true, fractionalMetrics)).getHeight() / 2);
        this.fillCharacters(this.defaultCharacters, java.awt.Font.PLAIN);
        this.fillCharacters(this.boldCharacters, java.awt.Font.BOLD);
    }

    static {
        for (int i = 0; i < 32; ++i) {
            final int amplifier = (i >> 3 & 1) * 85;
            int red = (i >> 2 & 1) * 170 + amplifier;
            int green = (i >> 1 & 1) * 170 + amplifier;
            int blue = (i & 1) * 170 + amplifier;
            if (i == 6) {
                red += 85;
            }
            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            COLOR_CODES[i] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
        }
    }

    /**
     * Populate a given {@link FontCharacter} array. The process of instantiating a FontCharacter calculates the
     * height and width of the character, as well as uploading the texture to OpenGL and storing the OpenGL texture
     * ID for rendering later. This process must be run for every character before it is rendered, as is done in this
     * method.
     *
     * @param characters A reference to a FontCharacter array to populate
     * @param style      The font style to use, as defined in {@link java.awt.Font}. Acceptable values are 0 = PLAIN, 1 = BOLD,
     *                   2 = ITALIC.
     */
    public void fillCharacters(final FontCharacter[] characters, final int style) {
        final Font font = this.font.deriveFont(style);
        final BufferedImage fontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D fontGraphics = (Graphics2D) fontImage.getGraphics();
        final FontMetrics fontMetrics = fontGraphics.getFontMetrics(font);

        for (int i = 0; i < characters.length; ++i) {
            final char character = (char) i;
            final Rectangle2D charRectangle = fontMetrics.getStringBounds(String.valueOf(character), fontGraphics);

            // Draw the character. This is cached into an OpenGL texture so that this process doesn't need to
            // be repeated on every frame that the character is later rendered on.

            final BufferedImage charImage = new BufferedImage(MathHelper.ceiling_double_int(
                    (float) charRectangle.getWidth() + 8.0f), MathHelper.ceiling_double_int(
                    (float) charRectangle.getHeight()), BufferedImage.TYPE_INT_ARGB);

            final Graphics2D charGraphics = (Graphics2D) charImage.getGraphics();
            charGraphics.setFont(font);

            // Calculate the width and height of the character
            final int width = charImage.getWidth();
            final int height = charImage.getHeight();
            charGraphics.setColor(TRANSPARENT_COLOR);
            charGraphics.fillRect(0, 0, width, height);
            setRenderHints(charGraphics);
            // Use getAscent() instead of getSize(), to fix the bottom of the character not displaying
            charGraphics.drawString(String.valueOf(character), MARGIN_WIDTH, fontMetrics.getAscent());

            // Generate a new OpenGL texture, and pass it along to uploadTexture() with the image of the character so
            // that it can be stored as a complete OpenGL texture for later use
            final int charTexture = GL11.glGenTextures();
            uploadTexture(charTexture, charImage, width, height);

            // Store the completed character back into the provided character array
            characters[i] = new FontCharacter(charTexture, width, height);
        }
    }

    public void fillCharacters(char character, final int style) {
        final Font font = this.font.deriveFont(style);
        final BufferedImage fontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D fontGraphics = (Graphics2D) fontImage.getGraphics();
        final FontMetrics fontMetrics = fontGraphics.getFontMetrics(font);

        final Rectangle2D charRectangle = fontMetrics.getStringBounds(String.valueOf(character), fontGraphics);

        // Draw the character. This is cached into an OpenGL texture so that this process doesn't need to
        // be repeated on every frame that the character is later rendered on.

        final BufferedImage charImage = new BufferedImage(MathHelper.ceiling_double_int(
                (float) charRectangle.getWidth() + 8.0f), MathHelper.ceiling_double_int(
                (float) charRectangle.getHeight()), BufferedImage.TYPE_INT_ARGB);

        final Graphics2D charGraphics = (Graphics2D) charImage.getGraphics();
        charGraphics.setFont(font);

        // Calculate the width and height of the character
        final int width = charImage.getWidth();
        final int height = charImage.getHeight();
        charGraphics.setColor(TRANSPARENT_COLOR);
        charGraphics.fillRect(0, 0, width, height);
        setRenderHints(charGraphics);
        // Use getAscent() instead of font.getSize(), to fix the bottom of the character not displaying
        charGraphics.drawString(String.valueOf(character), MARGIN_WIDTH, fontMetrics.getAscent());

        // Generate a new OpenGL texture, and pass it along to uploadTexture() with the image of the character so
        // that it can be stored as a complete OpenGL texture for later use
        final int charTexture = GL11.glGenTextures();
        uploadTexture(charTexture, charImage, width, height);

        // Store the completed character back into the provided character array
        internationalCharacters[character] = new FontCharacter(charTexture, width, height);
    }

    public void setRenderHints(final Graphics2D graphics) {
        graphics.setColor(Color.WHITE);
        if (antialiasing) {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    }

    public void uploadTexture(final int texture, final BufferedImage image, final int width, final int height) {
        final int[] pixels = image.getRGB(0, 0, width, height, new int[width * height], 0, width);
        final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(width * height * MARGIN_WIDTH);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int pixel = pixels[x + y * width];
                byteBuffer.put((byte) ((pixel >> 16) & MASK));
                byteBuffer.put((byte) ((pixel >> 8) & MASK));
                byteBuffer.put((byte) (pixel & MASK));
                byteBuffer.put((byte) ((pixel >> 24) & MASK));
            }
        }
        byteBuffer.flip();
        GlStateManager.bindTexture(texture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
    }

    public String trimStringToWidth(String text, float width, boolean reverse, boolean more) {
        if (text == null) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            int startIndex = reverse ? text.length() - 1 : 0;
            int step = reverse ? -1 : 1;

            String nextChar = "";
            for (int i = startIndex; i <= text.length() - 1 && i >= 0 && getStringWidth(builder + nextChar) <= width; i += step) {
                builder.append(text.charAt(i));
                nextChar = reverse ? (i == 0 ? "" : String.valueOf(text.charAt(i + step))) : (i == text.length() - 1 ? "" : String.valueOf(text.charAt(i + step)));
            }

            if (reverse) builder.reverse();
            String result = builder.toString();
            if (more && !text.equals(result)) {
                result = reverse? "..." + result : result + "...";
            }
            return result;
        }
    }

    public String autoReturn(String text, float returnWidth, int maxReturns) {
        if (text == null || returnWidth <= 0 || maxReturns < -1) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        text = trimStringToWidth(text, returnWidth * maxReturns - getWidth(" ..."), false, true);

        StringBuilder result = new StringBuilder();
        StringBuilder line = new StringBuilder();
        int currentReturns = 0;

        for (char c : text.toCharArray()) {
            line.append(c);
            if (getWidth(line.toString()) > returnWidth) {
                if (maxReturns != -1 && currentReturns >= maxReturns) {
                    break;
                } else {
                    // 换行
                    result.append(line.substring(0, line.length() - 1)).append("\n");
                    line = new StringBuilder().append(c); // 重新开始新的一行
                    currentReturns++;
                }
            }
        }

        // 添加最后一行（如果有的话）
        result.append(line);

        return result.toString();
    }

    public int autoReturnCount(String text, float returnWidth, int maxReturns) {
        if (text == null || returnWidth <= 0 || maxReturns < -1) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        text = trimStringToWidth(text, returnWidth * maxReturns - getWidth(" ..."), false, true);

        StringBuilder result = new StringBuilder();
        StringBuilder line = new StringBuilder();
        int currentReturns = 0;

        for (char c : text.toCharArray()) {
            line.append(c);
            if (getWidth(line.toString()) > returnWidth) {
                if (maxReturns != -1 && currentReturns >= maxReturns) {
                    break;
                } else {
                    // 换行
                    result.append(line.substring(0, line.length() - 1)).append("\n");
                    line = new StringBuilder().append(c); // 重新开始新的一行
                    currentReturns++;
                }
            }
        }
        // 添加最后一行（如果有的话）
        result.append(line);

        return 1 + currentReturns;
    }

    public int drawString(final String text, final double x, final double y, final int color) {
        return drawString(text, x, y, color, false);
    }

    public float drawCenteredString(final String text, final double x, final double y, final int color) {
        return drawString(text, x - width(text) / 2f, y, color, false); // whoever bitshifted this instead of diving by 2 is a fucking nerd and virgin
    }

    public float drawRightString(String text, double x, double y, int color) {
        return drawString(text, x - (width(text)), y, color, false);
    }

    public int drawStringWithShadow(final String text, final double x, final double y, final int color) {
        drawString(text, x + 1, y + 1, new Color(50,50,50,50).getRGB(), false);
        return drawString(text, x, y, color, false);
    }

    public int drawString(String text, double x, double y, int color, final boolean shadow) {
        if (text == null) return 0;
        if (text.contains(Minecraft.getMinecraft().session.getUsername())) text = Transformer.constructString(text).replaceAll("§.", "");

        if (!this.international && this.requiresInternationalFont(text)) {
            return FontManager.getRegular(this.font.getSize() - 1).drawString(text, x, y + 1, color);
        }

        final FontCharacter[] characterSet = this.international ? internationalCharacters : defaultCharacters;

        double givenX = x;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glScalef(SCALE, SCALE, SCALE);

        x -= MARGIN_WIDTH / SCALE_INVERSE;
        y -= MARGIN_WIDTH / SCALE_INVERSE;
        x *= SCALE_INVERSE;
        y *= SCALE_INVERSE;
        y -= fontHeight / 5;

        final double startX = x;

        final int length = text.length();
        ColorUtil.glColor(shadow ? 50 : color);

        for (int i = 0; i < length; ++i) {
            final char character = text.charAt(i);

            if ((character == 167) && i + 1 < text.length()) {
                int l = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));

                if (l < 16) {
                    if (l < 0) {
                        l = 15;
                    }

                    if (shadow) {
                        l += 16;
                    }

                    int i1 = this.COLOR_CODES[l];

                    if (Config.isCustomColors()) {
                        i1 = CustomColors.getTextColor(l, i1);
                    }

                    GlStateManager.color((float) (i1 >> 16) / 255.0F, (float) (i1 >> 8 & 255) / 255.0F, (float) (i1 & 255) / 255.0F, 1f);
                } else if (l == 21) {
                    GlStateManager.color((float) (color >> 16) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, 1f);
                }

                ++i;
            } else {

                if (this.international) {
                    if (internationalCharacters[character] == null)
                        fillCharacters(character, java.awt.Font.PLAIN);
                } else {
                    if (defaultCharacters[character] == null)
                        fillCharacters(defaultCharacters, java.awt.Font.PLAIN);
                }

                try {
                    if (character == '\n') {
                        x = startX;
                        y += height() * 2;
                        continue;
                    }

                    final FontCharacter fontCharacter = characterSet[character];
                    fontCharacter.render((float) x, (float) y);
                    x += fontCharacter.getWidth() - MARGIN_WIDTH * 2;
                } catch (Exception exception) {
                    System.out.println("Character \"" + character + "\" was out of bounds " +
                            "(" + ((int) character) + " out of bounds for " + characterSet.length + ")");
                }
            }
        }

        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        GlStateManager.bindTexture(0);

        return (int) (x - givenX);
    }

    public int width(String text) {
        if (text == null) return 0;
        if (text.contains(Minecraft.getMinecraft().session.getUsername())) text = Transformer.constructString(text).replaceAll("§.", "");;

        if (!this.international && this.requiresInternationalFont(text)) {
            return FontManager.getRegular(this.font.getSize()).width(text);
        }

        final FontCharacter[] characterSet = this.international ? internationalCharacters : defaultCharacters;
        final int length = text.length();
        float width = 0;

        for (int i = 0; i < length; ++i) {
            final char character = text.charAt(i);

            if (this.international) {
                if (internationalCharacters[character] == null)
                    fillCharacters(character, java.awt.Font.PLAIN);
            } else {
                if (defaultCharacters[character] == null)
                    fillCharacters(defaultCharacters, java.awt.Font.PLAIN);
            }
//            if (previousCharacter != COLOR_INVOKER) {
//                if (character == COLOR_INVOKER) {
//                    final int index = COLOR_CODE_CHARACTERS.indexOf(text.toLowerCase().charAt(i + 1));
//                    if (index < 16 || index == 21) {
//                        characterSet = defaultCharacters;
//                    } else if (index == 17) {
//                        characterSet = boldCharacters;
//                    }
//                } else if (characterSet.length > character) {
//                    width += characterSet[character].getWidth() - MARGIN_WIDTH * 2;
//                }
//            }
            width += characterSet[character].getWidth() - MARGIN_WIDTH * 2f;
        }

        return (int) width / 2;
    }

    public float height() {
        return fontHeight;
    }

    private boolean requiresInternationalFont(String text) {
        int highest = 0;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) > highest) highest = text.charAt(i);
        }

        return highest >= 256;
    }
}