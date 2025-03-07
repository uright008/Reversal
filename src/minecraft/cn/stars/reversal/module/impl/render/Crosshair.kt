package cn.stars.reversal.module.impl.render

import cn.stars.reversal.event.impl.AttackEvent
import cn.stars.reversal.event.impl.Render2DEvent
import cn.stars.reversal.module.Category
import cn.stars.reversal.module.Module
import cn.stars.reversal.module.ModuleInfo
import cn.stars.reversal.value.impl.BoolValue
import cn.stars.reversal.value.impl.NumberValue
import cn.stars.reversal.util.player.MovementUtils
import cn.stars.reversal.util.render.RenderUtils
import cn.stars.reversal.value.impl.ColorValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "Crosshair", localizedName = "自定义准星", description = "Customize your crosshair", localizedDescription = "自定义你的准星", category = Category.RENDER)
class Crosshair : Module() {
    // Size, width, hitmarker
    private val colorValue = ColorValue("Color", this)
    private val widthValue = NumberValue("Width", this,0.5, 0.2, 10.0, 0.1)
    private val sizeValue = NumberValue("Length", this,7.0, 0.2, 15.0, 0.1)
    private val gapValue = NumberValue("Gap", this,5.0, 0.2, 15.0, 0.1)
    private val dynamicValue = BoolValue("Dynamic", this, true)
    private val hitMarkerValue = BoolValue("HitMarker", this, true)

    var target: EntityLivingBase? = null

    override fun onAttack(event: AttackEvent) {
        if (event.target is EntityLivingBase) {
            target = event.target as EntityLivingBase
        }
    }

    override fun onRender2D(event: Render2DEvent) {
        val sr = event.scaledResolution
        val width = widthValue.value.toFloat()
        val size = sizeValue.value.toFloat()
        val gap = gapValue.value.toFloat()
        val isMoving = dynamicValue.enabled && MovementUtils.isMoving()
        GL11.glPushMatrix()
        RenderUtils.drawBorderedRect(sr.scaledWidth / 2f - width, sr.scaledHeight / 2f - gap - size - if (isMoving) 2 else 0, sr.scaledWidth / 2f + 1.0f + width, sr.scaledHeight / 2f - gap - if (isMoving) 2 else 0, 0.5f, Color(0, 0, 0).rgb, colorValue.color.rgb)
        RenderUtils.drawBorderedRect(sr.scaledWidth / 2f - width, sr.scaledHeight / 2f + gap + 1 + (if (isMoving) 2 else 0) - 0.15f, sr.scaledWidth / 2f + 1.0f + width, sr.scaledHeight / 2f + 1 + gap + size + (if (isMoving) 2 else 0) - 0.15f, 0.5f, Color(0, 0, 0).rgb, colorValue.color.rgb)
        RenderUtils.drawBorderedRect(sr.scaledWidth / 2f - gap - size - (if (isMoving) 2 else 0) + 0.15f, sr.scaledHeight / 2f - width, sr.scaledWidth / 2f - gap - (if (isMoving) 2 else 0) + 0.15f, sr.scaledHeight / 2 + 1.0f + width, 0.5f, Color(0, 0, 0).rgb, colorValue.color.rgb)
        RenderUtils.drawBorderedRect(sr.scaledWidth / 2f + 1 + gap + if (isMoving) 2 else 0, sr.scaledHeight / 2f - width, sr.scaledWidth / 2f + size + gap + 1.0f + if (isMoving) 2 else 0, sr.scaledHeight / 2 + 1.0f + width, 0.5f, Color(0, 0, 0).rgb, colorValue.color.rgb)
        GL11.glPopMatrix()
        GlStateManager.resetColor()
        if (hitMarkerValue.enabled && target != null && target!!.hurtTime > 0) {
            GL11.glPushMatrix()
            GlStateManager.enableBlend()
            GlStateManager.disableTexture2D()
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
            GL11.glColor4f(1f, 1f, 1f, target!!.hurtTime.toFloat() / target!!.maxHurtTime.toFloat())
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glLineWidth(1f)
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap, sr.scaledHeight / 2f + gap)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap + size, sr.scaledHeight / 2f + gap + size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap, sr.scaledHeight / 2f - gap)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap - size, sr.scaledHeight / 2f - gap - size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap, sr.scaledHeight / 2f + gap)
            GL11.glVertex2f(sr.scaledWidth / 2f - gap - size, sr.scaledHeight / 2f + gap + size)
            GL11.glEnd()
            GL11.glBegin(3)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap, sr.scaledHeight / 2f - gap)
            GL11.glVertex2f(sr.scaledWidth / 2f + gap + size, sr.scaledHeight / 2f - gap - size)
            GL11.glEnd()
            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
            GL11.glPopMatrix()
        }
    }
}