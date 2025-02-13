package cn.stars.addons.mobends.animation.player;

import cn.stars.addons.mobends.animation.Animation;
import cn.stars.addons.mobends.client.model.ModelRendererBends;
import cn.stars.addons.mobends.client.model.entity.ModelBendsPlayer;
import cn.stars.addons.mobends.data.Data_Player;
import cn.stars.addons.mobends.data.EntityData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;

public class Animation_Climb extends Animation {
    @Override
    public String getName() {
        return "climb";
    }

    @Override
    public void animate(final EntityLivingBase argEntity, final ModelBase argModel, final EntityData argData) {
        final ModelBendsPlayer model = (ModelBendsPlayer) argModel;
        final Data_Player data = (Data_Player) argData;

        // 使身体稍微前倾，模拟爬梯子姿势
        ((ModelRendererBends) model.bipedBody).rotation.setSmoothX(10.0f, 0.2f);

        // 手臂交替向前高举过头，模拟抓住梯子的动作
        ((ModelRendererBends) model.bipedRightArm).rotation.setSmoothX((float) (-Math.sin(data.ticks / 5.0f) * 30.0f - 150.0f), 0.3f);
        ((ModelRendererBends) model.bipedLeftArm).rotation.setSmoothX((float) (Math.sin(data.ticks / 5.0f) * 30.0f - 150.0f), 0.3f);
        ((ModelRendererBends) model.bipedRightArm).rotation.setSmoothZ(10.0f, 0.3f);
        ((ModelRendererBends) model.bipedLeftArm).rotation.setSmoothZ(-10.0f, 0.3f);

        // 腿部交替弯曲，模拟攀爬时的腿部动作
        ((ModelRendererBends) model.bipedRightLeg).rotation.setSmoothX((float) (Math.sin(data.ticks / 5.0f) * 30.0f), 0.3f);
        ((ModelRendererBends) model.bipedLeftLeg).rotation.setSmoothX((float) (-Math.sin(data.ticks / 5.0f) * 30.0f), 0.3f);

        // 前臂保持稍微弯曲，增强自然感
        model.bipedRightForeLeg.rotation.setSmoothX(15.0f, 0.2f);
        model.bipedLeftForeLeg.rotation.setSmoothX(15.0f, 0.2f);

        // 头部保持正常运动，使其不影响玩家视角
        ((ModelRendererBends) model.bipedHead).rotation.setX(model.headRotationX);
        ((ModelRendererBends) model.bipedHead).rotation.setY(model.headRotationY);
    }
}