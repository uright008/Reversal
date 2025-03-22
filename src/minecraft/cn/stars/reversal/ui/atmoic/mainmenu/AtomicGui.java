package cn.stars.reversal.ui.atmoic.mainmenu;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ConcurrentModificationException;
import java.util.List;

public class AtomicGui implements GameInstance {
    public String name;
    public String icon;
    protected ScaledResolution sr = new ScaledResolution(mc);
    protected int width;
    protected int height;
    protected List<GuiButton> buttonList = Lists.newArrayList();
    public GuiButton selectedButton;

    public final Animation hoverAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);

    public AtomicGui(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public void initGui() {
        buttonList.clear();
        sr = new ScaledResolution(mc);
        width = sr.getScaledWidth();
        height = sr.getScaledHeight();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (GuiButton guiButton : this.buttonList) {
            guiButton.drawButton(mc, mouseX, mouseY);
        }
    }

    public void updateScreen() {
        sr = new ScaledResolution(mc);
        width = sr.getScaledWidth();
        height = sr.getScaledHeight();
    }

    public void keyTyped(char typedChar, int keyCode) {}

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0)
        {
            try {
                for (GuiButton guiButton : this.buttonList) {

                    if (guiButton.mousePressed(mc, mouseX, mouseY)) {
                        this.selectedButton = guiButton;
                        guiButton.playPressSound(mc.getSoundHandler());
                        this.actionPerformed(guiButton);
                    }
                }
            } catch (ConcurrentModificationException ignored) {}
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.selectedButton != null && state == 0)
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {}

    public void onGuiClosed() {}

    public void handleMouseInput() {}

    public void actionPerformed(GuiButton button) {}

    public void confirmClicked(boolean result, int id) {}

    // Built-in
    public void drawIcon(int posX, int posY, int color) {}
}
