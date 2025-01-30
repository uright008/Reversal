package cn.stars.reversal.music.ui.gui;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.music.ui.MusicPlayerScreen;
import cn.stars.reversal.util.animation.advanced.composed.CustomAnimation;
import cn.stars.reversal.util.animation.advanced.impl.SmoothStepAnimation;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Mouse;

/**
 * @author ChengFeng
 * @since 2024/8/13
 **/
@Getter
@Setter
public abstract class MusicPlayerGUI implements GameInstance {
    protected float posX, posY, width, height;
    protected boolean hovering, isBottom;
    public MusicPlayerGUI parent;

    public MusicPlayerGUI(MusicPlayerGUI parent) {
        this.parent = parent;
    }

    protected CustomAnimation scrollAnim = new CustomAnimation(SmoothStepAnimation.class, 100);
    public void handleScroll() {
        // Scroll
        int wheel = Mouse.getDWheel() * 420;
        if (wheel != 0) {
            scrollAnim.setStartPoint(scrollAnim.getOutput());
            if (wheel > 0) {
                scrollAnim.setEndPoint(scrollAnim.getEndPoint() + 30f);
            } else {
                scrollAnim.setEndPoint(scrollAnim.getEndPoint() - 30f);
            }
            if (scrollAnim.getEndPoint() > 0) scrollAnim.setEndPoint(0f);
            float maxScroll = height - (MusicPlayerScreen.height - MusicPlayerScreen.topWidth - MusicPlayerScreen.bottomWidth - 3f);
            if (-scrollAnim.getEndPoint() > maxScroll) {
                scrollAnim.setEndPoint(-maxScroll);
                isBottom = true;
            } else isBottom = false;
            scrollAnim.getAnimation().reset();
        }
    }

    public abstract boolean draw(float x, float y, int mouseX, int mouseY, float cx, float cy, float scale);

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void onGuiClosed() {}

    public void freeMemory() {

    }
}
