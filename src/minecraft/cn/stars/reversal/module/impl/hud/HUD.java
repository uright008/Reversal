package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.ui.hud.Hud;
import cn.stars.reversal.value.impl.BoolValue;

@ModuleInfo(name = "HUD", localizedName = "module.HUD.name", description = "Show a hud on your screen",
        localizedDescription = "module.HUD.desc", category = Category.HUD, defaultEnabled = true)
public class HUD extends Module {
    public final BoolValue display_when_debugging = new BoolValue("Display when debugging", this, false);
    public HUD() {
        setWidth(0);
        setHeight(0);
        setCanBeEdited(false);
    }
    private boolean firstBootHandled = false;

    @Override
    public void onUpdateAlways() {
        if (Reversal.firstBoot && !isEnabled() && !firstBootHandled) {
            this.setEnabled(true);
            firstBootHandled = true;
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        Hud.renderHud();
    }
}
