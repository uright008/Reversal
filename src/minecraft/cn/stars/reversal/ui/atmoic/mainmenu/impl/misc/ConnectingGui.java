package cn.stars.reversal.ui.atmoic.mainmenu.impl.misc;

import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.MiscGui;
import cn.stars.reversal.ui.atmoic.mainmenu.util.ServerConnector;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RenderUtils;
import cn.stars.reversal.util.render.RoundedUtil;
import net.minecraft.client.multiplayer.ServerData;

import java.awt.*;

public class ConnectingGui extends MiscGui {
    private int enterID = 0;
    private final ServerConnector serverConnector = new ServerConnector();
    private TextButton cancelButton, reconnectButton;

    public ConnectingGui(ServerData serverData)
    {
        mc.loadWorld(null);
        mc.setServerData(serverData);
        serverConnector.reset();
        serverConnector.connect(serverData);
    }

    @Override
    public void initGui() {
        super.initGui();
        enterID++;
        if (enterID > 1) {
            this.reconnectButton = new TextButton(width / 2f - 100, height / 2f, 200, 20, () -> {
                AtomicMenu.setMiscGui(new ConnectingGui(mc.getCurrentServerData()));
                AtomicMenu.switchGui(8);
            }, "重进", "", true, 6, 90, 5, 20);
        }
        this.cancelButton = new TextButton(width / 2f - 100, height / 2f + 25, 200, 20, () -> {
            serverConnector.stop();
            AtomicMenu.switchGui(2);
        }, "取消", "", true, 6, 90, 5, 20);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (enterID > 1) {
                if (RenderUtil.isHovered(reconnectButton.x, reconnectButton.y, reconnectButton.width, reconnectButton.height, mouseX, mouseY)){
                    mc.getSoundHandler().playButtonPress();
                    reconnectButton.runAction();
                }
            }
            if (RenderUtil.isHovered(cancelButton.x, cancelButton.y, cancelButton.width, cancelButton.height, mouseX, mouseY)){
                mc.getSoundHandler().playButtonPress();
                cancelButton.runAction();
            }
        }
    }

    @Override
    public void updateScreen()
    {
        serverConnector.update();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(width / 2f - 200, height / 2f - 60, 400, 120, 3, Color.BLACK);
            RenderUtils.drawLoadingCircle2(this.width / 2f, this.height / 2f + 15, 10, Color.WHITE);

            RenderUtils.drawLoadingCircle3(57,47,5, Color.WHITE);
            RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
            FontManager.getRainbowParty(48).drawString("connecting", 75, 35, Color.WHITE.getRGB());
        }, 2, 2);

        RoundedUtil.drawRound(width / 2f - 200, height / 2f - 60, 400, 120, 3, new Color(20, 20, 20, 160));
        RenderUtils.drawLoadingCircle3(57,47,5, Color.WHITE);
        RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
        FontManager.getRainbowParty(48).drawString("connecting", 75, 35, Color.WHITE.getRGB());

        String ip = "Unknown";

        final ServerData serverData = mc.getCurrentServerData();
        if(serverData != null)
            ip = "IP: " + serverData.serverIP;

        if (enterID > 1) {
            regular24Bold.drawCenteredString("上一次连接的服务器:", this.width / 2f, this.height / 2f - 40, Color.WHITE.getRGB());
            psm18.drawCenteredString(ip, this.width / 2f, this.height / 2f - 25, new Color(220,220,220,220).getRGB());
        } else {
            RenderUtils.drawLoadingCircle2(this.width / 2f, this.height / 2f - 35, 10, Color.WHITE);
            regular24Bold.drawCenteredString("正在连接至服务器...", this.width / 2f, this.height / 2f - 10, Color.WHITE.getRGB());
            psm18.drawCenteredString(ip, this.width / 2f, this.height / 2f + 5, new Color(220, 220, 220, 220).getRGB());
        }

        cancelButton.draw(mouseX, mouseY, partialTicks);
        if (enterID > 1) reconnectButton.draw(mouseX, mouseY, partialTicks);
    }

}
