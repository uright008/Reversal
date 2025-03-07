package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.PreMotionEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;

@ModuleInfo(name = "NoBob", localizedName = "移除摇晃", description = "Disable the walking bob effect",
        localizedDescription = "关闭走路时手臂的摇晃动画", category = Category.RENDER)
public class NoBob extends Module {
    @Override
    public void onPreMotion(PreMotionEvent event) {
        mc.thePlayer.distanceWalkedModified = 0f;
    }
}
