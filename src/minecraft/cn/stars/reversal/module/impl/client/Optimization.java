package cn.stars.reversal.module.impl.client;

import cn.stars.addons.optimization.entityculling.EntityCullingMod;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.value.impl.BoolValue;

@ModuleInfo(name = "Optimization", localizedName = "module.Optimization.name", description = "Optimize client performance", localizedDescription = "module.Optimization.desc", category = Category.CLIENT)
public class Optimization extends Module {
    public final BoolValue entityCulling = new BoolValue("Entity Culling", this, true);

    public static EntityCullingMod entityCullingMod;

    @Override
    public void onUpdateAlways() {
        checkClientModuleState();
    }

    @Override
    public void onLoad() {
        entityCullingMod = new EntityCullingMod();
        entityCullingMod.onInitialize();
    }
}
