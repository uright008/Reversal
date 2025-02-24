package cn.stars.reversal.ui.atmoic.mainmenu.util;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.util.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

import java.awt.*;

public class ServerListEntryLanScan implements GuiListExtended.IGuiListEntry
{
    private final Minecraft mc = Minecraft.getMinecraft();

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
    {
        int i = y + slotHeight / 2 - this.mc.fontRendererObj.FONT_HEIGHT / 2 + 2;
        GameInstance.psm18.drawString("Lan Server detecting...", 70, i,  Color.WHITE.getRGB());

        RenderUtils.drawLoadingCircle2(60, i + 3, 4, Color.WHITE);
    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
    {
    }

    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
    {
        return false;
    }

    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
    {
    }
}
