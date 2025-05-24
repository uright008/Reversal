package cn.stars.reversal.module.impl.player;

import cn.stars.reversal.event.impl.UpdateEvent;
import cn.stars.reversal.event.impl.WorldEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.math.RandomUtil;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

@ModuleInfo(name = "AutoTip", localizedName = "module.AutoTip.name", description = "Auto tip players in hypixel", localizedDescription = "module.AutoTip.desc", category = Category.PLAYER)
public class AutoTip extends Module {
    public final NumberValue delay = new NumberValue("Delay", this, 5, 4, 10, 1);
    public final BoolValue anonymous = new BoolValue("Anonymous", this, true);

    private final String[] gm = new String[] {"sw", "tnt", "classic", "bsg", "mw", "cvc", "war", "uhc", "smash", "speed"};
    private final ArrayList<String> usedName = new ArrayList<>();
    private final TimeUtil timeUtil = new TimeUtil();
    private int currentIndex;

    @Override
    public void onUpdate(UpdateEvent event) {
        if (timeUtil.hasReached(delay.getInt() * 1000L)) {
            searchAndTip();
            timeUtil.reset();
        }
    }

    private void searchAndTip() {
        if (usedName.size() > 100) usedName.clear();
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (!usedName.contains(player.getName())) {
                usedName.add(player.getName());
            }
        }
        if (usedName.isEmpty() || (usedName.size() == 1 && usedName.get(0).equals(mc.thePlayer.getName()))) return;
        int playerIndex = RandomUtil.INSTANCE.nextInt(0, usedName.size() - 1);
        if (currentIndex >= gm.length) {
            mc.thePlayer.sendChatMessage("/tipall");
            currentIndex = 0;
        } else {
            mc.thePlayer.sendChatMessage("/tip" + (anonymous.enabled ? " -a " : " ") + usedName.get(playerIndex) + " " + gm[currentIndex]);
            usedName.remove(playerIndex);
            currentIndex++;
        }
    }

    @Override
    public void onWorld(WorldEvent event) {
        timeUtil.reset();
        usedName.clear();
    }
}
