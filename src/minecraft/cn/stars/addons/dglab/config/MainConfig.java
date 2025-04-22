package cn.stars.addons.dglab.config;

import cn.stars.addons.dglab.entity.NetworkAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainConfig {
    private boolean AutoStartWebSocketServer = false;
    private int port = 9999, serverPort = port;
    private String address, network;
    private boolean address2 = false, network2 = false;



    public MainConfig(boolean autoStartWebSocketServer) {
        AutoStartWebSocketServer = autoStartWebSocketServer;
        autoGetNetworkAddress();
    }

    public void autoGetNetworkAddress(){
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            if(localhost != null) {
                address = localhost.getHostAddress();
                address2 = true;
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localhost);
                if(networkInterface != null) {
                    network = networkInterface.getDisplayName();
                    network2 =true;
                }
            }
        } catch (UnknownHostException | SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = (port <0 || port > 65535) ? 9999 : port;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = (serverPort <0 || serverPort > 65535) ? 9999 : serverPort;
    }

    public String getAddress() {
        if(address2)
            return address;
        else return "error";
    }

    public String getNetwork() {
        if(network2)
            return network;
        else return "unknown";
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public void setAddress(String address) {
        this.address = address;
        try {
            // 通过IP地址获取InetAddress对象
            InetAddress inetAddress = InetAddress.getByName(address);
            address2 = true;
            network2 = false;
            if(inetAddress != null) {
                // 通过InetAddress获取对应的网卡

                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
                if(networkInterface != null) {
                    network2 = true;
                    network = networkInterface.getDisplayName();
                }


            }

        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    public void savaFile(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            File file = new File(Minecraft.getMinecraft().mcDataDir, "Reversal/Misc/Dglab/MainConfig.json");
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // 创建父目录
                file.createNewFile(); // 创建文件
            }
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(this, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException
        }
    }

    public static MainConfig loadJson() {
        Gson gson = new Gson();
        File file = new File(Minecraft.getMinecraft().mcDataDir, "Reversal/Misc/Dglab/MainConfig.json");
        if (!file.exists()) {
            return new MainConfig(true); // 默认的对象，可以根据需求初始化
        }
        try (Reader reader = new FileReader(file)) {
            NetworkAdapter networkInterface = new NetworkAdapter();
            MainConfig modConfig = gson.fromJson(reader, MainConfig.class);
            if(!modConfig.address2 || (modConfig.network2&&networkInterface.getNetworkMap().size() == 1)) modConfig.autoGetNetworkAddress();
            else{
                String address = networkInterface.NICGetaddress(modConfig.address);
                if(address != null)
                    modConfig.setAddress(address);
            }
            return modConfig;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Handle FileNotFoundException
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
            // Handle other JSON related exceptions
        }
        return null;
    }

    public MainConfig() {
    }

    public boolean getAutoStartWebSocketServer() {
        return AutoStartWebSocketServer;
    }

    public void setAutoStartWebSocketServer(boolean autoStartWebSocketServer) {
        AutoStartWebSocketServer = autoStartWebSocketServer;
    }
}
