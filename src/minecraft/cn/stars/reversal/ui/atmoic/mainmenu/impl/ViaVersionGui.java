package cn.stars.reversal.ui.atmoic.mainmenu.impl;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.ui.modern.TextField;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RenderUtils;
import cn.stars.reversal.util.render.RoundedUtil;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.ProtocolVersionAnimation;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class ViaVersionGui extends AtomicGui {
    private java.util.List<ProtocolVersion> protocols;
    private ViaVersionGui.List viaList;
    private TextButton reverseButton, exitButton;
    private TextButton[] buttons;
    private TextField searchField;
    private boolean reversed = false;

    public ViaVersionGui() {
        super("ViaVersion", "i");
    }

    @Override
    public void drawIcon(int posX, int posY, int color) {
        atomic24.drawString(icon, posX + 2, posY + 1, color);
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
        this.exitButton = new TextButton(width / 2f - 60, height - 60, 120, 35, () -> AtomicMenu.switchGui(0),
                "返回主菜单", "g", true, 12, 38, 11);
        this.reverseButton = new TextButton(this.width - 90, 68, 36, 20, this::doReverse, "↑", "", true, 1, 13, 5, 20);
        this.buttons = new TextButton[]{this.exitButton, this.reverseButton};

        searchField = new TextField(width - 165, 20, GameInstance.regular16, new Color(30, 30, 30, 100), new Color(30,30,30,120));
        searchField.setSelectedLine(true);

        loadList();

        this.viaList = new ViaVersionGui.List(mc);
        this.viaList.registerScrollButtons(9, 10);
    }

    private void loadList()
    {
        if (ViaLoadingBase.PROTOCOL_ANIMATION.isEmpty()) {
            for (int i = 0; i < ViaLoadingBase.PROTOCOLS.size(); i++) {
                ViaLoadingBase.PROTOCOL_ANIMATION.put(ViaLoadingBase.PROTOCOLS.get(i), new ProtocolVersionAnimation());
            }
        }
        if (searchField.text.isEmpty()) {
            ArrayList<ProtocolVersion> list = new ArrayList<>(ViaLoadingBase.PROTOCOLS);
            if (this.reversed) Collections.reverse(list);
            this.protocols = list;
        } else {
            ArrayList<ProtocolVersion> list = new ArrayList<>();
            for (ProtocolVersion protocol : ViaLoadingBase.PROTOCOLS) {
                if (protocol.getName().toLowerCase().contains(searchField.text.toLowerCase())) {
                    list.add(protocol);
                }
            }
            if (this.reversed) Collections.reverse(list);
            this.protocols = list;
        }

    }

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        this.viaList.handleMouseInput();
    }

    @SneakyThrows
    public void doReverse() {
        reversed = !reversed;
        if (reversed) {
            reverseButton = new TextButton(this.width - 90, 68, 36, 20, this::doReverse, "↓", "", true, 1, 13, 5, 20);
        } else {
            reverseButton = new TextButton(this.width - 90, 68, 36, 20, this::doReverse, "↑", "", true, 1, 13, 5, 20);
        }
        this.buttons = new TextButton[]{this.exitButton, this.reverseButton};
        loadList();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(50, 100, width - 100, height - 120, 3, Color.BLACK);
            RoundedUtil.drawRound(50, 65, width - 100, 25, 3, Color.BLACK);

            RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
            RenderUtils.drawLoadingCircle3(57,47,5, Color.WHITE);
            FontManager.getRainbowParty(48).drawString("via version", 75, 35, Color.WHITE.getRGB());
        }, 2, 2);

        RoundedUtil.drawRound(50, 100, width - 100, height - 120, 3, new Color(20, 20, 20, 160));
        RoundedUtil.drawRound(50, 65, width - 100, 25, 3, new Color(20, 20, 20, 160));

        atomic24.drawString("3", 55, 74, Color.WHITE.getRGB());
        searchField.draw(70, 68, mouseX, mouseY);

        RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
        RenderUtils.drawLoadingCircle3(57,47,5, Color.WHITE);
        FontManager.getRainbowParty(48).drawString("via version", 75, 35, Color.WHITE.getRGB());

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(50, 100, width - 100, height - 160);
        this.viaList.setShowSelectionBox(false);
        this.viaList.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class List extends GuiSlot
    {
        public List(Minecraft mcIn)
        {
            super(mcIn, ViaVersionGui.this.width, ViaVersionGui.this.height, 105, ViaVersionGui.this.height - 80, 26);
        }

        protected int getSize()
        {
            return ViaVersionGui.this.protocols.size();
        }

        @Override
        public int getListWidth() {
            return this.width - 110;
        }

        @Override
        protected void elementClicked(int i, boolean b, int i1, int i2) {
            final ProtocolVersion protocolVersion = ViaLoadingBase.PROTOCOLS.get(i);
            ViaLoadingBase.getInstance().reload(protocolVersion);
        }

        protected boolean isSelected(int slotIndex)
        {
            return false;
        }

        protected int getContentHeight()
        {
            return ViaVersionGui.this.protocols.size() * 26;
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
        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
            ProtocolVersionAnimation pva = ViaLoadingBase.PROTOCOL_ANIMATION.get(ViaVersionGui.this.protocols.get(entryID));
            pva.getHoverAnimation().run(RenderUtil.isHovered(p_180791_2_ - 2, p_180791_3_ - 2 , getListWidth(), slotHeight, mouseXIn, mouseYIn) ? 100 : 0);

            RenderUtil.roundedRectangle(p_180791_2_ - 2, p_180791_3_ - 3, getListWidth(), slotHeight - 2, 2, new Color(20, 20, 20, (int) pva.getHoverAnimation().getValue()));

            if (protocols.indexOf(ViaLoadingBase.getInstance().getTargetVersion()) == entryID) {
                pva.getSelectAnimation().run(150);
            } else {
                pva.getSelectAnimation().run(0);
            }

            RenderUtil.roundedRectangle(p_180791_2_ - 2, p_180791_3_ - 3, getListWidth(), slotHeight - 2, 2, new Color(20, 20, 20, (int) pva.getSelectAnimation().getValue()));

            GameInstance.psm24.drawCenteredString(protocols.get(entryID).getName(), width / 2f, p_180791_3_ + 5, Color.WHITE.getRGB());
            if (protocols.get(entryID).getName().equalsIgnoreCase("1.8.x"))
                GameInstance.psm16.drawString("(native version)", p_180791_2_ + getListWidth() - 60, p_180791_3_ + 7, new Color(120, 120, 120, 250).getRGB());
            else if (protocols.get(entryID).getName().equalsIgnoreCase("1.21.4"))
                GameInstance.psm16.drawString("(latest version)", p_180791_2_ + getListWidth() - 60, p_180791_3_ + 7, new Color(120, 120, 120, 250).getRGB());

            GameInstance.atomic24.drawString("A", width / 2f + GameInstance.psm24.width(protocols.get(entryID).getName()) / 2f + 20, p_180791_3_ + p_180791_4_ / 2f - atomic24.height() / 2f, new Color(250,250,250, (int)(pva.getSelectAnimation().getValue() * 1.6)).getRGB());
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
        this.loadList();
    }
}
