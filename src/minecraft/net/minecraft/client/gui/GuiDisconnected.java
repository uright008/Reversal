package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.ui.atmoic.island.Atomic;
import cn.stars.reversal.ui.notification.NotificationManager;
import cn.stars.reversal.ui.atmoic.misc.component.TextButton;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

import static cn.stars.reversal.GameInstance.*;

public class GuiDisconnected extends GuiScreen
{
    private String reason;
    private IChatComponent message;
    private List<String> multilineMessage;
    private int field_175353_i;
    private TextButton reconnectButton, cancelButton;
    private TextButton[] buttons;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp)
    {
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = chatComp;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    public void initGui()
    {
        this.buttonList.clear();
        this.multilineMessage = Arrays.asList(regular16.autoReturn(this.message.getFormattedText(), this.width - 50, 100).split("\n"));
        this.field_175353_i = (int) (this.multilineMessage.size() * regular16.height());
        reconnectButton = new TextButton(this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2, 200, 20, () -> this.mc.displayGuiScreen(Reversal.atomicMenu), "返回主菜单", "", true, 1, 75, 5, 20);
        cancelButton = new TextButton(this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + 25, 200, 20, () -> this.mc.displayGuiScreen(new GuiConnecting(Reversal.atomicMenu, mc, mc.getCurrentServerData())), "重连", "", true, 1, 90, 5, 20);
        buttons = new TextButton[]{reconnectButton, cancelButton};
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
    }

    @Override
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

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        try {
            float maxLength = Math.max(regular24Bold.width(this.reason), 300);
            this.drawDefaultBackground();

            GameInstance.clearRunnables();

            if (this.multilineMessage != null) {
                for (String s : this.multilineMessage) {
                    maxLength = Math.max(maxLength, regular16.width(s));
                }
            }

            float finalMaxLength = maxLength;
            ModuleInstance.getPostProcessing().drawElementWithBlur(() -> RenderUtil.rect(0,0,width,height, new Color(0,0,0, 255)), 2, 2);
            ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
                RoundedUtil.drawRound(this.width / 2f - finalMaxLength / 2 - 30, this.height / 2f - this.field_175353_i / 2f - regular16.height() * 2 - 10, finalMaxLength + 60, 100 + 10 * multilineMessage.size(), 3, Color.BLACK);
            }, 2, 2);

            RoundedUtil.drawRound(this.width / 2f - maxLength / 2 - 30, this.height / 2f - this.field_175353_i / 2f - regular16.height() * 2 - 10, maxLength + 60, 100 + 10 * multilineMessage.size(), 3, new Color(20, 20, 20, 160));
            RenderUtil.rect(this.width / 2f - maxLength / 2 - 30, this.height / 2f - this.field_175353_i / 2f - regular16.height() * 2 + 10, maxLength + 60, 0.5, new Color(220, 220, 220, 240));

            for (TextButton button : buttons) {
                button.draw(mouseX, mouseY, partialTicks);
            }

            regular24Bold.drawCenteredString(this.reason, this.width / 2f, this.height / 2f - this.field_175353_i / 2f - regular16.height() * 2 - 5, new Color(220, 220, 220, 240).getRGB());
            int i = this.height / 2 - this.field_175353_i / 2;

            if (this.multilineMessage != null) {
                for (String s : this.multilineMessage) {
                    regular16.drawCenteredString(s, this.width / 2f, i, new Color(220, 220, 220, 240).getRGB());
                    maxLength = Math.max(maxLength, regular16.width(s));
                    i += regular16.height();
                }
            }

            Atomic.INSTANCE.render(new ScaledResolution(mc));

            NotificationManager.onRender2D();

            UI_BLOOM_RUNNABLES.forEach(Runnable::run);
            UI_BLOOM_RUNNABLES.clear();

            super.drawScreen(mouseX, mouseY, partialTicks);
        } catch (StackOverflowError error) {

        }
    }
}
