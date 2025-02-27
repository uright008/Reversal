package cn.stars.reversal.module.impl.misc;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.BlockCollideEvent;
import cn.stars.reversal.event.impl.PacketSendEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.NoteValue;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import static net.minecraft.util.EnumFacing.*;

@ModuleInfo(name = "Protocol", chineseName = "跨版本协议", description = "Fix something when you enter specified servers",
        chineseDescription = "在你进入特定服务器时修复一些东西", category = Category.MISC)
public class Protocol extends Module {
    private final NoteValue note = new NoteValue("未跨版本请勿开启.由该功能造成的封禁均不属于开发者问题.", this);
    private final BoolValue fix1_9_plusAttackDistance = new BoolValue("1.9+ Attack Distance", this, true);
    private final BoolValue fix1_11_plusBlockPlacement = new BoolValue("1.12+ Block Placement", this, false);
    private final BoolValue fix1_9_plusBlockCollide = new BoolValue("1.9+ Block Collide", this, false);
    private final BoolValue fix1_9_plusMinimumMotion = new BoolValue("1.9+ Minimum Motion", this, false);

    @Override
    public void onUpdateAlways() {
        if (RainyAPI.isViaCompatibility) {
            Reversal.showMsg("已禁用跨版本,无法使用该功能");
            setEnabled(false);
            return;
        }
        if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() == ViaLoadingBase.getInstance().getNativeVersion() && this.enabled) {
            Reversal.showMsg("未进行跨版本,无法使用该功能");
            setEnabled(false);
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if (fix1_11_plusBlockPlacement.isEnabled() && ViaLoadingBase.getInstance().getTargetVersion().getVersion() >= ProtocolVersion.v1_12.getVersion()
                && packet instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement blockPacket = (C08PacketPlayerBlockPlacement) packet;
            blockPacket.facingX /= 16.0F;
            blockPacket.facingY /= 16.0F;
            blockPacket.facingZ /= 16.0F;
            event.setPacket(blockPacket);
        }
    }

    @Override
    public void onBlockCollide(BlockCollideEvent event) {
        if (fix1_9_plusBlockCollide.isEnabled()) {
            if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > ProtocolVersion.v1_8.getVersion()) {
                final Block block = event.getBlock();

                if (block instanceof BlockLadder) {
                    final IBlockState iblockstate = mc.theWorld.getBlockState(new BlockPos(event.getX(), event.getY(), event.getZ()));

                    if (iblockstate.getBlock() == block) {
                        final float f = 0.125F + 0.0625f;

                        Comparable<?> value = iblockstate.getValue(BlockLadder.FACING);
                        if (value.equals(NORTH)) {
                            event.setCollisionBoundingBox(new AxisAlignedBB(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F)
                                    .offset(event.getX(), event.getY(), event.getZ()));
                        } else if (value.equals(SOUTH)) {
                            event.setCollisionBoundingBox(new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f)
                                    .offset(event.getX(), event.getY(), event.getZ()));
                        } else if (value.equals(WEST)) {
                            event.setCollisionBoundingBox(new AxisAlignedBB(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F)
                                    .offset(event.getX(), event.getY(), event.getZ()));
                        } else {
                            event.setCollisionBoundingBox(new AxisAlignedBB(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F)
                                    .offset(event.getX(), event.getY(), event.getZ()));
                        }
                    }
                }
            }
        }
    }

    public double getReachDistance() {
        if (this.isEnabled() && fix1_9_plusAttackDistance.isEnabled()) {
            if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > ProtocolVersion.v1_8.getVersion()) {
                return 2.9D;
            } else {
                return 3.0D;
            }
        }
        return 3.0D;
    }

    public double getMinimumMotion(EntityLivingBase entity) {
        if (this.isEnabled() && fix1_9_plusMinimumMotion.isEnabled() && entity == mc.thePlayer) {
            if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > ProtocolVersion.v1_8.getVersion()) {
                return 0.003D;
            } else {
                return 0.005D;
            }
        }
        return 0.005D;
    }
}
