package cn.stars.addons.dglab;

import cn.stars.addons.dglab.Tool.DGWaveformTool;
import cn.stars.addons.dglab.config.MainConfig;
import cn.stars.addons.dglab.config.StrengthConfig;
import cn.stars.addons.dglab.config.WaveformConfig;
import cn.stars.addons.dglab.entity.DGStrength;
import cn.stars.addons.dglab.entity.Waveform.Waveform;
import cn.stars.addons.dglab.qr.ToolQR;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.impl.addons.Dglab;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;



public class DglabClient {

    public static WebSocketServer webSocketServer = null;
    public static StrengthConfig strengthConfig = new StrengthConfig();
    public static final MainConfig mainConfig = MainConfig.loadJson();
    public static Map<String, Waveform> waveformMap = WaveformConfig.LoadWaveform();

    public static void init() {
        // 注册连接的服务器
        webSocketServer = new WebSocketServer(new InetSocketAddress(mainConfig.getServerPort()));

        strengthConfig = StrengthConfig.loadJson();

        DGWaveformTool.updateDuration();

        if (mainConfig.getAutoStartWebSocketServer()) {
            webSocketServer.start();
            Dglab.serverLoaded = true;
        }
    }

    // 二维码
    public static void createQR() {
        ToolQR.createQR();
    }

    public static void setStrength(int a, int b) {
        DGStrength DGStrength = webSocketServer.getStrength();
        DGStrength.setAStrength(a);
        DGStrength.setBStrength(b);
        webSocketServer.setStrength(DGStrength);
        webSocketServer.sendStrength();
    }

    public static void setMinStrength(int a, int b) {
        strengthConfig.setAMin(a);
        strengthConfig.setBMin(b);
        strengthConfig.savaFile();
    }

    public static void setDamageStrength(float a, float b) {
        strengthConfig.setADamageStrength(a);
        strengthConfig.setBDamageStrength(b);
        strengthConfig.savaFile();
    }

    public static void setDelayTime(int a, int b) {
        strengthConfig.setADelayTime(a);
        strengthConfig.setBDelayTime(b);
        strengthConfig.savaFile();
    }

    public static void setDownTime(int a, int b) {
        strengthConfig.setADownTime(a);
        strengthConfig.setBDownTime(b);
        strengthConfig.savaFile();
    }

    public static void setDownValue(int a, int b) {
        strengthConfig.setADownValue(a);
        strengthConfig.setBDownValue(b);
        strengthConfig.savaFile();
    }

    public static void startWebSocketServer() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String ipAddress = localhost.getHostAddress();
            Reversal.showMsg("本地ip:" + ipAddress);
            Reversal.showMsg("请确保连接的手机和此客户端在同一局域网下");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        webSocketServer.start();
    }



    public static WebSocketServer getServer() {return webSocketServer;}

    public static StrengthConfig getStrengthConfig() {return strengthConfig;}

    public static MainConfig getMainConfig(){return mainConfig;}


    //屏幕强度显示
 /*   private void onHudRender(DrawContext drawContext, RenderTickCounter tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && client.world != null && (mainConfig.getRenderingPositionX() < client.getWindow().getScaledWidth() || mainConfig.getRenderingPositionY() < client.getWindow().getScaledHeight())) {
            // 假设强度数值是一个整数
//            int strengthValue = getStrengthValue(client.player);

            // 计算图标和文本的位置
            int x = mainConfig.getRenderingPositionX();
            int y = mainConfig.getRenderingPositionY();


            // 创建并渲染 OrderedText

            if(webSocketServer.getConnected()) {
                Text strengthText;
                Text strengthText1;
                if(mainConfig.isRenderingMax()) {
                    strengthText = Text.literal("A:" + webSocketServer.getStrength().getAStrength() + ",Max:" + webSocketServer.getStrength().getAMaxStrength());

                    strengthText1 = Text.literal("B:" + webSocketServer.getStrength().getBStrength() + ",Max:" + webSocketServer.getStrength().getBMaxStrength());

                }
                else {
                    strengthText = Text.literal("A:" + webSocketServer.getStrength().getAStrength());

                    strengthText1 = Text.literal("B:" + webSocketServer.getStrength().getBStrength());
                }
                OrderedText orderedText = strengthText.asOrderedText();
                OrderedText orderedText1 = strengthText1.asOrderedText();
                drawContext.drawTextWithShadow(client.textRenderer, orderedText, x, y, 0xFFFFFF);
                drawContext.drawTextWithShadow(client.textRenderer, orderedText1, x, y + 9, 0xFFFFFF);
            }
            else {
                Text strengthText = Text.literal("未连接");
                OrderedText orderedText = strengthText.asOrderedText();
                drawContext.drawTextWithShadow(client.textRenderer, orderedText, x, y, 0xFF0000);
            }
        }
    } */


}
