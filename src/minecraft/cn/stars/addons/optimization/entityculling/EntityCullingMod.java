package cn.stars.addons.optimization.entityculling;

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