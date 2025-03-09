package cn.stars.addons.optimization.entityculling;

import cn.stars.reversal.module.impl.client.Optimization;
import cn.stars.reversal.util.misc.ModuleInstance;

public class EntityCullingMod extends EntityCullingModBase {


    public EntityCullingMod() {
    }

    @Override
    public void initModloader() {
    }

    public void doClientTick() {
        this.clientTick();
    }

    public void doWorldTick() {
        this.worldTick();
    }

}