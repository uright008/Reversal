package cn.stars.reversal.module.impl.addons;

import cn.stars.addons.dglab.DglabClient;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.UpdateEvent;
import cn.stars.reversal.event.impl.ValueChangedEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.value.impl.*;
import lombok.SneakyThrows;

import java.net.InetAddress;

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
    public final NumberValue a_DecreaseDelay = new NumberValue("A Decrease Delay (ms)", this, 2000, 1, 10000, 50);
    public final NumberValue b_DecreaseDelay = new NumberValue("B Decrease Delay (ms)", this, 2000, 1, 10000, 50);
    // 开始减少后多少时间减少一次
    public final NumberValue a_DecreaseCountdown = new NumberValue("A Decrease Countdown (ms)", this, 200, 1, 5000, 50);
    public final NumberValue b_DecreaseCountdown = new NumberValue("B Decrease Countdown (ms)", this, 200, 1, 5000, 50);

    // Websocket
    public final TextValue qrCodeIp = new TextValue("QRCode IP", this, "192.168.31.51");
    public final TextValue qrCodePort = new TextValue("QRCode Port", this, "9999");
    public final TextValue serverPort = new TextValue("Server Port", this, "9999");

    // 波形A
    public final TextValue a_DamageWaveform = new TextValue("A Waveform (Damage)", this, "#\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64000000\"");
    public final TextValue a_HealingWaveform = new TextValue("A Waveform (Healing)", this, "#\"0A0A0A0A1921282F\",\"0A0A0A0A363D444B\",\"0A0A0A0A4B433C35\",\"0A0A0A0A2E272019\"");

    // 波形B
    public final TextValue b_DamageWaveform = new TextValue("B Waveform (Damage)", this, "#\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64646464\",\"0A0A0A0A64000000\"");
    public final TextValue b_HealingWaveform = new TextValue("B Waveform (Healing)", this, "#\"0A0A0A0A1921282F\",\"0A0A0A0A363D444B\",\"0A0A0A0A4B433C35\",\"0A0A0A0A2E272019\"");

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
    //    DglabClient.
    }

    @SneakyThrows
    @Override
    public void onUpdate(UpdateEvent event) {
        if (!serverLoaded) {
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

    @Override
    public void onLoad() {
        DglabClient.init();
    }
}
