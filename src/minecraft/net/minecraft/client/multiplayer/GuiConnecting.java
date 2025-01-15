package net.minecraft.client.multiplayer;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.atmoic.Atomic;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
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
    private Animation animation = new Animation(Easing.EASE_OUT_QUINT, 600);
    private TextButton cancelButton;

    public GuiConnecting(GuiScreen p_i1181_1_, Minecraft mcIn, ServerData p_i1181_3_)
    {
        this.mc = mcIn;
        this.previousGuiScreen = p_i1181_1_;
        ServerAddress serveraddress = ServerAddress.fromString(p_i1181_3_.serverIP);
        mcIn.loadWorld((WorldClient)null);
        mcIn.setServerData(p_i1181_3_);
        this.connect(serveraddress.getIP(), serveraddress.getPort());
    }

    public GuiConnecting(GuiScreen p_i1182_1_, Minecraft mcIn, String hostName, int port)
    {
        this.mc = mcIn;
        this.previousGuiScreen = p_i1182_1_;
        mcIn.loadWorld((WorldClient)null);
        this.connect(hostName, port);
    }

    private void connect(final String ip, final int port)
    {
        logger.info("Connecting to " + ip + ", " + port);
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

                    GuiConnecting.logger.error((String)"Couldn't connect to server", (Throwable)unknownhostexception);
                    GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")));
                }
                catch (Exception exception)
                {
                    if (GuiConnecting.this.cancel)
                    {
                        return;
                    }

                    GuiConnecting.logger.error((String)"Couldn't connect to server", (Throwable)exception);
                    String s = exception.toString();

                    if (inetaddress != null)
                    {
                        String s1 = inetaddress.toString() + ":" + port;
                        s = s.replaceAll(s1, "");
                    }

                    GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", s)));
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
        Atomic.registerAtomic("Connecting to: " + mc.getCurrentServerData().serverIP, "Server", 3000L);
        this.cancelButton = new TextButton(width / 2 - 100, height / 2 + 100, 200, 20, this::action, "取消", "", true, 6, 90, 5, 20);
        this.animation = new Animation(Easing.EASE_OUT_QUINT, 600);
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

        // blur
        RiseShaders.GAUSSIAN_BLUR_SHADER.update();
        RiseShaders.GAUSSIAN_BLUR_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_BLUR_RUNNABLES);

        // bloom
        RiseShaders.POST_BLOOM_SHADER.update();
        RiseShaders.POST_BLOOM_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_POST_BLOOM_RUNNABLES);

        GameInstance.clearRunnables();

        cancelButton.draw(mouseX, mouseY, partialTicks);

        RoundedUtil.drawRound(width / 2f - 225, 200, 450, height - 400, 4, new Color(30, 30, 30, 160));
        RoundedUtil.drawRound(width / 2f - 125, 10, 250, 40, 6, new Color(30, 30, 30, 160));

        GameInstance.NORMAL_BLUR_RUNNABLES.add(() -> {
            RoundedUtil.drawRound(width / 2f - 225, 200, 450, height - 400, 4, Color.BLACK);
            RoundedUtil.drawRound(width / 2f - 125, 10, 250, 40, 6, Color.BLACK);
        });
        RenderUtil.rect(width / 2f - 225, 220, 450, 0.5, new Color(220, 220, 220, 240));
        GameInstance.regular24Bold.drawCenteredString("连接服务器", width / 2f, 206, new Color(220, 220, 220, 240).getRGB());

        RenderUtil.image(new ResourceLocation("reversal/images/logo/curiosity.png"), width / 2f - 100,  height / 2f - 160, 200, 200);
        RenderUtils.drawLoadingCircle2(this.width / 2, this.height / 2 + 50, 6, new Color(220, 220, 220, 220));

        String ip = "Unknown";

        final ServerData serverData = mc.getCurrentServerData();
        if(serverData != null)
            ip = "IP: " + serverData.serverIP;

        FontManager.getSpecialIcon(80).drawString("b", width / 2f - 115, 17, new Color(220, 220, 220, 240).getRGB());
        GameInstance.regular24Bold.drawString("SERVER INFORMATION", width / 2f - 55, 18, new Color(220, 220, 220, 240).getRGB());
        GameInstance.regular20.drawString(ip, width / 2f - 55, 34, new Color(220, 220, 220, 240).getRGB());

        regular24Bold.drawCenteredString("正在连接至服务器...", this.width / 2, this.height / 2 + 70, new Color(220, 220, 220, 220).getRGB());

        regular16.drawString("Open Source PVP Client By Stars.", 4, height - 30, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString("https://www.github.com/RinoRika/Reversal", 4, height - 20, new Color(220, 220, 220, 240).getRGB());
        regular16.drawString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")), 4, height - 10, new Color(220, 220, 220, 240).getRGB());

        Atomic.INSTANCE.render(new ScaledResolution(mc));

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
