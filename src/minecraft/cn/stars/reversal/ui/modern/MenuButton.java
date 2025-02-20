package cn.stars.reversal.ui.modern;


import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.render.RenderUtil;
import lombok.Getter;
import lombok.Setter;

public class MenuButton extends MenuComponent {

    private final Runnable runnable;
    public boolean isHovering;
    @Setter
    public boolean enabled = true;

    @Getter
    private final Animation curiosityAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    @Getter
    private final Animation curiosityBorderAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    @Getter
    private final Animation curiosityFontAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);

    public MenuButton(double x, double y, double width, double height, Runnable runnable) {
        super(x, y, width, height);
        this.runnable = runnable;
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        isHovering = RenderUtil.isHovered(this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY) && enabled;
        this.curiosityAnimation.run(isHovering ? 250 : 200);
        this.curiosityBorderAnimation.run(isHovering ? 200 : 0);
        this.curiosityFontAnimation.run(isHovering ? 250 : 180);
    }

    public void runAction() {
        this.runnable.run();
    }
}
