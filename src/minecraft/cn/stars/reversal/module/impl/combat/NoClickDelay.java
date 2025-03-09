package cn.stars.reversal.module.impl.combat;

import cn.stars.reversal.event.impl.PreMotionEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;

@ModuleInfo(name = "NoClickDelay", localizedName = "module.NoClickDelay.name", description = "Remove the delay of clicking",
        localizedDescription = "module.NoClickDelay.desc", category = Category.COMBAT)
public class NoClickDelay extends Module {

    @Override
    public void onPreMotion(PreMotionEvent event) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (!mc.inGameHasFocus) return;
            mc.leftClickCounter = 0;
        }
    }
}
