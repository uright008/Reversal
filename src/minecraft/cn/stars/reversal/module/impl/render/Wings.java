package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.Render3DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.render.WingUtils;
import cn.stars.reversal.value.impl.BoolValue;

@ModuleInfo(name = "Wings", localizedName = "翅膀", description = "Render a wing on your back",
        localizedDescription = "在你的背上渲染一个翅膀", category = Category.RENDER)
public class Wings extends Module {
    private final BoolValue showInFirstPerson = new BoolValue("First Person", this, false);
    WingUtils wingUtils = new WingUtils();

    @Override
    public void onRender3D(Render3DEvent event) {
        if (mc.thePlayer != null && !mc.thePlayer.isDead && !mc.thePlayer.isInvisible()) {
            if (mc.gameSettings.thirdPersonView == 0 && !showInFirstPerson.isEnabled()) return;
            wingUtils.renderWings(event.getPartialTicks());
        }
    }
}
