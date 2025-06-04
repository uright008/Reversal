package cn.stars.addons.optimization.entityculling;

import cn.stars.reversal.module.impl.client.Optimization;
import cn.stars.reversal.util.misc.ModuleInstance;

public class EntityCullingMod extends EntityCullingModBase {


    public EntityCullingMod() {
    }

    public void doClientTick() {
        if (ModuleInstance.getModule(Optimization.class).entityCulling.enabled) this.clientTick();
    }

    public void doWorldTick() {
        if (ModuleInstance.getModule(Optimization.class).entityCulling.enabled) this.worldTick();
    }

}