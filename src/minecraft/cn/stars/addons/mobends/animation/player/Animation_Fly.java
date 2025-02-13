package cn.stars.addons.mobends.animation.player;

import cn.stars.addons.mobends.animation.Animation;
import cn.stars.addons.mobends.client.model.ModelRendererBends;
import cn.stars.addons.mobends.client.model.entity.ModelBendsPlayer;
import cn.stars.addons.mobends.data.Data_Player;
import cn.stars.addons.mobends.data.EntityData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;

public class Animation_Fly extends Animation {
    @Override
    public String getName() {
        return "fly";
    }

    @Override
    public void animate(final EntityLivingBase argEntity, final ModelBase argModel, final EntityData argData) {
        final ModelBendsPlayer model = (ModelBendsPlayer) argModel;
        final Data_Player data = (Data_Player) argData;

        // 使身体放平，模拟飞行姿势
        ((ModelRendererBends) model.bipedBody).rotation.setSmoothX(90.0f, 0.2f);

        // 手臂伸展开来，并调整方向，使其朝外，而不是向内
        ((ModelRendererBends) model.bipedRightArm).rotation.setSmoothX(-10.0f, 0.3f);
        ((ModelRendererBends) model.bipedLeftArm).rotation.setSmoothX(-10.0f, 0.3f);
        ((ModelRendererBends) model.bipedRightArm).rotation.setSmoothZ(75.0f, 0.3f);
        ((ModelRendererBends) model.bipedLeftArm).rotation.setSmoothZ(-75.0f, 0.3f);

        // 腿部也放平，与身体一致
        ((ModelRendererBends) model.bipedRightLeg).rotation.setSmoothX((float) (90.0f + (Math.sin(data.ticks / 5.0f) * 10.0f)), 0.3f);
        ((ModelRendererBends) model.bipedLeftLeg).rotation.setSmoothX((float) (90.0f - (Math.sin(data.ticks / 5.0f) * 10.0f)), 0.3f);

        // 头部调整朝向，使玩家能够正常看向前方
        ((ModelRendererBends) model.bipedHead).rotation.setSmoothX(-60.0f, 0.3f);
        ((ModelRendererBends) model.bipedHead).rotation.setSmoothY(0.0f, 0.3f);
    }
}
