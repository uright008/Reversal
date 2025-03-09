package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.Render3DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.ArrayList;

@ModuleInfo(name = "BlockinDisplay", localizedName = "module.BlockinDisplay.name", description = "Display the block you need to place when blockin", localizedDescription = "module.BlockinDisplay.desc", category = Category.RENDER)
public class BlockinDisplay extends Module {
    public final BoolValue fill = new BoolValue("Fill", this, true);
    public final NumberValue count = new NumberValue("Trigger Block Count", this, 3, 2, 6, 1);

    @Override
    public void onRender3D(Render3DEvent event) {
        if (checkIfBlockin()) {
            ArrayList<BlockPos> blocks = getSurroundingBlocks();
            blocks.removeIf(i -> !(mc.theWorld.getBlockState(i).getBlock() instanceof BlockAir));
            for (BlockPos pos : blocks) {
                ModuleInstance.getModule(BlockOverlay.class).drawBlock(pos, fill.enabled, true, true, 4, 40f);
            }
        }
    }

    private boolean checkIfBlockin() {
        ArrayList<BlockPos> blocks = getSurroundingBlocks();
        blocks.removeIf(i -> mc.theWorld.getBlockState(i).getBlock() instanceof BlockAir);
        return blocks.size() >= count.getInt();
    }
    
    private ArrayList<BlockPos> getSurroundingBlocks() {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        Vec3 pos = getPlayerPos();
        blocks.add(new BlockPos(pos.add(new Vec3(1,0,0))));
        blocks.add(new BlockPos(pos.add(new Vec3(-1,0,0))));
        blocks.add(new BlockPos(pos.add(new Vec3(0,0,1))));
        blocks.add(new BlockPos(pos.add(new Vec3(0,0,-1))));
        blocks.add(new BlockPos(pos.add(new Vec3(1,1,0))));
        blocks.add(new BlockPos(pos.add(new Vec3(-1,1,0))));
        blocks.add(new BlockPos(pos.add(new Vec3(0,1,1))));
        blocks.add(new BlockPos(pos.add(new Vec3(0,1,-1))));
        blocks.add(new BlockPos(pos.add(new Vec3(0,2,0))));
        return blocks;
    }

    private Vec3 getPlayerPos() {
        return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }
}
