package cn.stars.reversal.module.impl.addons;

import cn.stars.addons.rfp.RFP;
import cn.stars.reversal.event.impl.TickEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;

@ModuleInfo(name = "RealFirstPerson", localizedName = "module.RealFirstPerson.name", description = "Display the whole body on first person", localizedDescription = "module.RealFirstPerson.desc", category = Category.ADDONS)
public class RealFirstPerson extends Module {
    public static RFP rfp = new RFP();

    @Override
    public void onLoad() {
        rfp.init();
    }

    @Override
    public void onTick(TickEvent event) {
        rfp.onTick();
    }

    @Override
    protected void onDisable() {
        rfp.onDisable();
    }
}
