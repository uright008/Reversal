package cn.stars.reversal.module.impl.addons;

import cn.stars.addons.rfp.REN;
import cn.stars.reversal.event.impl.TickEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;

@ModuleInfo(name ="RFP", description = "?", category = Category.ADDONS)
public class RFP extends Module {
    REN ren = new REN();

    @Override
    public void onLoad() {
        ren.init();
    }

    @Override
    public void onTick(TickEvent event) {
        ren.onTick();
    }
}
