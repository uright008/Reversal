package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.PreBlurEvent
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.NumberValue
import cn.stars.reversal.util.wrapper.WrapperBufferBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11

@ModuleInfo(name = "MotionBlur", localizedName = "module.MotionBlur.name", description = "Render blur on your view", localizedDescription = "module.MotionBlur.desc", category = Category.RENDER)
class MotionBlur : Module() {
    private var blurBufferMain: Framebuffer? = null
    private var blurBufferInto: Framebuffer? = null
    private val multiplier = NumberValue("Multiplier", this, 4.0, 0.0, 10.0, 0.1)

    private fun checkFramebufferSizes(framebuffer: Framebuffer?, width: Int, height: Int): Framebuffer {
        var theFramebuffer = framebuffer
        if (theFramebuffer == null || theFramebuffer.framebufferWidth != width || theFramebuffer.framebufferHeight != height) {
            if (theFramebuffer == null) {
                theFramebuffer = Framebuffer(width, height, true)
            } else {
                theFramebuffer.createBindFramebuffer(width, height)
            }
            theFramebuffer.setFramebufferFilter(9728)
        }
        return theFramebuffer
    }

    private fun drawTexturedRectNoBlend(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        uMin: Float,
        uMax: Float,
        vMin: Float,
        vMax: Float,
        filter: Int
    ) {
        GlStateManager.enableTexture2D()
        GL11.glTexParameteri(3553, 10241, filter)
        GL11.glTexParameteri(3553, 10240, filter)
        val tessellator = Tessellator.instance
        val worldrenderer = WrapperBufferBuilder(tessellator)
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldrenderer.pos(x, y + height, 0.0).tex(uMin.toDouble(), vMax.toDouble()).endVertex()
        worldrenderer.pos(x + width, y + height, 0.0).tex(uMax.toDouble(), vMax.toDouble()).endVertex()
        worldrenderer.pos(x + width, y, 0.0).tex(uMax.toDouble(), vMin.toDouble()).endVertex()
        worldrenderer.pos(x, y, 0.0).tex(uMin.toDouble(), vMin.toDouble()).endVertex()
        tessellator.draw()
        GL11.glTexParameteri(3553, 10241, 9728)
        GL11.glTexParameteri(3553, 10240, 9728)
    }

    override fun onPreBlur(event: PreBlurEvent) {
        if (mc.thePlayer == null || mc.thePlayer!!.ticksExisted < 20) return
        if (mc.currentScreen == null) {
            if (OpenGlHelper.isFramebufferEnabled()) {
                val sr = ScaledResolution(Minecraft.getMinecraft())
                val width = mc.framebuffer.framebufferWidth
                val height = mc.framebuffer.framebufferHeight
                GlStateManager.matrixMode(5889)
                GlStateManager.loadIdentity()
                GlStateManager.ortho(
                    0.0,
                    (width / sr.scaleFactor).toDouble(),
                    (height / sr.scaleFactor).toDouble(),
                    0.0,
                    2000.0,
                    4000.0
                )
                GlStateManager.matrixMode(5888)
                GlStateManager.loadIdentity()
                GlStateManager.translate(0f, 0f, -2000f)
                blurBufferMain = checkFramebufferSizes(blurBufferMain, width, height)
                blurBufferInto = checkFramebufferSizes(blurBufferInto, width, height)
                blurBufferInto!!.framebufferClear()
                blurBufferInto!!.bindFramebuffer(true)
                OpenGlHelper.glBlendFunc(770, 771, 0, 1)
                GlStateManager.disableLighting()
                GlStateManager.disableFog()
                GlStateManager.disableBlend()
                mc.framebuffer.bindFramebufferTexture()
                GlStateManager.color(1f, 1f, 1f, 1f)
                drawTexturedRectNoBlend(
                    0.0f,
                    0.0f,
                    (width / sr.scaleFactor).toFloat(),
                    (height / sr.scaleFactor).toFloat(),
                    0.0f,
                    1.0f,
                    0.0f,
                    1.0f,
                    9728
                )
                GlStateManager.enableBlend()
                blurBufferMain!!.bindFramebufferTexture()
                GlStateManager.color(1f, 1f, 1f, multiplier.value.toFloat() / 10 - 0.1f)
                drawTexturedRectNoBlend(
                    0f,
                    0f,
                    (width / sr.scaleFactor).toFloat(),
                    (height / sr.scaleFactor).toFloat(),
                    0f,
                    1f,
                    1f,
                    0f,
                    9728
                )
                mc.framebuffer.bindFramebuffer(true)
                blurBufferInto!!.bindFramebufferTexture()
                GlStateManager.color(1f, 1f, 1f, 1f)
                GlStateManager.enableBlend()
                OpenGlHelper.glBlendFunc(770, 771, 1, 771)
                drawTexturedRectNoBlend(
                    0.0f,
                    0.0f,
                    (width / sr.scaleFactor).toFloat(),
                    (height / sr.scaleFactor).toFloat(),
                    0.0f,
                    1.0f,
                    0.0f,
                    1.0f,
                    9728
                )
                val tempBuff = blurBufferMain
                blurBufferMain = blurBufferInto
                blurBufferInto = tempBuff
            }
        }
    }
}
