package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.PreMotionEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;

@ModuleInfo(name = "NoBob", localizedName = "module.NoBob.name", description = "Disable the walking bob effect",
        localizedDescription = "module.NoBob.desc", category = Category.RENDER)
public class NoBob extends Module {
    @Override
    public void onPreMotion(PreMotionEvent event) {
        mc.thePlayer.distanceWalkedModified = 0f;
    }
}
