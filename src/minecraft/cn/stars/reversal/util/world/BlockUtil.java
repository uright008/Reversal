package cn.stars.reversal.util.world;

import cn.stars.addons.culling.util.Vec3d;
import cn.stars.reversal.GameInstance;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

@UtilityClass
public final class BlockUtil implements GameInstance {
    public boolean isInLiquid() {
        return mc.thePlayer.isInLava() || mc.thePlayer.isInWater();
    }

    public static Block getBlock(Vec3 vec3) {
        return getBlock(new BlockPos(vec3));
    }

    public static Block getBlock(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    public static IBlockState getState(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos);
    }

    public static Vec3d interpolatePos(float prevposX, float prevposY, float prevposZ, float posX, float posY, float posZ) {
        double x = prevposX + ((posX - prevposX) * mc.timer.renderPartialTicks) - mc.getRenderViewEntity().getPositionVector().xCoord;
        double y = prevposY + ((posY - prevposY) * mc.timer.renderPartialTicks) - mc.getRenderViewEntity().getPositionVector().yCoord;
        double z = prevposZ + ((posZ - prevposZ) * mc.timer.renderPartialTicks) - mc.getRenderViewEntity().getPositionVector().zCoord;
        return new Vec3d(x, y, z);
    }
}
