package cn.stars.reversal.engine.impl;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.misc.DisconnectGui;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerConnector implements GameInstance {
    private boolean cancel;
    private NetworkManager networkManager;

    public void reset() {
        this.cancel = false;
        this.networkManager = null;
    }

    public void connect(ServerData serverData) {
        this.cancel = false;
        Reversal.threadPoolExecutor.execute(createConnectorThread(serverData));
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
        Thread connectorThread = new Thread("Server Connector")
        {
            public void run()
            {
                ServerAddress serveraddress = ServerAddress.fromString(serverData.serverIP);
                String ip = serveraddress.getIP();
                int port = serveraddress.getPort();

                Reversal.getLogger().info("连接至 {}, {}", ip, port);

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

                    Reversal.getLogger().error("无法连接至服务器", unknownhostexception);
                    mc.addScheduledTask(() -> {
                        AtomicMenu.setMiscGui(new DisconnectGui("connect.failed", new ChatComponentTranslation("disconnect.genericReason", "未知的主机")));
                        AtomicMenu.switchGui(8);
                        mc.displayGuiScreen(Reversal.atomicMenu);
                    });
                }
                catch (Exception exception) {
                    if (cancel) {
                        this.interrupt();
                        return;
                    }

                    Reversal.getLogger().error("无法连接至服务器", exception);
                    mc.addScheduledTask(() -> {
                        AtomicMenu.setMiscGui(new DisconnectGui("connect.failed", new ChatComponentTranslation("disconnect.genericReason", exception.toString())));
                        AtomicMenu.switchGui(8);
                        mc.displayGuiScreen(Reversal.atomicMenu);
                    });
                }
            }
        };
        connectorThread.setPriority(10);
        return connectorThread;
    }
}
