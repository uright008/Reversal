package cn.stars.reversal.module.impl.addons;

import cn.stars.addons.dglab.DglabClient;
import cn.stars.addons.dglab.config.WaveformConfig;
import cn.stars.addons.dglab.entity.DGStrength;
import cn.stars.addons.dglab.entity.NetworkAdapter;
import cn.stars.addons.dglab.entity.Waveform.Waveform;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.TickEvent;
import cn.stars.reversal.event.impl.UpdateEvent;
import cn.stars.reversal.event.impl.ValueChangedEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.value.impl.*;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ModuleInfo(name = "Dglab", localizedName = "module.Dglab.name", description = "Connect to your dglab device (3.x Required)", localizedDescription = "module.Dglab.desc", category = Category.ADDONS)
public class Dglab extends Module {
    public final NoteValue noteValue = new NoteValue("别瞎拉不然有你好看的", this);
    public final NumberValue a_Strength = new NumberValue("A Strength", this, 10, 1, 100, 1); // max=100, 200太危险
    public final NumberValue b_Strength = new NumberValue("B Strength", this, 10, 1, 100, 1);
    public final NumberValue a_DamageIncrease = new NumberValue("A Damage Increase", this, 1, 1, 20, 1); // max=20 不然电死你
    public final NumberValue b_DamageIncrease = new NumberValue("B Damage Increase", this, 1, 1, 20, 1);

    public final NumberValue a_Decrease = new NumberValue("A Decrease", this, 1, 1, 100, 1); // max=100
    public final NumberValue b_Decrease = new NumberValue("B Decrease", this, 1, 1, 100, 1);
    // 受伤后多少时间后开始减少
    public final NumberValue a_DecreaseDelay = new NumberValue("A Decrease Delay (tick)", this, 40, 1, 100, 1);
    public final NumberValue b_DecreaseDelay = new NumberValue("B Decrease Delay (tick)", this, 40, 1, 100, 1);
    // 开始减少后多少时间减少一次
    public final NumberValue a_DecreaseCountdown = new NumberValue("A Decrease Countdown (tick)", this, 5, 1, 100, 1);
    public final NumberValue b_DecreaseCountdown = new NumberValue("B Decrease Countdown (tick)", this, 5, 1, 100, 1);

    // Websocket
    public final TextValue qrCodeIp = new TextValue("QRCode IP", this, "192.168.31.51");
    public final TextValue qrCodePort = new TextValue("QRCode Port", this, "9999");
    public final TextValue serverPort = new TextValue("Server Port", this, "9999");
    public final CustomValue loadQrCode = new CustomValue("Load QR Code", this, DglabClient::createQR);
    public final CustomValue toggleNetwork = new CustomValue("Toggle Network Adapter", this, this::toggleNetworkAdapter);

    // 波形A
    public final TextValue a_DamageWaveform = new TextValue("A Waveform (Damage)", this, "\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64000000\"");
    public final TextValue a_HealingWaveform = new TextValue("A Waveform (Healing)", this, "\"0A0A0A0A1921282F\",\"0A0A0A0A363D444B\",\"0A0A0A0A4B433C35\",\"0A0A0A0A2E272019\"");

    // 波形B
    public final TextValue b_DamageWaveform = new TextValue("B Waveform (Damage)", this, "\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64000000\"");
    public final TextValue b_HealingWaveform = new TextValue("B Waveform (Healing)", this, "\"0A0A0A0A1921282F\",\"0A0A0A0A363D444B\",\"0A0A0A0A4B433C35\",\"0A0A0A0A2E272019\"");

    public static boolean serverLoaded = false;
    private final TimeUtil timeUtil = new TimeUtil();

    @Override
    public void onValueChanged(ValueChangedEvent event) {
        updateAllValues();
    }

    public void updateAllValues() {
        DglabClient.setMinStrength(a_Strength.getInt(), b_Strength.getInt());
        DglabClient.setDamageStrength(a_DamageIncrease.getInt(), b_DamageIncrease.getInt());
        DglabClient.setDownValue(a_Decrease.getInt(), b_Decrease.getInt());
        DglabClient.setDelayTime(a_DecreaseDelay.getInt(), b_DecreaseDelay.getInt());
        DglabClient.setDownTime(a_DecreaseCountdown.getInt(), b_DecreaseCountdown.getInt());
        DglabClient.mainConfig.setAddress(qrCodeIp.getText());
        DglabClient.mainConfig.setPort(Integer.parseInt(qrCodePort.getText()));
        DglabClient.mainConfig.setServerPort(Integer.parseInt(serverPort.getText()));

        Map<String, Waveform> waveform = new HashMap<>();
        waveform.put("ADamage", new Waveform(a_DamageWaveform.getText()).DataToGraph());
        waveform.put("BDamage", new Waveform(b_DamageWaveform.getText()).DataToGraph());
        waveform.put("AHealing", new Waveform(a_HealingWaveform.getText()).DataToGraph());
        waveform.put("BHealing", new Waveform(b_HealingWaveform.getText()).DataToGraph());
        DglabClient.waveformMap = waveform;

        DglabClient.mainConfig.savaFile();
        DglabClient.getStrengthConfig().savaFile();
        WaveformConfig.saveWaveform(waveform);
    }

    private int tickCounter = 0; // 计数器，用于跟踪游戏刻
    private int lastRunTickA = 0; // 上次为A执行的tick计数器值
    private int lastRunTickB = 0; // 上次为B执行的tick计数器值
    private boolean hasDetectedADelay = false; // 标志，表示是否检测到A的延迟第一次不为0
    private boolean hasDetectedBDelay = false; // 标志，表示是否检测到B的延迟第一次不为0
    private boolean ClearA = false; // 标志，表示是否检测到A的延迟第一次不为0
    private boolean ClearB = false; // 标志，表示是否检测到B的延迟第一次不为0
    private boolean hasDetectedADelayZeroAndStrength = false; // 标志，表示是否检测到A的延迟为0且强度大于0
    private boolean hasDetectedBDelayZeroAndStrength = false; // 标志，表示是否检测到B的延迟为0且强度大于0

    @Override
    public void onTick(TickEvent event) {
        DGStrength dgStrength = DglabClient.webSocketServer.getStrength(); // 获取DGStrength对象
        int ADelayTime = dgStrength.getADelayTime(), BDelayTime = dgStrength.getBDelayTime(); // 获取A和B的等待时间


        // 更新等待时间
        ADelayTime = (ADelayTime > 0) ? ADelayTime - 1 : 0; // 如果ADelayTime大于0，减少1；否则设置为0
        BDelayTime = (BDelayTime > 0) ? BDelayTime - 1 : 0; // 如果BDelayTime大于0，减少1；否则设置为0
        DglabClient.webSocketServer.setDelayTime(ADelayTime, BDelayTime); // 设置更新后的等待时间

        int AStrength = dgStrength.getAStrength(), BStrength = dgStrength.getBStrength(); // 获取A和B的强度
        int AMin = 0, BMin = 0;
        if(mc.thePlayer != null){
            AMin = (int) (DglabClient.strengthConfig.getAMin() * ((mc.thePlayer.getMaxHealth() - mc.thePlayer.getHealth()) / mc.thePlayer.getMaxHealth()));
            BMin = (int) (DglabClient.strengthConfig.getBMin() * ((mc.thePlayer.getMaxHealth() - mc.thePlayer.getHealth()) / mc.thePlayer.getMaxHealth()));
        }
        if (tickCounter % DglabClient.strengthConfig.getADownTime() == 0 && ADelayTime <= 0 && AStrength > AMin){
            // 如果计数器是ADownTime的倍数，且ADelayTime小于等于0且AStrength大于0，则发送A的强度值
            if(DglabClient.webSocketServer.getStrength().getAStrength() - DglabClient.strengthConfig.getADownValue() < AMin) {
                DglabClient.webSocketServer.sendStrengthToClient(AMin, 2, 1);
            }
            else
                DglabClient.webSocketServer.sendStrengthToClient(DglabClient.strengthConfig.getADownValue(), 0, 1);
        }
        if (tickCounter % DglabClient.strengthConfig.getBDownTime() == 0 && BDelayTime <= 0 && BStrength > BMin) {
            // 如果计数器是BDownTime的倍数，且BDelayTime小于等于0且BStrength大于0，则发送B的强度值
            if(DglabClient.webSocketServer.getStrength().getBStrength() - DglabClient.strengthConfig.getBDownValue() < BMin)
                DglabClient.webSocketServer.sendStrengthToClient(BMin, 2, 2);
            else
                DglabClient.webSocketServer.sendStrengthToClient(DglabClient.strengthConfig.getBDownValue(), 0, 2);
        }

        // 检查A的延迟时间和强度
        if (ADelayTime > 0) {
            hasDetectedADelayZeroAndStrength = false;
            ClearA = false;
            if (!hasDetectedADelay) {
                DglabClient.webSocketServer.sendDgWaveform(2, true, 1);
                hasDetectedADelay = true;
            } else if (tickCounter - lastRunTickA >= DglabClient.waveformMap.get("ADamage").getDuration() * 2) {
                DglabClient.webSocketServer.sendDgWaveform(2, false, 1);
                lastRunTickA = tickCounter;
            }
        } else {
            hasDetectedADelay = false;
            if (AStrength > 0) {
                if (!hasDetectedADelayZeroAndStrength) {
                    DglabClient.webSocketServer.sendDgWaveform(3, true, 1);
                    hasDetectedADelayZeroAndStrength = true;
                } else if (tickCounter - lastRunTickA >= DglabClient.waveformMap.get("AHealing").getDuration() * 2) {
                    DglabClient.webSocketServer.sendDgWaveform(3, false, 1);
                    lastRunTickA = tickCounter;
                }

            }
            else if(!ClearA){
                DglabClient.webSocketServer.CleanFrequency(1);
                ClearA = true;
            }
        }

        if (BDelayTime > 0) {
            ClearB = false;
            hasDetectedBDelayZeroAndStrength = false;
            if (!hasDetectedBDelay) {
                DglabClient.webSocketServer.sendDgWaveform(2, true, 2);
                hasDetectedBDelay = true;
            } else if (tickCounter - lastRunTickB >= DglabClient.waveformMap.get("BDamage").getDuration() * 2) {
                DglabClient.webSocketServer.sendDgWaveform(2, false, 2);
                lastRunTickB = tickCounter;
            }
        } else {
            hasDetectedBDelay = false;
            if (BStrength > 0) {
                if (!hasDetectedBDelayZeroAndStrength) {
                    DglabClient.webSocketServer.sendDgWaveform(3, true, 2);
                    hasDetectedBDelayZeroAndStrength = true;
                } else if (tickCounter - lastRunTickB >= DglabClient.waveformMap.get("BHealing").getDuration() * 2) {
                    DglabClient.webSocketServer.sendDgWaveform(3, false, 2);
                    lastRunTickB = tickCounter;
                }

            }
            else if(!ClearB){
                DglabClient.webSocketServer.CleanFrequency(2);
                ClearB = true;
            }
        }

        tickCounter++; // 增加计数器
        if (tickCounter == 2147483625) tickCounter = 0; // 如果计数器达到2147483625，则重置为0
    }

    @SneakyThrows
    @Override
    public void onUpdate(UpdateEvent event) {
        if (!serverLoaded) {
            Reversal.showMsg("开启Socket服务器!");
            DglabClient.startWebSocketServer();
            serverLoaded = true;
        }
        if (!DglabClient.webSocketServer.getConnected()) {
            if (timeUtil.hasReached(5000L)) {
                InetAddress localhost = InetAddress.getLocalHost();
                String ipAddress = localhost.getHostAddress();
                Reversal.showMsg("本地ip:" + ipAddress);
                Reversal.showMsg("请确保连接的手机和此客户端在同一局域网下");
                timeUtil.reset();
            }
        }
    }

    private NetworkAdapter network = new NetworkAdapter();
    private LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>(network.getNetworkMap());

    private void toggleNetworkAdapter(){
        boolean isKeyFound = false;
        for (Map.Entry<String, String> entry : linkedHashMap.entrySet()){
            if(entry.getKey().equals(DglabClient.mainConfig.getNetwork())) isKeyFound = true;
            else if(isKeyFound){
                DglabClient.mainConfig.setAddress(entry.getValue());
                DglabClient.mainConfig.setNetwork(entry.getKey());
                qrCodeIp.setText(entry.getValue());
                return;
            }
        }
        Map.Entry<String, String> firstEntry = linkedHashMap.entrySet().iterator().next();
        DglabClient.mainConfig.setAddress(firstEntry.getValue());
        DglabClient.mainConfig.setNetwork(firstEntry.getKey());
        qrCodeIp.setText(firstEntry.getValue());
    }

    @Override
    public void onLoad() {
        DglabClient.init();
        updateAllValues();
    }
}
