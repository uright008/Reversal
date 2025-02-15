package cn.stars.reversal.ui.atmoic.mainmenu.menus;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.ui.atmoic.island.Atomic;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.shader.RiseShaders;
import cn.stars.reversal.util.shader.base.ShaderRenderType;
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
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class SinglePlayerGui extends AtomicGui {
    private final MFont upperIcon = FontManager.getAtomic(24);
    private static final Logger logger = LogManager.getLogger();
    private final DateFormat field_146633_h = new SimpleDateFormat();
    protected String screenTitle = "Select world";
    private boolean field_146634_i;
    private int selectedIndex;
    private java.util.List<SaveFormatComparator> field_146639_s;
    private SinglePlayerGui.List availableWorlds;
    private String field_146637_u;
    private String field_146636_v;
    private String[] field_146635_w = new String[4];
    private boolean confirmingDelete;
    private TextButton renameButton, deleteButton, selectButton, recreateButton, createButton;
    private TextButton[] buttons;

    public SinglePlayerGui() {
        super("SinglePlayer", "b");
    }

    @Override
    public void drawIcon(int posX, int posY) {
        upperIcon.drawString(icon, posX, posY, Color.WHITE.getRGB());
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (TextButton menuButton : this.buttons) {
                if (RenderUtil.isHovered(menuButton.getX(), menuButton.getY(), menuButton.getWidth(), menuButton.getHeight(), mouseX, mouseY)) {
                    mc.getSoundHandler().playButtonPress();
                    menuButton.runAction();
                    break;
                }
            }
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.addWorldSelectionButtons();
        this.screenTitle = I18n.format("selectWorld.title");

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
        this.field_146639_s = isaveformat.getSaveList();
        Collections.sort(this.field_146639_s);
        this.selectedIndex = -1;
    }

    protected String func_146621_a(int p_146621_1_)
    {
        return this.field_146639_s.get(p_146621_1_).getFileName();
    }

    protected String func_146614_d(int p_146614_1_)
    {
        String s = this.field_146639_s.get(p_146614_1_).getDisplayName();

        if (StringUtils.isEmpty(s))
        {
            s = I18n.format("selectWorld.world") + " " + (p_146614_1_ + 1);
        }

        return s;
    }

    public void addWorldSelectionButtons()
    {
        selectButton = new TextButton(this.width / 2 - 154, this.height - 52, 150, 20, () -> this.func_146615_e(this.selectedIndex), "进入世界", "", true, 1, 50, 5, 20);

        createButton = new TextButton(this.width / 2 + 4, this.height - 52, 150, 20, () -> mc.displayGuiScreen(new GuiCreateWorld(Reversal.atomicMenu)), "创建新世界", "", true, 1, 45, 5, 20);

        renameButton = new TextButton(this.width / 2 - 154, this.height - 28, 72, 20, () -> {
            if (selectedIndex != -1) {
                mc.displayGuiScreen(new GuiRenameWorld(Reversal.atomicMenu, this.func_146621_a(this.selectedIndex)));
            }
        }, "重命名", "", true, 1, 20, 5, 20);

        deleteButton = new TextButton(this.width / 2 - 76, this.height - 28, 72, 20, () -> {
            if (selectedIndex != -1) {
                String s = this.func_146614_d(this.selectedIndex);
                if (s != null) {
                    this.confirmingDelete = true;
                    GuiYesNo guiyesno = makeDeleteWorldYesNo(Reversal.atomicMenu, s, this.selectedIndex);
                    mc.displayGuiScreen(guiyesno);
                }
            }
        }, "删除", "", true, 1, 25, 5, 20);

        recreateButton = new TextButton(this.width / 2 + 4, this.height - 28, 72, 20, () -> {
            if (selectedIndex != -1) {
                GuiCreateWorld guicreateworld = new GuiCreateWorld(Reversal.atomicMenu);
                ISaveHandler isavehandler = mc.getSaveLoader().getSaveLoader(this.func_146621_a(this.selectedIndex), false);
                WorldInfo worldinfo = isavehandler.loadWorldInfo();
                isavehandler.flush();
                guicreateworld.recreateFromExistingWorld(worldinfo);
                mc.displayGuiScreen(guicreateworld);
            }
        }, "重建", "", true, 1, 25, 5, 20);

    //    cancelButton = new TextButton(this.width / 2 + 82, this.height - 28, 72, 20, () -> mc.displayGuiScreen(this.parentScreen), "取消", "", true, 1, 25, 5, 20);

        buttons = new TextButton[] {selectButton, createButton, recreateButton, renameButton, deleteButton};
    }

    public void func_146615_e(int p_146615_1_)
    {
        if (p_146615_1_ != -1) {
            mc.displayGuiScreen(null);

            if (!this.field_146634_i) {
                this.field_146634_i = true;
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
    }

    public void confirmClicked(boolean result, int id)
    {
        if (this.confirmingDelete)
        {
            this.confirmingDelete = false;

            if (result)
            {
                ISaveFormat isaveformat = this.mc.getSaveLoader();
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

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        RoundedUtil.drawRound(width / 2f - 225, 10, 450, height - 15, 4, new Color(30, 30, 30, 160));
        RenderUtil.rect(width / 2f - 225, 30, 450, 0.5, new Color(220, 220, 220, 240));
        GameInstance.MODERN_BLUR_RUNNABLES.add(() -> RoundedUtil.drawRound(width / 2f - 225, 10, 450, height - 15, 4, Color.BLACK));
        GameInstance.regular24Bold.drawCenteredString("单人游戏", width / 2f, 16, new Color(220, 220, 220, 240).getRGB());

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(width / 2f - 225, 31, 450, height - 95);
        this.availableWorlds.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

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

    class List extends GuiSlot
    {
        public List(Minecraft mcIn)
        {
            super(mcIn, SinglePlayerGui.this.width, SinglePlayerGui.this.height, 32, SinglePlayerGui.this.height - 64, 36);
        }

        protected int getSize()
        {
            return SinglePlayerGui.this.field_146639_s.size();
        }

        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
        {
            SinglePlayerGui.this.selectedIndex = slotIndex;
            boolean flag = SinglePlayerGui.this.selectedIndex >= 0 && SinglePlayerGui.this.selectedIndex < this.getSize();

            if (isDoubleClick && flag)
            {
                SinglePlayerGui.this.func_146615_e(slotIndex);
            }
        }

        protected boolean isSelected(int slotIndex)
        {
            return slotIndex == SinglePlayerGui.this.selectedIndex;
        }

        protected int getContentHeight()
        {
            return SinglePlayerGui.this.field_146639_s.size() * 36;
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

        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn)
        {
            SaveFormatComparator saveformatcomparator = SinglePlayerGui.this.field_146639_s.get(entryID);
            String s = saveformatcomparator.getDisplayName();

            if (StringUtils.isEmpty(s))
            {
                s = SinglePlayerGui.this.field_146637_u + " " + (entryID + 1);
            }

            String s1 = saveformatcomparator.getFileName();
            s1 = s1 + " (" + SinglePlayerGui.this.field_146633_h.format(new Date(saveformatcomparator.getLastTimePlayed()));
            s1 = s1 + ")";
            String s2 = "";

            if (saveformatcomparator.requiresConversion())
            {
                s2 = SinglePlayerGui.this.field_146636_v + " " + s2;
            }
            else
            {
                s2 = SinglePlayerGui.this.field_146635_w[saveformatcomparator.getEnumGameType().getID()];

                if (saveformatcomparator.isHardcoreModeEnabled())
                {
                    s2 = EnumChatFormatting.DARK_RED + I18n.format("极限", new Object[0]) + EnumChatFormatting.RESET;
                }

                if (saveformatcomparator.getCheatsEnabled())
                {
                    s2 = s2 + ", " + I18n.format("外桂");
                }
            }

            GameInstance.regular20Bold.drawString(s, p_180791_2_ + 2, p_180791_3_ + 3, new Color(220, 220, 220, 240).getRGB());
            GameInstance.regular16.drawString(s1, p_180791_2_ + 2, p_180791_3_ + 15, new Color(120,120,120, 240).getRGB());
            GameInstance.regular16.drawString(s2, p_180791_2_ + 2, p_180791_3_ + 15 + 10, new Color(120,120,120, 240).getRGB());
        }
    }
}
