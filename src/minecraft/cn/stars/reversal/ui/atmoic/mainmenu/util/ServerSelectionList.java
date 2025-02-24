package cn.stars.reversal.ui.atmoic.mainmenu.util;

import cn.stars.reversal.ui.atmoic.mainmenu.impl.MultiPlayerGui;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;

import java.util.Collections;
import java.util.List;

public class ServerSelectionList extends GuiListExtended
{
    private final MultiPlayerGui owner;
    private final List<ServerListEntryNormal> serverListInternet = Lists.newArrayList();
    private final List<ServerListEntryLanDetected> serverListLan = Lists.newArrayList();
    private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
    private int selectedSlotIndex = -1;

    public ServerSelectionList(MultiPlayerGui ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = ownerIn;
    }

    public GuiListExtended.IGuiListEntry getListEntry(int index)
    {
        if (index < this.serverListInternet.size())
        {
            return this.serverListInternet.get(index);
        }
        else
        {
            index = index - this.serverListInternet.size();

            if (index == 0)
            {
                return this.lanScanEntry;
            }
            else
            {
                --index;
                return this.serverListLan.get(index);
            }
        }
    }

    public int getSize()
    {
        return this.serverListInternet.size() + 1 + this.serverListLan.size();
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn)
    {
        if (selectedSlotIndexIn < 0) {
            owner.selectButton.setEnabled(false);
            owner.editButton.setEnabled(false);
            owner.deleteButton.setEnabled(false);
            this.selectedSlotIndex = -1;
        } else {
            owner.selectButton.setEnabled(true);
            owner.editButton.setEnabled(true);
            owner.deleteButton.setEnabled(true);
            this.selectedSlotIndex = selectedSlotIndexIn;
        }
    }

    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.selectedSlotIndex;
    }

    public int func_148193_k()
    {
        return this.selectedSlotIndex;
    }

    public void loadInternetServerList(ServerList p_148195_1_)
    {
        this.serverListInternet.clear();

        for (int i = 0; i < p_148195_1_.countServers(); ++i)
        {
            if (!owner.searchField.text.isEmpty()) {
                if (p_148195_1_.getServerData(i).serverName.toLowerCase().contains(owner.searchField.text.toLowerCase())) {
                    this.serverListInternet.add(new ServerListEntryNormal(this.owner, p_148195_1_.getServerData(i)));
                }
            } else {
                this.serverListInternet.add(new ServerListEntryNormal(this.owner, p_148195_1_.getServerData(i)));
            }
        }

        if (owner.reversed) {
            Collections.reverse(this.serverListInternet);
        }
    }

    public void loadLanServerList(List<LanServerDetector.LanServer> p_148194_1_)
    {
        this.serverListLan.clear();

        for (LanServerDetector.LanServer lanserverdetector$lanserver : p_148194_1_)
        {
            this.serverListLan.add(new ServerListEntryLanDetected(this.owner, lanserverdetector$lanserver));
        }

        if (owner.reversed) {
            Collections.reverse(this.serverListLan);
        }
    }

    protected int getScrollBarX()
    {
        return width - 55;
    }

    @Override
    public int getListWidth() {
        return this.width - 110;
    }
}
