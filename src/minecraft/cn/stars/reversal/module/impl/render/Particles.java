/*
 Copyright Alan Wood 2021
 None of this code to be reused without my written permission
 Intellectual Rights owned by Alan Wood
 */
package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.AttackEvent;
import cn.stars.reversal.event.impl.PreMotionEvent;
import cn.stars.reversal.event.impl.Render3DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.NumberValue;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.misc.EvictingList;
import cn.stars.reversal.util.render.HitParticleUtils;
import cn.stars.reversal.util.render.RenderUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

import java.util.List;

@ModuleInfo(name = "Particles", localizedName = "module.Particles.name", description = "Renders colorful balls when you attack someone",
        localizedDescription = "module.Particles.desc", category = Category.RENDER)
public final class Particles extends Module {
    public final ColorValue colorValue = new ColorValue("Color", this);
    private final NumberValue amount = new NumberValue("Amount", this, 10, 1, 20, 1);
    private final BoolValue physics = new BoolValue("Physics", this, true);
    private final NumberValue aliveTime = new NumberValue("Alive Time", this, 1, 1, 10, 1);

    private final List<HitParticleUtils> particles = new EvictingList<>(100);
    private final TimeUtil timer = new TimeUtil();
    private EntityLivingBase target;

    @Override
    public void onDisable() {
        particles.clear();
    }

    @Override
    public void onAttack(final AttackEvent event) {
        if (event.getTarget() instanceof EntityLivingBase)
            target = (EntityLivingBase) event.getTarget();
    }

    @Override
    public void onPreMotion(final PreMotionEvent event) {
        if (target != null && target.hurtTime >= 9 && mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) < 10) {
            for (int i = 0; i < amount.getValue(); i++)
                particles.add(new HitParticleUtils(new Vec3(target.posX + (Math.random() - 0.5) * 0.5, target.posY + Math.random() * 1 + 0.5, target.posZ + (Math.random() - 0.5) * 0.5)));

            target = null;
        }
    }

    @Override
    public void onRender3D(final Render3DEvent event) {
        if (particles.isEmpty())
            return;

        for (int i = 0; i <= timer.getElapsedTime() / 1E+11 * aliveTime.getValue(); i++) {
            if (physics.isEnabled())
                particles.forEach(HitParticleUtils::update);
            else
                particles.forEach(HitParticleUtils::updateWithoutPhysics);
        }

        particles.removeIf(particle -> mc.thePlayer.getDistanceSq(particle.getPosition().xCoord, particle.getPosition().yCoord, particle.getPosition().zCoord) > 50 * 10);

        timer.reset();

        RenderUtil.renderParticles(particles);
    }
}