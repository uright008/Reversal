package cn.stars.reversal.font;

public abstract class MFont {
    public abstract int drawString(String text, double x, double y, int color, boolean dropShadow);

    public abstract int drawString(final String text, final double x, final double y, final int color);

    public abstract int drawStringWithShadow(final String text, final double x, final double y, final int color);

    public abstract float width(String text);

    public float getWidth(String text) { return width(text); }

    public float getStringWidth(String text) { return width(text); }

    public abstract float drawCenteredString(final String text, final double x, final double y, final int color);

    public abstract float drawRightString(final String text, final double x, final double y, final int color);

    public abstract String trimStringToWidth(String text, float width, boolean reverse, boolean more);

    public abstract String autoReturn(String text, float returnWidth, int maxReturns);

    public abstract int autoReturnCount(String text, float returnWidth, int maxReturns);

    public abstract float height();

    public float getHeight() { return height(); }
}
