package cn.stars.reversal.ui.modern;


import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.render.RenderUtil;
import lombok.Getter;

public class MenuButton extends MenuComponent {

    private final Runnable runnable;
    public boolean isHovering;

    @Getter
    private final Animation animation = new Animation(Easing.EASE_OUT_QUINT, 500);
    @Getter
    private final Animation hoverAnimation = new Animation(Easing.EASE_OUT_SINE, 250);
    @Getter
    private final Animation curiosityAnimation = new Animation(Easing.EASE_OUT_SINE, 400);
    @Getter
    private final Animation curiosityBorderAnimation = new Animation(Easing.EASE_OUT_SINE, 400);
    @Getter
    private final Animation curiosityFontAnimation = new Animation(Easing.EASE_OUT_SINE, 400);

    public MenuButton(double x, double y, double width, double height, Runnable runnable) {
        super(x, y, width, height);
        this.runnable = runnable;
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        isHovering = RenderUtil.isHovered(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY);
        this.hoverAnimation.run(isHovering ? 120 : 45);
        this.curiosityAnimation.run(isHovering ? 250 : 200);
        this.curiosityBorderAnimation.run(isHovering ? 200 : 0);
        this.curiosityFontAnimation.run(isHovering ? 250 : 180);
    }

    public void runAction() {
        this.runnable.run();
    }
}
