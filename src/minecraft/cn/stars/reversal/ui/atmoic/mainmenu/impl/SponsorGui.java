/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2024 Starlight, All rights reserved.
 */
package cn.stars.reversal.ui.atmoic.mainmenu.impl;

import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

public class SponsorGui extends AtomicGui {
    public GuiScreen parent;
    private TextButton exitButton;
    private TextButton[] buttons;

    public SponsorGui() {
        super("Sponsor", "g");
    }

    @Override
    public void drawIcon(int posX, int posY, int color) {
        atomic24.drawString(icon, posX + 1, posY + 0.5, color);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(50, 65, width - 100, height - 85, 3, Color.BLACK);

            RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
            FontManager.getRainbowParty(48).drawString("sponsor", 75, 35, Color.WHITE.getRGB());
        }, 2, 2);

        RoundedUtil.drawRound(50, 65, width - 100, height - 85, 3, new Color(20, 20, 20, 160));

        RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
        FontManager.getRainbowParty(48).drawString("sponsor", 75, 35, Color.WHITE.getRGB());

        psb20.drawString("Not implemented yet. (TwT)", 60, 80, new Color(220, 220, 220, 240).getRGB());

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.exitButton = new TextButton(width / 2f - 60, height - 60, 120, 35, () -> AtomicMenu.switchGui(0),
                "返回主菜单", "g", true, 12, 38, 11);
        buttons = new TextButton[]{exitButton};
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
    }
}
