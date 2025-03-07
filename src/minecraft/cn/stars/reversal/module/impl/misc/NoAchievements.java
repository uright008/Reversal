package cn.stars.reversal.module.impl.misc;

import cn.stars.reversal.event.impl.PacketReceiveEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import net.minecraft.network.play.server.S37PacketStatistics;

@ModuleInfo(name = "NoAchievements", localizedName = "移除成就", description = "Disable your achievements info",
        localizedDescription = "阻止显示你的成就信息", category = Category.MISC)
public class NoAchievements extends Module {
    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacket() instanceof S37PacketStatistics)
            event.setCancelled(true);
    }
}
