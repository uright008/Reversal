package cn.stars.reversal.module.impl.render

import cn.stars.reversal.event.impl.Render3DEvent
import cn.stars.reversal.module.Category
import cn.stars.reversal.module.Module
import cn.stars.reversal.module.ModuleInfo
import cn.stars.reversal.value.impl.BoolValue
import cn.stars.reversal.value.impl.NumberValue
import cn.stars.reversal.util.wrapper.WrapperAxisAlignedBB
import cn.stars.reversal.util.wrapper.WrapperBlockPos
import cn.stars.reversal.util.wrapper.WrapperBufferBuilder
import cn.stars.reversal.value.impl.ColorValue
import net.minecraft.block.Block
import net.minecraft.block.BlockStairs
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.MovingObjectPosition
import org.lwjgl.opengl.GL11

@ModuleInfo(name = "BlockOverlay", localizedName = "module.BlockOverlay.name", description = "Render a overlay around blocks", localizedDescription = "module.BlockOverlay.desc", category = Category.RENDER)
class BlockOverlay : Module() {
    private var colorValue = ColorValue("Color", this)
    private var fill = BoolValue("Fill", this, false)
    private var outline = BoolValue("Outline", this, true)
    private var throughBlock = BoolValue("ThroughBlock", this, false)
    private var lineWidth = NumberValue("Width", this,  1.0, 0.1, 10.0, 0.1)

    override fun onRender3D(event: Render3DEvent?) {
        if (mc.objectMouseOver != null) {
            if (isHoveringOverBlock()) {
                drawBlock(mc.objectMouseOver.blockPos, fill.enabled, outline.enabled, throughBlock.enabled, lineWidth.value.toFloat())
            }
        }
    }

    fun drawBlock(blockpos: BlockPos, filled: Boolean, outlined: Boolean, throughBlocked: Boolean, lineWidths: Float, fillAlpha: Float = 255f) {
        val color = colorValue.color
        val pos = WrapperBlockPos(blockpos)
        val state = getBlockState(pos)
        val block = getBlock(pos)
        val x = pos.x - mc.renderManager.viewerPosX
        val y = pos.y - mc.renderManager.viewerPosY
        val z = pos.z - mc.renderManager.viewerPosZ
        GL11.glPushMatrix()
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        GL11.glDisable(3553)
        GL11.glEnable(2848)
        if (throughBlock.enabled) {
            GL11.glDisable(2929)
        }
        GlStateManager.depthMask(false)
        val blockBoundingBox = getBlockBoundingBox(pos, state)
        val minX: Double = if (block is BlockStairs) 0.0 else blockBoundingBox.minX()
        val minY: Double = if (block is BlockStairs) 0.0 else blockBoundingBox.minY()
        val minZ: Double = if (block is BlockStairs) 0.0 else blockBoundingBox.minZ()
        if (filled) {
            GL11.glPushMatrix()
            GlStateManager.color(
                color.red / 255.0f,
                color.green / 255.0f,
                color.blue / 255.0f,
                fillAlpha / 255.0f
            )
            drawBoundingBox(
                WrapperAxisAlignedBB(
                    x + minX - 0.01,
                    y + minY - 0.01,
                    z + minZ - 0.01,
                    x + blockBoundingBox.maxX() + 0.01,
                    y + blockBoundingBox.maxY() + 0.01,
                    z + blockBoundingBox.maxZ() + 0.01
                )
            )
            GL11.glPopMatrix()
        }
        if (outlined) {
            GL11.glPushMatrix()
            GlStateManager.color(
                color.red / 255.0f,
                color.green / 255.0f,
                color.blue / 255.0f,
                color.alpha / 255.0f
            )
            GL11.glLineWidth(lineWidths)
            drawBoundingBoxOutline(
                WrapperAxisAlignedBB(
                    x + minX - 0.005,
                    y + minY - 0.005,
                    z + minZ - 0.005,
                    x + blockBoundingBox.maxX() + 0.005,
                    y + blockBoundingBox.maxY() + 0.005,
                    z + blockBoundingBox.maxZ() + 0.005
                )
            )
            GL11.glPopMatrix()
        }
        GL11.glDisable(2848)
        GL11.glEnable(3553)
        if (throughBlocked) {
            GL11.glEnable(2929)
        }
        GlStateManager.depthMask(true)
        GL11.glLineWidth(1.0f)
        GL11.glPopMatrix()
    }

    fun isHoveringOverBlock(): Boolean {
        return Minecraft.getMinecraft().objectMouseOver != null && Minecraft.getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
    }

    fun getBlockState(pos: WrapperBlockPos): IBlockState {
        return Minecraft.getMinecraft().theWorld.getBlockState(pos.pos)
    }

    fun getBlock(pos: WrapperBlockPos): Block {
        return Minecraft.getMinecraft().theWorld.getBlockState(pos.pos).block
    }

    fun getBlockBoundingBox(pos: WrapperBlockPos, state: IBlockState?): WrapperAxisAlignedBB {
        return WrapperAxisAlignedBB(
            AxisAlignedBB(
                getBlock(pos).blockBoundsMinX,
                getBlock(pos).blockBoundsMinY,
                getBlock(pos).blockBoundsMinZ,
                getBlock(pos).blockBoundsMaxX,
                getBlock(pos).blockBoundsMaxY,
                getBlock(pos).blockBoundsMaxZ
            )
        )
    }

    private fun posBoundingBoxLeftHalf(boundingBox: WrapperAxisAlignedBB, bufferBuilder: WrapperBufferBuilder) {
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
    }

    fun drawBoundingBoxOutline(boundingBox: WrapperAxisAlignedBB) {
        val tessellator = Tessellator.instance
        val bufferBuilder = WrapperBufferBuilder(tessellator)
        bufferBuilder.begin(3, DefaultVertexFormats.POSITION)
        posBoundingBoxHalf(boundingBox, bufferBuilder)
        tessellator.draw()
        bufferBuilder.begin(3, DefaultVertexFormats.POSITION)
        posBoundingBoxLeftHalf(boundingBox, bufferBuilder)
        tessellator.draw()
        bufferBuilder.begin(1, DefaultVertexFormats.POSITION)
        posBoundingBoxSquare(boundingBox, tessellator, bufferBuilder)
    }

    private fun posBoundingBoxHalf(boundingBox: WrapperAxisAlignedBB, bufferBuilder: WrapperBufferBuilder) {
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
    }

    private fun posBoundingBoxSquare(
        boundingBox: WrapperAxisAlignedBB,
        tessellator: Tessellator,
        bufferBuilder: WrapperBufferBuilder
    ) {
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        tessellator.draw()
    }
    fun drawBoundingBox(boundingBox: WrapperAxisAlignedBB) {
        val tessellator = Tessellator.instance
        val bufferBuilder = WrapperBufferBuilder(tessellator)
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION)
        posBoundingBoxSquare(boundingBox, tessellator, bufferBuilder)
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION)
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        tessellator.draw()
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION)
        posBoundingBoxLeftHalf(boundingBox, bufferBuilder)
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
        tessellator.draw()
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION)
        posBoundingBoxHalf(boundingBox, bufferBuilder)
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
        tessellator.draw()
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION)
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
        tessellator.draw()
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION)
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.minZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ()).endVertex()
        bufferBuilder.pos(boundingBox.maxX(), boundingBox.minY(), boundingBox.maxZ()).endVertex()
        tessellator.draw()
    }
}