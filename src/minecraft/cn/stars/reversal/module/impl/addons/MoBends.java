package cn.stars.reversal.module.impl.addons;

import cn.stars.addons.mobends.AnimatedEntity;
import cn.stars.addons.mobends.client.renderer.entity.RenderBendsPlayer;
import cn.stars.addons.mobends.client.renderer.entity.RenderBendsSpider;
import cn.stars.addons.mobends.client.renderer.entity.RenderBendsZombie;
import cn.stars.addons.mobends.data.Data_Player;
import cn.stars.addons.mobends.data.Data_Spider;
import cn.stars.addons.mobends.data.Data_Zombie;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.Render3DEvent;
import cn.stars.reversal.event.impl.TickEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.misc.ModuleInstance;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.compatibility.util.vector.Vector3f;


@ModuleInfo(name = "MoBends", localizedName = "module.MoBends.name", description = "Show more animations on entity",
        localizedDescription = "module.MoBends.desc", category = Category.ADDONS)
public class MoBends extends Module {
    public static boolean loaded = false;
    public static float ticks;
    public static float ticksPerFrame;
    public static final ResourceLocation texture_NULL = new ResourceLocation("reversal/images/white.png");

    @Override
    public void onUpdateAlways() {
        if ((ModuleInstance.getModule(WaveyCapes.class).isEnabled() || ModuleInstance.getModule(RealFirstPerson.class).isEnabled()) && this.isEnabled()) {
            Reversal.showMsg(I18n.format("message.MoBends.conflict"));
            this.setEnabled(false);
        }
    }

    @Override
    public void onRender3D(final Render3DEvent e) {
        if (mc.theWorld == null) {
            return;
        }
        for (int i = 0; i < Data_Player.dataList.size(); ++i) {
            Data_Player.dataList.get(i).update(e.getPartialTicks());
        }
        for (int i = 0; i < Data_Zombie.dataList.size(); ++i) {
            Data_Zombie.dataList.get(i).update(e.getPartialTicks());
        }
        for (int i = 0; i < Data_Spider.dataList.size(); ++i) {
            Data_Spider.dataList.get(i).update(e.getPartialTicks());
        }
        if (mc.thePlayer != null) {
            final float newTicks = mc.thePlayer.ticksExisted + e.getPartialTicks();
            if (!mc.theWorld.isRemote || !mc.isGamePaused()) {
                ticksPerFrame = Math.min(Math.max(0.0f, newTicks - ticks), 1.0f);
                ticks = newTicks;
            }
            else {
                ticksPerFrame = 0.0f;
            }
        }
    }

    @Override
    public void onTick(final TickEvent event) {
        if (mc.theWorld == null) {
            return;
        }
        if (!loaded) {
            AnimatedEntity.register();
            loaded = true;
        }
        for (int i = 0; i < Data_Player.dataList.size(); ++i) {
            final Data_Player data = Data_Player.dataList.get(i);
            final Entity entity = mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Player.dataList.remove(data);
                    Data_Player.add(new Data_Player(entity.getEntityId()));
                }
                else {
                    data.motion_prev.set(data.motion);
                    data.motion.x = (float)entity.posX - data.position.x;
                    data.motion.y = (float)entity.posY - data.position.y;
                    data.motion.z = (float)entity.posZ - data.position.z;
                    data.position = new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
                }
            }
            else {
                Data_Player.dataList.remove(data);
            }
        }
        for (int i = 0; i < Data_Zombie.dataList.size(); ++i) {
            final Data_Zombie data2 = Data_Zombie.dataList.get(i);
            final Entity entity = mc.theWorld.getEntityByID(data2.entityID);
            if (entity != null) {
                if (!data2.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Zombie.dataList.remove(data2);
                    Data_Zombie.add(new Data_Zombie(entity.getEntityId()));
                }
                else {
                    data2.motion_prev.set(data2.motion);
                    data2.motion.x = (float)entity.posX - data2.position.x;
                    data2.motion.y = (float)entity.posY - data2.position.y;
                    data2.motion.z = (float)entity.posZ - data2.position.z;
                    data2.position = new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
                }
            }
            else {
                Data_Zombie.dataList.remove(data2);
            }
        }
        for (int i = 0; i < Data_Spider.dataList.size(); ++i) {
            final Data_Spider data3 = Data_Spider.dataList.get(i);
            final Entity entity = mc.theWorld.getEntityByID(data3.entityID);
            if (entity != null) {
                if (!data3.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Spider.dataList.remove(data3);
                    Data_Spider.add(new Data_Spider(entity.getEntityId()));
                }
                else {
                    data3.motion_prev.set(data3.motion);
                    data3.motion.x = (float)entity.posX - data3.position.x;
                    data3.motion.y = (float)entity.posY - data3.position.y;
                    data3.motion.z = (float)entity.posZ - data3.position.z;
                    data3.position = new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
                }
            }
            else {
                Data_Spider.dataList.remove(data3);
            }
        }
    }
    public static boolean onRenderLivingEvent(final RendererLivingEntity renderer, final EntityLivingBase entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        if (renderer instanceof RenderBendsPlayer || renderer instanceof RenderBendsZombie || renderer instanceof RenderBendsSpider) {
            return false;
        }
        final AnimatedEntity animatedEntity = AnimatedEntity.getByEntity(entity);
        if ((animatedEntity != null && (entity instanceof EntityPlayer || entity instanceof EntityZombie)) || entity instanceof EntitySpider) {
            if (entity instanceof EntityPlayer) {
                final AbstractClientPlayer player = (AbstractClientPlayer)entity;
                AnimatedEntity.getPlayerRenderer(player).doRender(player, x, y, z, entityYaw, partialTicks);
            }
            else if (entity instanceof EntityZombie) {
                final EntityZombie zombie = (EntityZombie)entity;
                if (AnimatedEntity.zombieRenderer == null) return false;
                AnimatedEntity.zombieRenderer.doRender(zombie, x, y, z, entityYaw, partialTicks);
            }
            else {
                final EntitySpider spider = (EntitySpider)entity;
                if (AnimatedEntity.spiderRenderer == null) return false;
                AnimatedEntity.spiderRenderer.doRender(spider, x, y, z, entityYaw, partialTicks);
            }
            return true;
        }
        return false;
    }
}
