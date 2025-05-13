package cn.stars.reversal.ui.atmoic.mainmenu.impl;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.engine.impl.ServerPinger;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.misc.AddServerGui;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.misc.ConnectingGui;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.misc.DirectConnectGui;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.misc.YesNoGui;
import cn.stars.reversal.ui.atmoic.mainmenu.util.ServerListEntryLanDetected;
import cn.stars.reversal.ui.atmoic.mainmenu.util.ServerListEntryLanScan;
import cn.stars.reversal.ui.atmoic.mainmenu.util.ServerListEntryNormal;
import cn.stars.reversal.ui.atmoic.mainmenu.util.ServerSelectionList;
import cn.stars.reversal.ui.atmoic.misc.component.TextButton;
import cn.stars.reversal.ui.atmoic.misc.component.TextField;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.resources.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiPlayerGui extends AtomicGui {
    private static final Logger logger = LogManager.getLogger();
    public final ServerPinger serverPinger = new ServerPinger();
    public ServerSelectionList serverListSelector;
    private ServerList savedServerList;
    public boolean deletingServer;
    private boolean addingServer;
    public boolean editingServer;
    private boolean directConnect;
    private String hoveringText;
    public ServerData selectedServer;
    private LanServerDetector.LanServerList lanServerList;
    private LanServerDetector.ThreadLanServerFind lanServerDetector;
    public TextButton editButton, deleteButton, selectButton, directButton, addButton, refreshButton, cancelButton, reverseButton;
    private TextButton[] buttons;
    GuiListExtended.IGuiListEntry guilistextended$iguilistentry;
    private boolean initialized;
    public TextField searchField;
    public boolean reversed;

    public MultiPlayerGui() {
        super("MultiPlayer", "multiplayer", "c");
    }

    @Override
    public void drawIcon(int posX, int posY, int color) {
        atomic24.drawString(icon, posX, posY + 0.5, color);
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);

        searchField = new TextField(width - 165, 20, GameInstance.regular16, new Color(30, 30, 30, 100), new Color(30,30,30,120));
        searchField.setSelectedLine(true);

        if (!this.initialized)
        {
            this.initialized = true;
            this.savedServerList = new ServerList(mc);
            this.savedServerList.loadServerList();
            this.lanServerList = new LanServerDetector.LanServerList();

            try
            {
                this.lanServerDetector = new LanServerDetector.ThreadLanServerFind(this.lanServerList);
                this.lanServerDetector.start();
            }
            catch (Exception exception)
            {
                logger.warn("Unable to start LAN server detection: {}", exception.getMessage());
            }

            this.serverListSelector = new ServerSelectionList(this, mc, this.width, this.height, 105, this.height - 70, 36);
            this.serverListSelector.loadInternetServerList(this.savedServerList);
        }
        else
        {
            this.serverListSelector.setDimensions(this.width, this.height, 105, this.height - 70);
        }

        this.createButtons();
    }

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        this.serverListSelector.handleMouseInput();
    }

    public void createButtons()
    {
        selectButton = new TextButton(this.width / 2.0 - 154, this.height - 70, 100, 20, this::connectToSelected, "连接服务器", "", true, 1, 25, 5, 20);

        directButton = new TextButton(this.width / 2.0 - 50, this.height - 70, 100, 20, () -> {
            this.directConnect = true;
            AtomicMenu.setMiscGui(new DirectConnectGui(this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
            AtomicMenu.switchGui(8);
        }, "直接连接", "", true, 1, 30, 5, 20);

        addButton = new TextButton(this.width / 2.0 + 4 + 50, this.height - 70, 100, 20, () -> {
            this.addingServer = true;
            AtomicMenu.setMiscGui(new AddServerGui(this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
            AtomicMenu.switchGui(8);
        }, "添加服务器", "", true, 1, 25, 5, 20);

        editButton = new TextButton(this.width / 2.0 - 154, this.height - 45, 70, 20, () -> {
            if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                this.editingServer = true;
                ServerData serverdata = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData();
                this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false);
                this.selectedServer.copyFrom(serverdata);
                AtomicMenu.setMiscGui(new AddServerGui(this.selectedServer));
                AtomicMenu.switchGui(8);
            }
        }, "编辑", "", true, 1, 25, 5, 20);

        deleteButton = new TextButton(this.width / 2.0 - 74, this.height - 45, 70, 20, () -> {
            if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                String s4 = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData().serverName;
                if (s4 != null)
                {
                    this.deletingServer = true;
                    String s = I18n.format("selectServer.deleteQuestion");
                    String s1 = "'" + s4 + "' " + I18n.format("selectServer.deleteWarning");
                    YesNoGui guiyesno = new YesNoGui(2, this.serverListSelector.getSelectedIndex(), s, s1);
                    AtomicMenu.setMiscGui(guiyesno);
                    AtomicMenu.switchGui(8);
                }
            }
        }, "删除", "", true, 1, 25, 5, 20);

        refreshButton = new TextButton(this.width / 2.0 + 4, this.height - 45, 70, 20, this::refreshServerList, "刷新", "", true, 1, 25, 5, 20);

        cancelButton = new TextButton(this.width / 2.0 + 4 + 80, this.height - 45, 70, 20, () -> AtomicMenu.switchGui(0), "返回", "", true, 1, 25, 5, 20);

        reverseButton = new TextButton(this.width - 90, 68, 36, 20, this::doReverse, "↑", "", true, 1, 13, 5, 20);

        selectButton.setEnabled(false);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        this.selectServer(this.serverListSelector.getSelectedIndex());
        buttons = new TextButton[] {selectButton, directButton, addButton, editButton, deleteButton, refreshButton, cancelButton, reverseButton};
    }

    @SneakyThrows
    public void doReverse() {
        reversed = !reversed;
        if (reversed) {
            reverseButton = new TextButton(this.width - 90, 68, 36, 20, this::doReverse, "↓", "", true, 1, 13, 5, 20);
        } else {
            reverseButton = new TextButton(this.width - 90, 68, 36, 20, this::doReverse, "↑", "", true, 1, 13, 5, 20);
        }
        this.serverListSelector.loadInternetServerList(this.savedServerList);
        buttons = new TextButton[] {selectButton, directButton, addButton, editButton, deleteButton, refreshButton, cancelButton, reverseButton};
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        guilistextended$iguilistentry = this.serverListSelector.getSelectedIndex() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedIndex());
        if (this.lanServerList.getWasUpdated())
        {
            List<LanServerDetector.LanServer> list = this.lanServerList.getLanServers();
            this.lanServerList.setWasNotUpdated();
            this.serverListSelector.loadLanServerList(list);
        }

        this.serverPinger.pingPendingNetworks();

        if (Display.wasResized()) {
            this.initialized = false;
            AtomicMenu.switchGui(2);
        }
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        if (this.lanServerDetector != null)
        {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }

        this.serverPinger.clearPendingNetworks();
    }

    private void refreshServerList()
    {
        this.initialized = false;
        AtomicMenu.switchGui(2);
    }

    @Override
    public void confirmClicked(boolean result, int id)
    {
        super.confirmClicked(result, id);
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelectedIndex() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedIndex());

        if (this.deletingServer)
        {
            this.deletingServer = false;

            if (result && guilistextended$iguilistentry instanceof ServerListEntryNormal)
            {
                this.savedServerList.removeServerData(this.serverListSelector.getSelectedIndex());
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.loadInternetServerList(this.savedServerList);
            }

            AtomicMenu.switchGui(2);
            mc.displayGuiScreen(Reversal.atomicMenu);
        }
        else if (this.directConnect)
        {
            this.directConnect = false;

            if (result)
            {
                connectToServer(this.selectedServer);
            }
            else
            {
                AtomicMenu.switchGui(2);
                mc.displayGuiScreen(Reversal.atomicMenu);
            }
        }
        else if (this.addingServer)
        {
            this.addingServer = false;

            if (result)
            {
                this.savedServerList.addServerData(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.loadInternetServerList(this.savedServerList);
            }

            AtomicMenu.switchGui(2);
            mc.displayGuiScreen(Reversal.atomicMenu);
        }
        else if (this.editingServer)
        {
            this.editingServer = false;

            if (result && guilistextended$iguilistentry instanceof ServerListEntryNormal)
            {
                ServerData serverdata = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData();
                serverdata.serverName = this.selectedServer.serverName;
                serverdata.serverIP = this.selectedServer.serverIP;
                serverdata.copyFrom(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.loadInternetServerList(this.savedServerList);
            }

            AtomicMenu.switchGui(2);
            mc.displayGuiScreen(Reversal.atomicMenu);
        } else {
            AtomicMenu.switchGui(2);
            mc.displayGuiScreen(Reversal.atomicMenu);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        int i = this.serverListSelector.getSelectedIndex();
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = i < 0 ? null : this.serverListSelector.getListEntry(i);
        searchField.keyTyped(typedChar, keyCode);
        this.serverListSelector.loadInternetServerList(this.savedServerList);

        if (keyCode == 63)
        {
            this.refreshServerList();
        }
        else
        {
            if (i >= 0)
            {
                if (keyCode == 200)
                {
                    if (GuiScreen.isShiftKeyDown())
                    {
                        if (i > 0 && guilistextended$iguilistentry instanceof ServerListEntryNormal)
                        {
                            this.savedServerList.swapServers(i, i - 1);
                            this.selectServer(this.serverListSelector.getSelectedIndex() - 1);
                            this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                            this.serverListSelector.loadInternetServerList(this.savedServerList);
                        }
                    }
                    else if (i > 0)
                    {
                        this.selectServer(this.serverListSelector.getSelectedIndex() - 1);
                        this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());

                        if (this.serverListSelector.getListEntry(this.serverListSelector.getSelectedIndex()) instanceof ServerListEntryLanScan)
                        {
                            if (this.serverListSelector.getSelectedIndex() > 0)
                            {
                                this.selectServer(this.serverListSelector.getSize() - 1);
                                this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                            }
                            else
                            {
                                this.selectServer(-1);
                            }
                        }
                    }
                    else
                    {
                        this.selectServer(-1);
                    }
                }
                else if (keyCode == 208)
                {
                    if (GuiScreen.isShiftKeyDown())
                    {
                        if (i < this.savedServerList.countServers() - 1)
                        {
                            this.savedServerList.swapServers(i, i + 1);
                            this.selectServer(i + 1);
                            this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                            this.serverListSelector.loadInternetServerList(this.savedServerList);
                        }
                    }
                    else if (i < this.serverListSelector.getSize())
                    {
                        this.selectServer(this.serverListSelector.getSelectedIndex() + 1);
                        this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());

                        if (this.serverListSelector.getListEntry(this.serverListSelector.getSelectedIndex()) instanceof ServerListEntryLanScan)
                        {
                            if (this.serverListSelector.getSelectedIndex() < this.serverListSelector.getSize() - 1)
                            {
                                this.selectServer(this.serverListSelector.getSize() + 1);
                                this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                            }
                            else
                            {
                                this.selectServer(-1);
                            }
                        }
                    }
                    else
                    {
                        this.selectServer(-1);
                    }
                }
                else if (keyCode != 28 && keyCode != 156)
                {
                    super.keyTyped(typedChar, keyCode);
                }
            }
            else
            {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.hoveringText = null;

        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(50, 100, width - 100, height - 120, 3, Color.BLACK);
            RoundedUtil.drawRound(50, 65, width - 100, 25, 3, Color.BLACK);
        }, 2, 2);

        RoundedUtil.drawRound(50, 100, width - 100, height - 120, 3, new Color(20, 20, 20, 160));
        RoundedUtil.drawRound(50, 65, width - 100, 25, 3, new Color(20, 20, 20, 160));

        atomic24.drawString("3", 55, 74, Color.WHITE.getRGB());
        searchField.draw(70, 68, mouseX, mouseY);

        AtomicMenu.POST_POSTPROCESSING_QUEUE.add(() -> atomic24.drawString("3", 55, 74, Color.WHITE.getRGB()));

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(50, 100, width - 100, height - 170);
        this.serverListSelector.setShowSelectionBox(false);
        this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.hoveringText != null)
        {
           ArrayList<String> arrayList = Lists.newArrayList(Splitter.on("\n").split(this.hoveringText));
           AtomicInteger h = new AtomicInteger();
           final float[] maxWidth = {0};
           arrayList.forEach(i -> {
               h.addAndGet(10);
               AtomicMenu.UI_BLOOM_RUNNABLES.add(() -> psm16.drawString(i, mouseX + 10, mouseY + h.get(), Color.WHITE.getRGB()));
               maxWidth[0] = Math.max(maxWidth[0], psm16.getStringWidth(i));
           });
           RenderUtil.roundedRectangle(mouseX + 5, mouseY + 5, maxWidth[0] + 10, h.get() + 5, 2, new Color(20, 20, 20, 160));
        }
    }

    public void connectToSelected()
    {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelectedIndex() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedIndex());

        if (guilistextended$iguilistentry instanceof ServerListEntryNormal)
        {
            connectToServer(((ServerListEntryNormal)guilistextended$iguilistentry).getServerData());
        }
        else if (guilistextended$iguilistentry instanceof ServerListEntryLanDetected)
        {
            LanServerDetector.LanServer lanserverdetector$lanserver = ((ServerListEntryLanDetected)guilistextended$iguilistentry).getLanServer();
            connectToServer(new ServerData(lanserverdetector$lanserver.getServerMotd(), lanserverdetector$lanserver.getServerIpPort(), true));
        }
    }

    public static void connectToServer(ServerData server)
    {
        AtomicMenu.setMiscGui(new ConnectingGui(server));
        AtomicMenu.switchGui(8);
    }

    public void selectServer(int index)
    {
        this.serverListSelector.setSelectedSlotIndex(index);
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = index < 0 ? null : this.serverListSelector.getListEntry(index);
        //    this.btnSelectServer.enabled = false;
        //    this.btnEditServer.enabled = false;
        //    this.btnDeleteServer.enabled = false;

        if (guilistextended$iguilistentry != null && !(guilistextended$iguilistentry instanceof ServerListEntryLanScan))
        {
            //    this.btnSelectServer.enabled = true;

            if (guilistextended$iguilistentry instanceof ServerListEntryNormal)
            {
                //    this.btnEditServer.enabled = true;
                //    this.btnDeleteServer.enabled = true;
            }
        }
    }

    public ServerPinger getServerPinger()
    {
        return this.serverPinger;
    }

    public void setHoveringText(String p_146793_1_)
    {
        this.hoveringText = p_146793_1_;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            for (TextButton menuButton : this.buttons) {
                if (RenderUtil.isHovered(menuButton.getX(), menuButton.getY(), menuButton.getWidth(), menuButton.getHeight(), mouseX, mouseY)) {
                    mc.getSoundHandler().playUISound("click");
                    menuButton.runAction();
                    break;
                }
            }
        }
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        this.serverListSelector.mouseReleased(mouseX, mouseY, state);
    }

    public ServerList getServerList()
    {
        return this.savedServerList;
    }

    public boolean func_175392_a(ServerListEntryNormal p_175392_1_, int p_175392_2_)
    {
        return p_175392_2_ > 0;
    }

    public boolean func_175394_b(ServerListEntryNormal p_175394_1_, int p_175394_2_)
    {
        return p_175394_2_ < this.savedServerList.countServers() - 1;
    }

    public void func_175391_a(ServerListEntryNormal p_175391_1_, int p_175391_2_, boolean p_175391_3_)
    {
        int i = p_175391_3_ ? 0 : p_175391_2_ - 1;
        this.savedServerList.swapServers(p_175391_2_, i);

        if (this.serverListSelector.getSelectedIndex() == p_175391_2_)
        {
            this.selectServer(i);
        }

        this.serverListSelector.loadInternetServerList(this.savedServerList);
    }

    public void func_175393_b(ServerListEntryNormal p_175393_1_, int p_175393_2_, boolean p_175393_3_)
    {
        int i = p_175393_3_ ? this.savedServerList.countServers() - 1 : p_175393_2_ + 1;
        this.savedServerList.swapServers(p_175393_2_, i);

        if (this.serverListSelector.getSelectedIndex() == p_175393_2_)
        {
            this.selectServer(i);
        }

        this.serverListSelector.loadInternetServerList(this.savedServerList);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        searchField.mouseDragged(mouseX, mouseY, clickedMouseButton);
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }
}
