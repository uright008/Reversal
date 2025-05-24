package cn.stars.reversal.ui.atmoic.mainmenu.impl;

import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RoundedUtil;

import java.awt.*;

public class MiscGui extends AtomicGui {
    public MiscGui() {
        super("Misc", "h");
    }

    public MiscGui(String displayName) { super("Misc", displayName, "h"); }

    @Override
    public void drawIcon(float posX, float posY, int color) {
        atomic24.drawString(icon, posX, posY + 1, color);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(width / 2f - 150, height / 2f - 50, 300, 100, 3, Color.BLACK);
        }, 2, 2);

        RoundedUtil.drawRound(width / 2f - 150, height / 2f - 50, 300, 100, 3, new Color(20, 20, 20, 160));

        psm18.drawCenteredString("Nothing here!", width / 2f, height / 2f - 20, Color.WHITE.getRGB());
        psm18.drawCenteredString("这个界面是用来存储你开启的上一个界面的!", width / 2f, height / 2f - 10, Color.WHITE.getRGB());
        psm18.drawCenteredString("开启一个不能用上方按钮栏打开的界面后，即可在这里找到~", width / 2f, height / 2f, Color.WHITE.getRGB());
        psm18.drawCenteredString("=w=", width / 2f, height / 2f + 10, Color.WHITE.getRGB());
        psm18.drawCenteredString("(瘫软)", width / 2f, height / 2f + 20, Color.WHITE.getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
