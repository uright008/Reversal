package cn.stars.reversal.module.impl.addons;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.NumberValue;

@ModuleInfo(name = "SkinLayers3D", localizedName = "3D皮肤层", description = "Extend your skin with extra layers",
        localizedDescription = "3D渲染你的皮肤层", category = Category.ADDONS)
public class SkinLayers3D extends Module {
    public final NumberValue baseVoxelSize = new NumberValue("Voxel Size", this, 1.15, 1.01, 1.4, 0.01);
    public final NumberValue bodyVoxelSize = new NumberValue("Torso Voxel Width", this, 1.05, 1.01, 1.4, 0.01);
    public final NumberValue headVoxelSize = new NumberValue("Head Voxel Size", this, 1.18, 1.01, 1.25, 0.01);
    public final NumberValue renderDistance = new NumberValue("Level Of Detail Distance", this ,14, 5, 40, 1);
}
