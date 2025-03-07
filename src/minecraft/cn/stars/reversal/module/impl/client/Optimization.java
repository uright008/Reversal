package cn.stars.reversal.module.impl.client;

import cn.stars.addons.optimization.entityculling.EntityCullingMod;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;

@ModuleInfo(name = "Optimization", localizedName = "优化", description = "Optimize client performance", localizedDescription = "优化客户端性能", category = Category.CLIENT)
public class Optimization extends Module {
    public final BoolValue ai_lookAI = new BoolValue("AI Improvements - Look AI", this, true);
    public final BoolValue ai_lookIdle = new BoolValue("AI Improvements - Look Idle", this, true);
    public final BoolValue ai_lookHelper = new BoolValue("AI Improvements - Look Helper", this, true);
    public final BoolValue entityCulling = new BoolValue("Entity Culling", this, true);

    public static EntityCullingMod entityCullingMod;

    @Override
    public void onUpdateAlways() {
        if (this.enabled) this.enabled = false;
    }

    @Override
    public void onLoad() {
        entityCullingMod = new EntityCullingMod();
        entityCullingMod.onInitialize();
    }
}
