package cn.stars.reversal.module.impl.render

import cn.stars.reversal.Reversal
import cn.stars.reversal.event.impl.Render3DEvent
import cn.stars.reversal.event.impl.UpdateEvent
import cn.stars.reversal.event.impl.WorldEvent
import cn.stars.reversal.module.Category
import cn.stars.reversal.module.Module
import cn.stars.reversal.module.ModuleInfo
import cn.stars.reversal.value.impl.ColorValue
import cn.stars.reversal.value.impl.NumberValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import java.math.BigDecimal
import java.util.*


@ModuleInfo(name = "DamageParticle", localizedName = "module.DamageParticle.name", description = "Show a particle of your damage when you hit.", localizedDescription = "module.DamageParticle.desc", category = Category.RENDER)
class DamageParticle : Module(){
    private val colorValue = ColorValue("Color", this)
    private val aliveTicksValue = NumberValue("AliveTicks", this,20.0, 10.0, 50.0, 1.0)
    private val sizeValue = NumberValue("Size", this,3.0, 1.0, 7.0, 1.0)

    private val healthData = mutableMapOf<Int, Float>()
    private val particles = mutableListOf<SingleParticle>()

   
    override fun onUpdate(event: UpdateEvent) {
        synchronized(particles) {
            for(entity in mc.theWorld.loadedEntityList) {
                if(entity is EntityLivingBase) {
                    val lastHealth = healthData.getOrDefault(entity.entityId,entity.maxHealth)
                    healthData[entity.entityId] = entity.health
                    if(lastHealth == entity.health) continue

                    try {
                        val prefix = if (!colorValue.themeColor) (if (lastHealth > entity.health) {
                            "§c❤"
                        } else {
                            "§a§l❤"
                        }) else (if (lastHealth > entity.health) {
                            "-"
                        } else {
                            "+"
                        })
                        particles.add(
                            SingleParticle(
                                prefix + BigDecimal(kotlin.math.abs(lastHealth - entity.health).toDouble()).setScale(
                                    1,
                                    BigDecimal.ROUND_HALF_UP
                                ).toDouble(),
                                entity.posX - 0.5 + Random(System.currentTimeMillis()).nextInt(5).toDouble() * 0.1,
                                entity.entityBoundingBox.minY + (entity.entityBoundingBox.maxY - entity.entityBoundingBox.minY) / 2.0,
                                entity.posZ - 0.5 + Random(System.currentTimeMillis() + 1L).nextInt(5).toDouble() * 0.1
                            )
                        )
                    } catch (e: NumberFormatException) {
                        Reversal.showMsg("当前服务器不支持DamageParticle! 请关闭功能。")
                    }
                }
            }

            val needRemove = ArrayList<SingleParticle> ()
            for (particle in particles) {
                particle.ticks++
                if (particle.ticks>aliveTicksValue.value) {
                    needRemove.add(particle)
                }
            }
            for (particle in needRemove) {
                particles.remove(particle)
            }
        }
    }

    override fun onRender3D(event: Render3DEvent) {
        synchronized(particles) {
            val renderManager = mc.renderManager
            val size = sizeValue.value * 0.01

            for (particle in particles) {
                val n: Double = particle.posX - renderManager.renderPosX
                val n2: Double = particle.posY - renderManager.renderPosY
                val n3: Double = particle.posZ - renderManager.renderPosZ
                GlStateManager.pushMatrix()
                GlStateManager.enablePolygonOffset()
                GlStateManager.doPolygonOffset(1.0f, -1500000.0f)
                GlStateManager.translate(n.toFloat(), n2.toFloat(), n3.toFloat())
                GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
                val textY = if (mc.gameSettings.thirdPersonView == 2) { -1.0f } else { 1.0f }

                GlStateManager.rotate(renderManager.playerViewX, textY, 0.0f, 0.0f)
                GlStateManager.scale(-size, -size, size)
                GL11.glDepthMask(false)
                mc.fontRendererObj.drawStringWithShadow(
                    particle.str,
                    (-(mc.fontRendererObj.getStringWidth(particle.str) / 2)).toFloat(),
                    (-(mc.fontRendererObj.FONT_HEIGHT - 1)).toFloat(),
                    (colorValue.color.rgb)
                )
                GL11.glColor4f(187.0f, 255.0f, 255.0f, 1.0f)
                GL11.glDepthMask(true)
                GlStateManager.doPolygonOffset(1.0f, 1500000.0f)
                GlStateManager.disablePolygonOffset()
                GlStateManager.resetColor()
                GlStateManager.popMatrix()
            }
        }
    }

    override fun onWorld(event: WorldEvent) {
        particles.clear()
        healthData.clear()
    }
}
class SingleParticle(val str: String, val posX: Double, val posY: Double, val posZ: Double) {
    var ticks = 0
}