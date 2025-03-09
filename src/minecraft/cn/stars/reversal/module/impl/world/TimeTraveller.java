package cn.stars.reversal.module.impl.world;

import cn.stars.reversal.event.impl.PacketReceiveEvent;
import cn.stars.reversal.event.impl.Render3DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import cn.stars.reversal.util.math.TimeUtil;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;

@ModuleInfo(name = "TimeTraveller", localizedName = "module.TimeTraveller.name", description = "Travel the time to your own",
        localizedDescription = "module.TimeTraveller.desc", category = Category.WORLD)
public class TimeTraveller extends Module {
    private final NumberValue time = new NumberValue("Time", this, 0, 0, 22999, 1);
    private final NumberValue timeSpeed = new NumberValue("Time Speed", this, 0, 0, 20, 0.1);
    public final ModeValue weather = new ModeValue("Weather", this, "None", "None", "Sun", "Rain", "Thunder", "Snow", "Light Snow", "Nether");
    private final NumberValue weatherSt = new NumberValue("Weather Strength", this, 1, 0, 1, 0.1);
    private final NumberValue lnSt = new NumberValue("Lightning Strength", this, 1, 0, 1, 0.1);

    private final TimeUtil timer = new TimeUtil();

    @Override
    public void onUpdateAlways() {
        setSuffix(String.valueOf((int)time.getValue()));

    }

    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate)
            event.setCancelled(true);
        if (event.getPacket() instanceof S2BPacketChangeGameState && !weather.getMode().equals("None")) {
            S2BPacketChangeGameState p = ((S2BPacketChangeGameState) event.getPacket());
            if (p.getGameState() == 7 || p.getGameState() == 8) { // change weather packet
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onRender3D(final Render3DEvent event) {
        mc.theWorld.setWorldTime2((long) ((time.getValue() + (timer.getElapsedTime() * timeSpeed.getValue()))));
        switch (weather.getMode()) {
            case "Sun":
                mc.theWorld.setRainStrength(0f);
                mc.theWorld.setThunderStrength(0f);
                break;

            case "Rain":
            case "Snow":
            case "Light Snow":
            case "Nether":
                mc.theWorld.setRainStrength((float) weatherSt.getValue());
                mc.theWorld.setThunderStrength(0f);
                break;

            case "Thunder":
                mc.theWorld.setRainStrength((float) weatherSt.getValue());
                mc.theWorld.setThunderStrength((float) lnSt.getValue());
                break;
        }
    }
}
