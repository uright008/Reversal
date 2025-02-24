package cn.stars.reversal.ui.modern.impl;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

import java.awt.*;
import java.io.IOException;

@NativeObfuscation
public class ModernViaMenu extends GuiScreen implements GuiYesNoCallback {
    private final GuiScreen parent;
    public SlotList list;

    MFont regular = FontManager.getRegular(20);
    MFont eaves = FontManager.getEaves(64);
    MFont regular2 = FontManager.getRegular(24);
    MFont regularBold = FontManager.getRegularBold(24);
    private final Animation fontAnimation = new Animation(Easing.EASE_OUT_SINE, 400);

    private TextButton back;
    private TextButton[] buttons;
    private String tipString = "";

    public ModernViaMenu(GuiScreen parent) {
        this.parent = parent;
    }

    @SneakyThrows
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        updatePostProcessing(true, partialTicks);

        back.draw(mouseX, mouseY, partialTicks);

        RoundedUtil.drawRound(width / 2f - 200, height / 2f - 240, 400, 400, 4, new Color(30, 30, 30, 200));
        RenderUtil.image(new ResourceLocation("reversal/images/logo/curiosity.png"), width / 2f - 190, height / 2f - 260, 100, 100);
        eaves.drawString("REVERSAL | ViaVersion", width / 2f - 110, height / 2f - 220, new Color(220, 220, 220, 240).getRGB());
        regular.drawCenteredString("一个可以使你加入高于1.8.x版本服务器的强大工具",
                width / 2f, height / 2f - 180, new Color(220, 220, 220, 240).getRGB());

        RenderUtil.rect(width / 2f - 200, height / 2f - 150, 400, 0.5, new Color(220, 220, 220, 240));

        GameInstance.NORMAL_BLUR_RUNNABLES.add(() -> RoundedUtil.drawRound(width / 2f - 200, height / 2f - 240, 400, 400, 4, Color.BLACK));

        regular.drawCenteredString(tipString, width / 2f, height / 2f + 110, new Color(220, 220, 220, 240).getRGB());

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        list.drawScreen(mouseX, mouseY, partialTicks);
        RenderUtil.scissor(width / 2f - 200, height / 2f - 148, 400, 308);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        updatePostProcessing(false, partialTicks);
    }

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        list = new SlotList(mc, width, height, centerY - 170, centerY + 150);

        this.back = new TextButton(centerX - 60, centerY + 180, 120, 35, () -> mc.displayGuiScreen(parent),
                "返回主菜单", "g", true, 12, 38, 11);

        this.buttons = new TextButton[] { this.back };
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
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

    public void handleMouseInput() throws IOException {
        list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        list.actionPerformed(button);
    }

    class SlotList extends GuiSlot {

        public SlotList(Minecraft mc, int width, int height, int top, int bottom) {
            super(mc, width, height, top + 30, bottom, 18);
        }

        @Override
        protected int getSize() {
            return ViaLoadingBase.PROTOCOLS.size();
        }

        @Override
        protected void elementClicked(int i, boolean b, int i1, int i2) {
            final ProtocolVersion protocolVersion = ViaLoadingBase.PROTOCOLS.get(i);
            ViaLoadingBase.getInstance().reload(protocolVersion);
        }

        @Override
        protected boolean shouldRenderOverlay() {
            return false;
        }

        @Override
        protected boolean shouldRenderContainer() {
            return false;
        }

        @Override
        protected boolean isSelected(int i) {
            return false;
        }

        @Override
        protected void drawBackground() {
        }

        @Override
        protected void drawSlot(int i, int i1, int i2, int i3, int i4, int i5) {
            if (ViaLoadingBase.PROTOCOLS.indexOf(ViaLoadingBase.getInstance().getTargetVersion()) == i) {
                regularBold.drawCenteredString(">  " + ViaLoadingBase.PROTOCOLS.get(i).getName() + "  <", width / 2f, i2 + 2, Color.WHITE.getRGB());
            } else {
                regular2.drawCenteredString(ViaLoadingBase.PROTOCOLS.get(i).getName(), width / 2f, i2 + 2, Color.GRAY.getRGB());
            }
        }
    }
}
