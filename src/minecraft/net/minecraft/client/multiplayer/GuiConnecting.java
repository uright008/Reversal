package net.minecraft.client.multiplayer;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.atmoic.island.Atomic;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.misc.DisconnectGui;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.ui.notification.NotificationManager;
import cn.stars.reversal.ui.notification.NotificationType;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RenderUtils;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.shader.RiseShaders;
import cn.stars.reversal.util.shader.base.ShaderRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cn.stars.reversal.GameInstance.*;
import static cn.stars.reversal.GameInstance.UI_BLOOM_RUNNABLES;

public class GuiConnecting extends GuiScreen
{
    private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();
    private NetworkManager networkManager;
    private boolean cancel;
    private final GuiScreen previousGuiScreen;
    private TextButton cancelButton;

    public GuiConnecting(GuiScreen p_i1181_1_, Minecraft mcIn, ServerData p_i1181_3_)
    {
        this.mc = mcIn;
        this.previousGuiScreen = p_i1181_1_;
        ServerAddress serveraddress = ServerAddress.fromString(p_i1181_3_.serverIP);
        mcIn.loadWorld(null);
        mcIn.setServerData(p_i1181_3_);
        this.connect(serveraddress.getIP(), serveraddress.getPort());
    }

    public GuiConnecting(GuiScreen p_i1182_1_, Minecraft mcIn, String hostName, int port)
    {
        this.mc = mcIn;
        this.previousGuiScreen = p_i1182_1_;
        mcIn.loadWorld(null);
        this.connect(hostName, port);
    }

    private void connect(final String ip, final int port)
    {
        logger.info("Connecting to {}, {}", ip, port);
        (new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet())
        {
            public void run()
            {
                InetAddress inetaddress = null;

                try
                {
                    if (GuiConnecting.this.cancel)
                    {
                        return;
                    }

                    inetaddress = InetAddress.getByName(ip);
                    GuiConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(inetaddress, port, GuiConnecting.this.mc.gameSettings.isUsingNativeTransport());
                    GuiConnecting.this.networkManager.setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.this.mc, GuiConnecting.this.previousGuiScreen));
                    GuiConnecting.this.networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
                    GuiConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiConnecting.this.mc.getSession().getProfile()));
                }
                catch (UnknownHostException unknownhostexception)
                {
                    if (GuiConnecting.this.cancel)
                    {
                        return;
                    }

                    logger.error("Couldn't connect to server", unknownhostexception);
                    mc.addScheduledTask(() -> {
                        AtomicMenu.setMiscGui(new DisconnectGui("connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")));
                        AtomicMenu.switchGui(8);
                        mc.displayGuiScreen(Reversal.atomicMenu);
                    });
                }
                catch (Exception exception)
                {
                    if (GuiConnecting.this.cancel)
                    {
                        return;
                    }

                    logger.error("Couldn't connect to server", exception);
                    String s = exception.toString();

                    if (inetaddress != null)
                    {
                        String s1 = inetaddress + ":" + port;
                        s = s.replaceAll(s1, "");
                    }

                    mc.addScheduledTask(() -> {
                        AtomicMenu.setMiscGui(new DisconnectGui("connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")));
                        AtomicMenu.switchGui(8);
                        mc.displayGuiScreen(Reversal.atomicMenu);
                    });
                }
            }
        }).start();
    }

    public void updateScreen()
    {
        if (this.networkManager != null)
        {
            if (this.networkManager.isChannelOpen())
            {
                this.networkManager.processReceivedPackets();
            }
            else
            {
                this.networkManager.checkDisconnected();
            }
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        GameInstance.clearRunnables();
        Reversal.notificationManager.registerNotification("Connecting to: " + mc.getCurrentServerData().serverIP, "Server", 3000L, NotificationType.NOTIFICATION);
        this.cancelButton = new TextButton(width / 2f - 100, height / 2f + 100, 200, 20, this::action, "取消", "", true, 6, 90, 5, 20);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    public void action() {
        this.cancel = true;

        if (this.networkManager != null) {
            this.networkManager.closeChannel(new ChatComponentText("Aborted"));
        }

        this.mc.displayGuiScreen(this.previousGuiScreen);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            if (RenderUtil.isHovered(cancelButton.x, cancelButton.y, cancelButton.width, cancelButton.height, mouseX, mouseY)){
                mc.getSoundHandler().playButtonPress();
                cancelButton.runAction();
            }
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.cancel = true;

            if (this.networkManager != null)
            {
                this.networkManager.closeChannel(new ChatComponentText("Aborted"));
            }

            this.mc.displayGuiScreen(this.previousGuiScreen);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        ModuleInstance.getPostProcessing().drawElementWithBlur(() -> RenderUtil.rect(0,0,width,height, new Color(0,0,0, 255)), 2, 2);
        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(width / 2f - 220, height / 2 - 52, 441, 107, 3, Color.BLACK);
            RenderUtils.drawLoadingCircle2(this.width / 2f, this.height / 2f + 15, 6, Color.WHITE);
        }, 2, 2);

        RoundedUtil.drawRound(width / 2f - 220, height / 2 - 52, 441, 107, 3, new Color(20, 20, 20, 160));
        RenderUtil.rect(width / 2f - 220, height / 2 - 32, 441, 0.5, new Color(220, 220, 220, 240));
        GameInstance.regular24Bold.drawCenteredString("连接服务器", width / 2f, height / 2f - 47, new Color(220, 220, 220, 240).getRGB());

        RenderUtil.image(new ResourceLocation("reversal/images/logo/curiosity.png"), width / 2f - 100,  height / 2f - 260, 200, 200);
        RenderUtils.drawLoadingCircle2(this.width / 2f, this.height / 2f + 15, 6, new Color(220, 220, 220, 220));

        String ip = "Unknown";

        final ServerData serverData = mc.getCurrentServerData();
        if(serverData != null)
            ip = "IP: " + serverData.serverIP;

        regular24Bold.drawCenteredString("正在连接至服务器...", this.width / 2f, this.height / 2f - 17, new Color(220, 220, 220, 220).getRGB());
        psm18.drawCenteredString(ip, this.width / 2f, this.height / 2f + 40, new Color(220, 220, 220, 220).getRGB());

        cancelButton.draw(mouseX, mouseY, partialTicks);

        NotificationManager.onRender2D();
        Atomic.INSTANCE.render(new ScaledResolution(mc));

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
