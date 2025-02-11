package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.AttackEvent;
import cn.stars.reversal.event.impl.PreMotionEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(name = "HitEffect", chineseName = "攻击粒子", description = "Renders particle effect when you attack someone",
        chineseDescription = "当你攻击时生成粒子效果", category = Category.RENDER)
public final class HitEffect extends Module {

    private final ModeValue mode = new ModeValue("Mode", this, "Blood", "None", "Blood", "Lightning", "Flame", "Explosion",
            "Smoke");
    private final BoolValue sharp = new BoolValue("Sharpness", this ,false);
    private final BoolValue crit = new BoolValue("Critical", this ,false);
    private final NumberValue amount = new NumberValue("Amount", this, 5, 1, 10, 1);
    private final BoolValue sound = new BoolValue("Sound", this, true);

    private EntityLivingBase target;

    @Override
    public void onUpdateAlwaysInGui() {
        sound.hidden = !mode.is("Blood") && !mode.is("Lightning");
    }

    @Override
    public void onUpdateAlways() {
        setSuffix(mode.getMode());
    }

    @Override
    public void onAttack(final AttackEvent event) {
        if (event.getTarget() instanceof EntityLivingBase)
            target = (EntityLivingBase) event.getTarget();
    }

    @Override
    public void onPreMotion(final PreMotionEvent event) {
        if (target != null && target.hurtTime >= 9 && mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) < 10) {
            if (mc.thePlayer.ticksExisted > 3) {
                if (sharp.isEnabled()) {
                    for (int i = 0; i < amount.getValue(); i++)
                        mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT_MAGIC);
                }
                if (crit.isEnabled()) {
                    for (int i = 0; i < amount.getValue(); i++)
                        mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT);
                }
                switch (mode.getMode()) {
                    case "Blood":
                        for (int i = 0; i < amount.getValue() * 8; i++)
                            mc.theWorld.spawnParticle(EnumParticleTypes.BLOCK_CRACK, target.posX, target.posY + target.height - 0.75, target.posZ, 0, 0, 0, Block.getStateId(Blocks.redstone_block.getDefaultState()));

                        if (sound.isEnabled())
                            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("dig.stone"), ((float) target.posX), ((float) target.posY), ((float) target.posZ)));
                        break;

                    case "Lightning":
                        for (int i = 0; i < amount.getValue(); i++)
                            mc.getNetHandler().handleSpawnGlobalEntity(new S2CPacketSpawnGlobalEntity(new EntityLightningBolt(mc.theWorld, target.posX, target.posY, target.posZ)));

                        if (sound.isEnabled()) {
                            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.explode"), 1.0f));
                            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("ambient.weather.thunder"), 1.0f));
                        }
                    case "Flame":
                        for (int i = 0; i < amount.getValue(); i++)
                            mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.FLAME);
                        break;
                    case "Explosion":
                        for (int i = 0; i < amount.getValue(); i++)
                            mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.EXPLOSION_NORMAL);
                        break;
                    case "Smoke":
                        for (int i = 0; i < amount.getValue(); i++)
                            mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.SMOKE_NORMAL);
                        break;
                }
            }

            target = null;
        }
    }
}
