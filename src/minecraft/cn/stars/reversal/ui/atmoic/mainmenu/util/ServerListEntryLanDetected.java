package cn.stars.reversal.ui.atmoic.mainmenu.util;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.MultiPlayerGui;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.resources.I18n;

import java.awt.*;

public class ServerListEntryLanDetected implements GuiListExtended.IGuiListEntry
{
    private final MultiPlayerGui owner;
    protected final Minecraft mc;
    protected final LanServerDetector.LanServer field_148291_b;
    private long field_148290_d = 0L;
    private final Animation hoverAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation selectAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);

    protected ServerListEntryLanDetected(MultiPlayerGui p_i45046_1_, LanServerDetector.LanServer p_i45046_2_)
    {
        this.owner = p_i45046_1_;
        this.field_148291_b = p_i45046_2_;
        this.mc = Minecraft.getMinecraft();
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
    {

        hoverAnimation.run(RenderUtil.isHovered(x - 2, y - 2 , listWidth, slotHeight + 4, mouseX, mouseY) ? 100 : 0);
        RenderUtil.roundedRectangle(x - 2, y - 2, listWidth, slotHeight + 4, 2, new Color(20, 20, 20, (int) hoverAnimation.getValue()));
        if (owner.serverListSelector.isSelected(slotIndex)) {
            selectAnimation.run(150);
        } else {
            selectAnimation.run(0);
        }
        RenderUtil.roundedRectangle(x - 2, y - 2, listWidth, slotHeight + 4, 2, new Color(20, 20, 20, (int) selectAnimation.getValue()));

        GameInstance.regular20Bold.drawString(I18n.format("lanServer.title"), x + 32 + 3, y + 1, Color.WHITE.getRGB());
        GameInstance.regular16.drawString(this.field_148291_b.getServerMotd(), x + 32 + 3, y + 12, new Color(220, 220, 220, 250).getRGB());

        if (this.mc.gameSettings.hideServerAddress)
        {
            GameInstance.regular16.drawString(I18n.format("selectServer.hiddenAddress"), x + 32 + 3, y + 12 + 11, new Color(220, 220, 220, 250).getRGB());
        }
        else
        {
            GameInstance.regular16.drawString(this.field_148291_b.getServerIpPort(), x + 32 + 3, y + 12 + 11, new Color(220, 220, 220, 250).getRGB());
        }
    }

    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
    {
        this.owner.selectServer(slotIndex);

        if (Minecraft.getSystemTime() - this.field_148290_d < 250L)
        {
            this.owner.connectToSelected();
        }

        this.field_148290_d = Minecraft.getSystemTime();
        return false;
    }

    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
    {
    }

    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
    {
    }

    public LanServerDetector.LanServer getLanServer()
    {
        return this.field_148291_b;
    }
}
