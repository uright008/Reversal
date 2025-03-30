package cn.stars.reversal.ui.atmoic.misc;


import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.render.RenderUtil;

import java.awt.*;

public class GUIBubble {
    public final Animation alphaAnimation;
    public final Animation radiusAnimation;
    public float x,y,radius;
    public int alpha;
    private boolean reversed;

    public GUIBubble(float x, float y, float radius, int alpha) {
        alphaAnimation = new Animation(Easing.EASE_OUT_EXPO, 250);
        radiusAnimation = new Animation(Easing.EASE_OUT_CUBIC, 600);
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.alpha = alpha;
    }

    public boolean shouldRemove() {
        return radiusAnimation.getValue() > radius && alphaAnimation.getDestinationValue() == 0 && alphaAnimation.isFinished();
    }

    public void render() {
        if (!reversed) {
            alphaAnimation.run(alpha);
            if (alphaAnimation.isFinished()) reversed = true;
        } else {
            alphaAnimation.run(0);
        }
        radiusAnimation.run(this.radius * 3);
        float rd = (float) radiusAnimation.getValue();
        RenderUtil.roundedRectangle(x - rd / 2f, y - rd / 2f, rd, rd, rd / 2f, new Color(250,250,250, (int) alphaAnimation.getValue()));
    }
}
