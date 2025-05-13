package cn.stars.reversal.engine.impl;

import cn.stars.reversal.Reversal;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ServerPinger
{
    private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
    private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

    public void ping(ServerData server) {
        Reversal.threadPoolExecutor.execute(createPingThread(server));
    }

    public void compatibilityPing(ServerData server) {
        Reversal.threadPoolExecutor.execute(createCompatibilityPingThread(server));
    }

    public Thread createPingThread(final ServerData server)
    {
        Thread pingThread = new Thread("Server Pinger") {
            public void run() {
                try {
                    ServerAddress serveraddress = ServerAddress.fromString(server.serverIP);
                    final NetworkManager networkmanager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
                    pingDestinations.add(networkmanager);
                    server.serverMOTD = "连接中...";
                    server.pingToServer = -1L;
                    server.playerList = null;
                    Reversal.getLogger().info("连接至 {}, {}", serveraddress.getIP(), serveraddress.getPort());
                    networkmanager.setNetHandler(new INetHandlerStatusClient() {
                        private boolean field_147403_d = false;
                        private boolean field_183009_e = false;
                        private long field_175092_e = 0L;

                        public void handleServerInfo(S00PacketServerInfo packetIn) {
                            if (this.field_183009_e) {
                                networkmanager.closeChannel(new ChatComponentText("Received unrequested status"));
                            } else {
                                this.field_183009_e = true;
                                ServerStatusResponse serverstatusresponse = packetIn.getResponse();

                                if (serverstatusresponse.getServerDescription() != null) {
                                    server.serverMOTD = serverstatusresponse.getServerDescription().getFormattedText();
                                } else {
                                    server.serverMOTD = "";
                                }

                                if (serverstatusresponse.getProtocolVersionInfo() != null) {
                                    server.gameVersion = serverstatusresponse.getProtocolVersionInfo().getName();
                                    server.version = serverstatusresponse.getProtocolVersionInfo().getProtocol();
                                } else {
                                    server.gameVersion = "旧版";
                                    server.version = 0;
                                }

                                if (serverstatusresponse.getPlayerCountData() != null) {
                                    server.populationInfo = EnumChatFormatting.GRAY + "" + serverstatusresponse.getPlayerCountData().getOnlinePlayerCount() + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + serverstatusresponse.getPlayerCountData().getMaxPlayers();

                                    if (ArrayUtils.isNotEmpty(serverstatusresponse.getPlayerCountData().getPlayers())) {
                                        StringBuilder stringbuilder = new StringBuilder();

                                        for (GameProfile gameprofile : serverstatusresponse.getPlayerCountData().getPlayers()) {
                                            if (stringbuilder.length() > 0) {
                                                stringbuilder.append("\n");
                                            }

                                            stringbuilder.append(gameprofile.getName());
                                        }

                                        if (serverstatusresponse.getPlayerCountData().getPlayers().length < serverstatusresponse.getPlayerCountData().getOnlinePlayerCount()) {
                                            if (stringbuilder.length() > 0) {
                                                stringbuilder.append("\n");
                                            }

                                            stringbuilder.append("...和").append(serverstatusresponse.getPlayerCountData().getOnlinePlayerCount() - serverstatusresponse.getPlayerCountData().getPlayers().length).append("个...");
                                        }

                                        server.playerList = stringbuilder.toString();
                                    }
                                } else {
                                    server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                                }

                                if (serverstatusresponse.getFavicon() != null) {
                                    String s = serverstatusresponse.getFavicon();

                                    if (s.startsWith("data:image/png;base64,")) {
                                        server.setBase64EncodedIconData(s.substring("data:image/png;base64,".length()));
                                    } else {
                                        Reversal.getLogger().error("Invalid server icon (unknown format)");
                                    }
                                } else {
                                    server.setBase64EncodedIconData(null);
                                }

                                this.field_175092_e = Minecraft.getSystemTime();
                                networkmanager.sendPacket(new C01PacketPing(this.field_175092_e));
                                this.field_147403_d = true;
                            }
                        }

                        public void handlePong(S01PacketPong packetIn) {
                            long i = this.field_175092_e;
                            long j = Minecraft.getSystemTime();
                            server.pingToServer = j - i;
                            networkmanager.closeChannel(new ChatComponentText("Finished"));
                        }

                        public void onDisconnect(IChatComponent reason) {
                            if (!this.field_147403_d) {
                                Reversal.getLogger().error("无法连接至 {}: {}", server.serverIP, reason.getUnformattedText());
                                server.serverMOTD = EnumChatFormatting.DARK_RED + "无法连接至服务器: " + reason.getFormattedText();
                                server.populationInfo = "";
                                Reversal.threadPoolExecutor.submit(createCompatibilityPingThread(server));
                            }
                        }
                    });

                    try {
                        networkmanager.sendPacket(new C00Handshake(47, serveraddress.getIP(), serveraddress.getPort(), EnumConnectionState.STATUS));
                        networkmanager.sendPacket(new C00PacketServerQuery());
                    } catch (Throwable throwable) {
                        Reversal.getLogger().error(throwable);
                    }
                } catch (UnknownHostException e) {
                    server.pingToServer = -1L;
                    server.serverMOTD = EnumChatFormatting.DARK_RED + "无法解析主机名";
                } catch (Exception e) {
                    server.pingToServer = -1L;
                    server.serverMOTD = EnumChatFormatting.DARK_RED + "无法连接至服务器";
                }
            }
        };
        pingThread.setPriority(10);
        return pingThread;
    }

    private Thread createCompatibilityPingThread(final ServerData server)
    {
        Thread compatibilityPingThread = new Thread("Compatibility Pinger") {
            public void run() {
                final ServerAddress serveraddress = ServerAddress.fromString(server.serverIP);
                (new Bootstrap()).group(NetworkManager.CLIENT_NIO_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>()
                {
                    protected void initChannel(Channel p_initChannel_1_) {
                        try
                        {
                            p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
                        }
                        catch (ChannelException ignored)
                        {
                        }

                        p_initChannel_1_.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>()
                        {
                            public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception
                            {
                                super.channelActive(p_channelActive_1_);
                                ByteBuf bytebuf = Unpooled.buffer();

                                try
                                {
                                    bytebuf.writeByte(254);
                                    bytebuf.writeByte(1);
                                    bytebuf.writeByte(250);
                                    char[] achar = "MC|PingHost".toCharArray();
                                    bytebuf.writeShort(achar.length);

                                    for (char c0 : achar)
                                    {
                                        bytebuf.writeChar(c0);
                                    }

                                    bytebuf.writeShort(7 + 2 * serveraddress.getIP().length());
                                    bytebuf.writeByte(127);
                                    achar = serveraddress.getIP().toCharArray();
                                    bytebuf.writeShort(achar.length);

                                    for (char c1 : achar)
                                    {
                                        bytebuf.writeChar(c1);
                                    }

                                    bytebuf.writeInt(serveraddress.getPort());
                                    p_channelActive_1_.channel().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                                }
                                finally
                                {
                                    bytebuf.release();
                                }
                            }
                            protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, ByteBuf p_channelRead0_2_) throws Exception
                            {
                                short short1 = p_channelRead0_2_.readUnsignedByte();

                                if (short1 == 255)
                                {
                                    String s = new String(p_channelRead0_2_.readBytes(p_channelRead0_2_.readShort() * 2).array(), Charsets.UTF_16BE);
                                    String[] astring = Iterables.toArray(PING_RESPONSE_SPLITTER.split(s), String.class);

                                    if ("§1".equals(astring[0]))
                                    {
                                        String s1 = astring[2];
                                        String s2 = astring[3];
                                        int j = MathHelper.parseIntWithDefault(astring[4], -1);
                                        int k = MathHelper.parseIntWithDefault(astring[5], -1);
                                        server.version = -1;
                                        server.gameVersion = s1;
                                        server.serverMOTD = s2;
                                        server.populationInfo = EnumChatFormatting.GRAY + "" + j + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + k;
                                    }
                                }

                                p_channelRead0_1_.close();
                            }
                            public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) {
                                p_exceptionCaught_1_.close();
                            }
                        });
                    }
                }).channel(NioSocketChannel.class).connect(serveraddress.getIP(), serveraddress.getPort());
            }
        };
        compatibilityPingThread.setPriority(10);
        return compatibilityPingThread;
    }

    public void pingPendingNetworks()
    {
        synchronized (this.pingDestinations)
        {
            Iterator<NetworkManager> iterator = this.pingDestinations.iterator();

            while (iterator.hasNext())
            {
                NetworkManager networkmanager = iterator.next();

                if (networkmanager.isChannelOpen())
                {
                    networkmanager.processReceivedPackets();
                }
                else
                {
                    iterator.remove();
                    networkmanager.checkDisconnected();
                }
            }
        }
    }

    public void clearPendingNetworks()
    {
        synchronized (this.pingDestinations)
        {
            Iterator<NetworkManager> iterator = this.pingDestinations.iterator();

            while (iterator.hasNext())
            {
                NetworkManager networkmanager = iterator.next();

                if (networkmanager.isChannelOpen())
                {
                    iterator.remove();
                    networkmanager.closeChannel(new ChatComponentText("Cancelled"));
                }
            }
        }
    }
}
