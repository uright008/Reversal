package cn.stars.reversal.ui.atmoic.mainmenu.impl;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.ui.modern.TextField;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RenderUtils;
import cn.stars.reversal.util.render.RoundedUtil;
import lombok.SneakyThrows;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class SinglePlayerGui extends AtomicGui {
    private static final Logger logger = LogManager.getLogger();
    private final DateFormat field_146633_h = new SimpleDateFormat();
    private int selectedIndex;
    private java.util.List<SaveFormatComparator> saveList;
    private SinglePlayerGui.List availableWorlds;
    private String field_146637_u;
    private String field_146636_v;
    private String[] field_146635_w = new String[4];
    private boolean confirmingDelete;
    private TextButton renameButton, deleteButton, selectButton, recreateButton, createButton, cancelButton, reverseButton;
    private TextButton[] buttons;
    private TextField searchField;
    private boolean reversed = false;

    public SinglePlayerGui() {
        super("SinglePlayer", "b");
    }

    @Override
    public void drawIcon(int posX, int posY, int color) {
        atomic24.drawString(icon, posX, posY + 0.5, color);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
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
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.addWorldSelectionButtons();
        searchField = new TextField(width - 165, 20, GameInstance.regular16, new Color(30, 30, 30, 100), new Color(30,30,30,120));
        searchField.setSelectedLine(true);

        try
        {
            this.loadLevelList();
        }
        catch (AnvilConverterException anvilconverterexception)
        {
            logger.error("Couldn't load level list", anvilconverterexception);
            mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", anvilconverterexception.getMessage()));
            return;
        }

        this.field_146637_u = I18n.format("selectWorld.world");
        this.field_146636_v = I18n.format("selectWorld.conversion");
        this.field_146635_w[WorldSettings.GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival");
        this.field_146635_w[WorldSettings.GameType.CREATIVE.getID()] = I18n.format("gameMode.creative");
        this.field_146635_w[WorldSettings.GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure");
        this.field_146635_w[WorldSettings.GameType.SPECTATOR.getID()] = I18n.format("gameMode.spectator");
        this.availableWorlds = new SinglePlayerGui.List(mc);
        this.availableWorlds.registerScrollButtons(4, 5);
    }

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        this.availableWorlds.handleMouseInput();
    }

    private void loadLevelList() throws AnvilConverterException
    {
        ISaveFormat isaveformat = mc.getSaveLoader();
        if (searchField.text.isEmpty()) {
            this.saveList = isaveformat.getSaveList();
            Collections.sort(this.saveList);
        } else {
            ArrayList<SaveFormatComparator> list = new ArrayList<>();
            for (SaveFormatComparator saveFormatComparator : isaveformat.getSaveList()) {
                if (saveFormatComparator.getDisplayName().toLowerCase().contains(searchField.text.toLowerCase())) {
                    list.add(saveFormatComparator);
                }
            }
            this.saveList = list;
        }
        if (this.reversed) Collections.reverse(this.saveList);
        this.selectedIndex = -1;
    }

    protected String func_146621_a(int p_146621_1_)
    {
        return this.saveList.get(p_146621_1_).getFileName();
    }

    protected String func_146614_d(int p_146614_1_)
    {
        String s = this.saveList.get(p_146614_1_).getDisplayName();

        if (StringUtils.isEmpty(s))
        {
            s = I18n.format("selectWorld.world") + " " + (p_146614_1_ + 1);
        }

        return s;
    }

    public void addWorldSelectionButtons()
    {
        selectButton = new TextButton(this.width / 2 - 154, this.height - 70, 150, 20, () -> this.loadWorld(this.selectedIndex), "进入世界", "", true, 1, 50, 5, 20);

        createButton = new TextButton(this.width / 2 + 4, this.height - 70, 150, 20, () -> mc.displayGuiScreen(new GuiCreateWorld(Reversal.atomicMenu)), "创建新世界", "", true, 1, 45, 5, 20);

        renameButton = new TextButton(this.width / 2 - 154, this.height - 45, 72, 20, () -> {
            if (selectedIndex != -1) {
                mc.displayGuiScreen(new GuiRenameWorld(Reversal.atomicMenu, this.func_146621_a(this.selectedIndex)));
            }
        }, "重命名", "", true, 1, 20, 5, 20);

        deleteButton = new TextButton(this.width / 2 - 76, this.height - 45, 72, 20, () -> {
            if (selectedIndex != -1) {
                String s = this.func_146614_d(this.selectedIndex);
                if (s != null) {
                    this.confirmingDelete = true;
                    GuiYesNo guiyesno = makeDeleteWorldYesNo(Reversal.atomicMenu, s, this.selectedIndex);
                    mc.displayGuiScreen(guiyesno);
                }
            }
        }, "删除", "", true, 1, 25, 5, 20);

        recreateButton = new TextButton(this.width / 2 + 4, this.height - 45, 72, 20, () -> {
            if (selectedIndex != -1) {
                GuiCreateWorld guicreateworld = new GuiCreateWorld(Reversal.atomicMenu);
                ISaveHandler isavehandler = mc.getSaveLoader().getSaveLoader(this.func_146621_a(this.selectedIndex), false);
                WorldInfo worldinfo = isavehandler.loadWorldInfo();
                isavehandler.flush();
                guicreateworld.recreateFromExistingWorld(worldinfo);
                mc.displayGuiScreen(guicreateworld);
            }
        }, "重建", "", true, 1, 25, 5, 20);

        cancelButton = new TextButton(this.width / 2 + 82, this.height - 45, 72, 20, () -> AtomicMenu.switchGui(0), "返回", "", true, 1, 25, 5, 20);

        reverseButton = new TextButton(this.width - 90, 68, 36, 20, this::doReverse, "↑", "", true, 1, 13, 5, 20);

        selectButton.setEnabled(false);
        renameButton.setEnabled(false);
        deleteButton.setEnabled(false);
        recreateButton.setEnabled(false);

        buttons = new TextButton[] {selectButton, createButton, recreateButton, renameButton, deleteButton, cancelButton, reverseButton};
    }

    @SneakyThrows
    public void doReverse() {
        reversed = !reversed;
        if (reversed) {
            reverseButton = new TextButton(this.width - 90, 68, 36, 20, this::doReverse, "↓", "", true, 1, 13, 5, 20);
        } else {
            reverseButton = new TextButton(this.width - 90, 68, 36, 20, this::doReverse, "↑", "", true, 1, 13, 5, 20);
        }
        buttons = new TextButton[] {selectButton, createButton, recreateButton, renameButton, deleteButton, cancelButton, reverseButton};
        this.loadLevelList();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(50, 100, width - 100, height - 120, 3, Color.BLACK);
            RoundedUtil.drawRound(50, 65, width - 100, 25, 3, Color.BLACK);

            RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
            RenderUtils.drawLoadingCircle3(57,47,5, Color.WHITE);
            FontManager.getRainbowParty(48).drawString("singleplayer", 75, 35, Color.WHITE.getRGB());
        }, 2, 2);

        RoundedUtil.drawRound(50, 100, width - 100, height - 120, 3, new Color(20, 20, 20, 160));
        RoundedUtil.drawRound(50, 65, width - 100, 25, 3, new Color(20, 20, 20, 160));

        atomic24.drawString("3", 55, 74, Color.WHITE.getRGB());
        searchField.draw(70, 68, mouseX, mouseY);

        RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
        RenderUtils.drawLoadingCircle3(57,47,5, Color.WHITE);
        FontManager.getRainbowParty(48).drawString("singleplayer", 75, 35, Color.WHITE.getRGB());

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(50, 100, width - 100, height - 170);
        this.availableWorlds.setShowSelectionBox(false);
        this.availableWorlds.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static GuiYesNo makeDeleteWorldYesNo(GuiYesNoCallback selectWorld, String name, int id)
    {
        String s = I18n.format("selectWorld.deleteQuestion");
        String s1 = "'" + name + "' " + I18n.format("selectWorld.deleteWarning");
        String s2 = I18n.format("selectWorld.deleteButton");
        String s3 = I18n.format("gui.cancel");
        return new GuiYesNo(selectWorld, s, s1, s2, s3, id);
    }

    class List extends GuiSlot implements GameInstance
    {
        public List(Minecraft mcIn)
        {
            super(mcIn, SinglePlayerGui.this.width, SinglePlayerGui.this.height, 105, SinglePlayerGui.this.height - 70, 36);
        }

        protected int getSize()
        {
            return SinglePlayerGui.this.saveList.size();
        }

        @Override
        public int getListWidth() {
            return this.width - 110;
        }

        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
            SinglePlayerGui.this.selectedIndex = slotIndex;
            boolean flag = SinglePlayerGui.this.selectedIndex >= 0 && SinglePlayerGui.this.selectedIndex < this.getSize();

            SinglePlayerGui.this.selectButton.setEnabled(flag);
            SinglePlayerGui.this.renameButton.setEnabled(flag);
            SinglePlayerGui.this.deleteButton.setEnabled(flag);
            SinglePlayerGui.this.recreateButton.setEnabled(flag);

            if (isDoubleClick && flag)
            {
                SinglePlayerGui.this.loadWorld(slotIndex);
            }
        }

        protected boolean isSelected(int slotIndex)
        {
            return slotIndex == SinglePlayerGui.this.selectedIndex;
        }

        protected int getContentHeight()
        {
            return SinglePlayerGui.this.saveList.size() * 36;
        }

        protected void drawBackground()
        {
        }

        @Override
        protected boolean shouldRenderOverlay() {
            return false;
        }

        @Override
        protected boolean shouldRenderContainer() {
            return false;
        }

        @SneakyThrows
        protected void drawSlot(int entryID, int x, int y, int height, int mouseXIn, int mouseYIn) {
            SaveFormatComparator saveformatcomparator = SinglePlayerGui.this.saveList.get(entryID);
            String s = saveformatcomparator.getDisplayName();

            if (StringUtils.isEmpty(s)) {
                s = SinglePlayerGui.this.field_146637_u + " " + (entryID + 1);
            }

            String s1 = saveformatcomparator.getFileName();
            s1 = s1 + " (" + SinglePlayerGui.this.field_146633_h.format(new Date(saveformatcomparator.getLastTimePlayed()));
            s1 = s1 + ")";
            String s2 = "";

            if (saveformatcomparator.requiresConversion()) {
                s2 = SinglePlayerGui.this.field_146636_v + " " + s2;
            } else {
                s2 = SinglePlayerGui.this.field_146635_w[saveformatcomparator.getEnumGameType().getID()];

                if (saveformatcomparator.isHardcoreModeEnabled()) {
                    s2 = EnumChatFormatting.DARK_RED + I18n.format("极限", new Object[0]) + EnumChatFormatting.RESET;
                }

                if (saveformatcomparator.getCheatsEnabled()) {
                    s2 = s2 + ", " + I18n.format("外桂");
                }
            }

            SinglePlayerGui.this.saveList.get(entryID).getHoverAnimation().run(RenderUtil.isHovered(x - 2, y - 2 , getListWidth(), slotHeight, mouseXIn, mouseYIn) ? 100 : 0);
            SinglePlayerGui.this.saveList.get(entryID).getDeleteAnimation().run(RenderUtil.isHovered(x - 25 + getListWidth() + 1, y - 10 + slotHeight / 2f, 16, 16, mouseXIn, mouseYIn) ? 255 : 155);
            SinglePlayerGui.this.saveList.get(entryID).getRecreateAnimation().run(RenderUtil.isHovered(x - 50 + getListWidth(), y - 10 + slotHeight / 2f, 16, 16, mouseXIn, mouseYIn) ? 255 : 155);
            SinglePlayerGui.this.saveList.get(entryID).getRenameAnimation().run(RenderUtil.isHovered(x - 75 + getListWidth() + 1, y - 10 + slotHeight / 2f, 16, 16, mouseXIn, mouseYIn) ? 255 : 155);

            if (Mouse.isButtonDown(0)) {
                if (RenderUtil.isHovered(x - 25 + getListWidth() + 1, y - 10 + slotHeight / 2f, 16, 16, mouseXIn, mouseYIn) && saveList.get(entryID).getDeleteAnimation().getValue() > 200) {
                    if (func_146614_d(entryID) != null) {
                        confirmingDelete = true;
                        GuiYesNo guiyesno = makeDeleteWorldYesNo(Reversal.atomicMenu, s, entryID);
                        GameInstance.mc.displayGuiScreen(guiyesno);
                    }
                }
                if (RenderUtil.isHovered(x - 50 + getListWidth(), y - 10 + slotHeight / 2f, 16, 16, mouseXIn, mouseYIn) && saveList.get(entryID).getRecreateAnimation().getValue() > 200) {
                    GuiCreateWorld guicreateworld = new GuiCreateWorld(Reversal.atomicMenu);
                    ISaveHandler isavehandler = GameInstance.mc.getSaveLoader().getSaveLoader(func_146621_a(entryID), false);
                    WorldInfo worldinfo = isavehandler.loadWorldInfo();
                    isavehandler.flush();
                    guicreateworld.recreateFromExistingWorld(worldinfo);
                    GameInstance.mc.displayGuiScreen(guicreateworld);
                }
                if (RenderUtil.isHovered(x - 75 + getListWidth() + 1, y - 10 + slotHeight / 2f, 16, 16, mouseXIn, mouseYIn) && saveList.get(entryID).getRenameAnimation().getValue() > 200) {
                    GameInstance.mc.displayGuiScreen(new GuiRenameWorld(Reversal.atomicMenu, func_146621_a(entryID)));
                }
            }

            RenderUtil.roundedRectangle(x - 2, y - 2, getListWidth(), slotHeight, 2, new Color(20, 20, 20, (int) SinglePlayerGui.this.saveList.get(entryID).getHoverAnimation().getValue()));
            if (isSelected(entryID)) {
                SinglePlayerGui.this.saveList.get(entryID).getSelectAnimation().run(150);
            } else {
                SinglePlayerGui.this.saveList.get(entryID).getSelectAnimation().run(0);
            }
            RenderUtil.roundedRectangle(x - 2, y - 2, getListWidth(), slotHeight, 2, new Color(20, 20, 20, (int) SinglePlayerGui.this.saveList.get(entryID).getSelectAnimation().getValue()));

            regular20Bold.drawString(s, x + 2, y + 3, Color.WHITE.getRGB());

            regular16.drawString(s1, x + 2, y + 15, new Color(120, 120, 120, 250).getRGB());
            regular16.drawString(s2, x + 2, y + 15 + 10, new Color(120, 120, 120, 250).getRGB());

            atomic24.drawString("A", x + 20 + Math.max(Math.max(regular16.width(s1), regular16.width(s2)), regular20Bold.width(s)), y + 12, new Color(255,255,255, (int)(saveList.get(entryID).getSelectAnimation().getValue() * 1.6)).getRGB());

            atomic24.drawString("B", x - 20 + getListWidth(), y - 5 + slotHeight / 2f, new Color(255,255,255, (int)(saveList.get(entryID).getDeleteAnimation().getValue())).getRGB());
            atomic24.drawString("D", x - 46 + getListWidth(), y - 5 + slotHeight / 2f, new Color(255,255,255, (int)(saveList.get(entryID).getRecreateAnimation().getValue())).getRGB());
            atomic24.drawString("C", x - 70 + getListWidth(), y - 5 + slotHeight / 2f, new Color(255,255,255, (int)(saveList.get(entryID).getRenameAnimation().getValue())).getRGB());
        }

        @Override
        protected int getScrollBarX()
        {
            return width - 55;
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        searchField.mouseDragged(mouseX, mouseY, clickedMouseButton);
    }

    @SneakyThrows
    @Override
    public void keyTyped(char typedChar, int keyCode) {
        searchField.keyTyped(typedChar, keyCode);
        this.loadLevelList();
    }

    public void loadWorld(int p_146615_1_)
    {
        if (p_146615_1_ != -1) {
            mc.displayGuiScreen(null);

            String s = this.func_146621_a(p_146615_1_);

            if (s == null) {
                s = "World" + p_146615_1_;
            }

            String s1 = this.func_146614_d(p_146615_1_);

            if (s1 == null) {
                s1 = "World" + p_146615_1_;
            }

            if (mc.getSaveLoader().canLoadWorld(s)) {
                mc.launchIntegratedServer(s, s1, null);
            }
        }
    }

    public void confirmClicked(boolean result, int id)
    {
        if (this.confirmingDelete)
        {
            this.confirmingDelete = false;

            if (result)
            {
                ISaveFormat isaveformat = mc.getSaveLoader();
                isaveformat.flushCache();
                isaveformat.deleteWorldDirectory(this.func_146621_a(id));

                try
                {
                    this.loadLevelList();
                }
                catch (AnvilConverterException anvilconverterexception)
                {
                    logger.error("Couldn't load level list", anvilconverterexception);
                }
            }

            mc.displayGuiScreen(Reversal.atomicMenu);
        }
    }
}
