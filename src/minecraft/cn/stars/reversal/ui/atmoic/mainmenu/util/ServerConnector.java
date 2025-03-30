package cn.stars.reversal.ui.atmoic.mainmenu.util;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.misc.DisconnectGui;
import cn.stars.reversal.util.ReversalLogger;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerConnector implements GameInstance {
    private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
    private boolean cancel;
    private NetworkManager networkManager;

    public void reset() {
        this.cancel = false;
        this.networkManager = null;
    }

    public void connect(ServerData serverData) {
        this.cancel = false;
        Reversal.threadPoolExecutor.submit(this.createConnectorThread(serverData));
    }

    public void update() {
        if (this.networkManager != null) {
            if (this.networkManager.isChannelOpen()) {
                this.networkManager.processReceivedPackets();
            }
            else {
                this.networkManager.checkDisconnected();
            }
        }
    }

    public void stop() {
        this.cancel = true;
        if (this.networkManager != null) {
            this.networkManager.closeChannel(new ChatComponentText("Aborted"));
        }
    }
    
    public Thread createConnectorThread(ServerData serverData)
    {
        return new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet())
        {
            public void run()
            {
                ServerAddress serveraddress = ServerAddress.fromString(serverData.serverIP);
                String ip = serveraddress.getIP();
                int port = serveraddress.getPort();

                Reversal.getLogger().info("Connecting to {}, {}", ip, port);

                InetAddress inetaddress;

                try {
                    if (cancel) {
                        this.interrupt();
                        return;
                    }

                    inetaddress = InetAddress.getByName(ip);
                    networkManager = NetworkManager.createNetworkManagerAndConnect(inetaddress, port, mc.gameSettings.isUsingNativeTransport());
                    networkManager.setNetHandler(new NetHandlerLoginClient(networkManager, mc, Reversal.atomicMenu));
                    networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
                    networkManager.sendPacket(new C00PacketLoginStart(mc.getSession().getProfile()));
                }
                catch (UnknownHostException unknownhostexception) {
                    if (cancel) {
                        this.interrupt();
                        return;
                    }

                    Reversal.getLogger().error("Couldn't connect to server", unknownhostexception);
                    mc.addScheduledTask(() -> {
                        AtomicMenu.setMiscGui(new DisconnectGui("connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")));
                        AtomicMenu.switchGui(8);
                        mc.displayGuiScreen(Reversal.atomicMenu);
                    });
                }
                catch (Exception exception) {
                    if (cancel) {
                        this.interrupt();
                        return;
                    }

                    Reversal.getLogger().error("Couldn't connect to server", exception);
                    mc.addScheduledTask(() -> {
                        AtomicMenu.setMiscGui(new DisconnectGui("connect.failed", new ChatComponentTranslation("disconnect.genericReason", exception.toString())));
                        AtomicMenu.switchGui(8);
                        mc.displayGuiScreen(Reversal.atomicMenu);
                    });
                }
            }
        };
    }
}
