package cn.stars.reversal.ui.atmoic.mainmenu.menus;

import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.util.render.RenderUtil;

import java.awt.*;

public class MultiPlayerGui extends AtomicGui {
    private final MFont upperIcon = FontManager.getAtomic(24);
    public MultiPlayerGui() {
        super("MultiPlayer", "c");
    }

    @Override
    public void drawIcon(int posX, int posY) {
        upperIcon.drawString(icon, posX, posY, Color.WHITE.getRGB());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mc.fontRendererObj.drawCenteredStringWithShadow("没写", sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, Color.WHITE.getRGB());

        RenderUtil.drawParallelogram(50,50,50,50,5, true, Color.BLACK);
    }
}
