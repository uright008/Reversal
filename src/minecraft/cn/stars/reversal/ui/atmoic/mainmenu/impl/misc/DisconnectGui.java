package cn.stars.reversal.ui.atmoic.mainmenu.impl.misc;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.MiscGui;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RenderUtils;
import cn.stars.reversal.util.render.RoundedUtil;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class DisconnectGui extends MiscGui {
    private final String reason;
    private final IChatComponent message;
    private List<String> multilineMessage;
    private int totalHeight;
    private TextButton reconnectButton, cancelButton;
    private TextButton[] buttons;

    public DisconnectGui(String reasonLocalizationKey, IChatComponent chatComp)
    {
        super("disconnected");
        this.reason = I18n.format(reasonLocalizationKey);
        this.message = chatComp;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        this.multilineMessage = Arrays.asList(regular16.autoReturn(this.message.getFormattedText(), this.width - 50, 100).split("\n"));
        this.totalHeight = (int) (this.multilineMessage.size() * regular16.height());
        reconnectButton = new TextButton(this.width / 2f - 100, this.height / 2f + this.totalHeight / 2f + 10, 200, 20, () -> AtomicMenu.switchGui(2), "返回主菜单", "", true, 1, 75, 5, 20);
        cancelButton = new TextButton(this.width / 2f - 100, this.height / 2f + this.totalHeight / 2f + 34, 200, 20, () -> {
            AtomicMenu.setMiscGui(new ConnectingGui(mc.getCurrentServerData()));
            AtomicMenu.switchGui(8);
        }, "重连", "", true, 1, 90, 5, 20);
        buttons = new TextButton[]{reconnectButton, cancelButton};
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
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float maxLength = Math.max(regular24Bold.width(this.reason), 300);

        if (this.multilineMessage != null) {
            for (String s : this.multilineMessage) {
                maxLength = Math.max(maxLength, regular16.width(s));
            }
        }

        float finalMaxLength = maxLength;

        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(this.width / 2f - finalMaxLength / 2 - 30, this.height / 2f - this.totalHeight / 2f - regular16.height() * 2 - 10, finalMaxLength + 60, 100 + 10 * multilineMessage.size(), 3, Color.BLACK);
        }, 2, 2);

        RoundedUtil.drawRound(this.width / 2f - maxLength / 2 - 30, this.height / 2f - this.totalHeight / 2f - regular16.height() * 2 - 10, maxLength + 60, 100 + 10 * multilineMessage.size(), 3, new Color(20, 20, 20, 160));

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        regular24Bold.drawCenteredString(this.reason, this.width / 2f, this.height / 2f - this.totalHeight / 2f - regular16.height() * 2 - 5, Color.WHITE.getRGB());
        int i = this.height / 2 - this.totalHeight / 2;

        if (this.multilineMessage != null) {
            for (String s : this.multilineMessage) {
                regular16.drawCenteredString(s, this.width / 2f, i, new Color(220,220,220,220).getRGB());
                maxLength = Math.max(maxLength, regular16.width(s));
                i += regular16.height();
            }
        }
    }
}
