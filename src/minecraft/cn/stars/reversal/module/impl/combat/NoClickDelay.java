package cn.stars.reversal.module.impl.combat;

import cn.stars.reversal.event.impl.PreMotionEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;

@ModuleInfo(name = "NoClickDelay", localizedName = "移除点击延迟", description = "Remove the delay of clicking",
        localizedDescription = "移除特殊情况下出现的鼠标点击延迟", category = Category.COMBAT)
public class NoClickDelay extends Module {

    @Override
    public void onPreMotion(PreMotionEvent event) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (!mc.inGameHasFocus) return;
            mc.leftClickCounter = 0;
        }
    }
}
