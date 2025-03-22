package cn.stars.reversal.util.render

import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin

class WingUtils : ModelBase() {
    var mc = Minecraft.getMinecraft()
    private var location: ResourceLocation = ResourceLocation("reversal/images/wings.png")
    private val wing: ModelRenderer
    private val wingTip: ModelRenderer
    private var playerUsesFullHeight: Boolean = true

    init {
        this.mc = Minecraft.getMinecraft()
        this.location = ResourceLocation("reversal/images/wings.png")
        this.playerUsesFullHeight = false

        // Set texture offsets.
        setTextureOffset("wing.bone", 0, 0)
        setTextureOffset("wing.skin", -10, 8)
        setTextureOffset("wingtip.bone", 0, 5)
        setTextureOffset("wingtip.skin", -10, 18)


        // Create wing model renderer.
        wing = ModelRenderer(this, "wing")
        wing.setTextureSize(30, 30) // 300px / 10px
        wing.setRotationPoint(-2f, 0f, 0f)
        wing.addBox("bone", -10.0f, -1.0f, -1.0f, 10, 2, 2)
        wing.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10)


        // Create wing tip model renderer.
        wingTip = ModelRenderer(this, "wingtip")
        wingTip.setTextureSize(30, 30) // 300px / 10px
        wingTip.setRotationPoint(-10.0f, 0.0f, 0.0f)
        wingTip.addBox("bone", -10.0f, -0.5f, -0.5f, 10, 1, 1)
        wingTip.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10)
        wing.addChild(wingTip) // Make the wingtip rotate around the wing.
    }

    fun renderWings(partialTicks: Float) {
        val scale: Double = 100 / 100.0
        val rotate = interpolate(mc.thePlayer.prevRenderYawOffset, mc.thePlayer.renderYawOffset, partialTicks).toDouble()

        GL11.glPushMatrix()
        GL11.glScaled(-scale, -scale, scale)
        GL11.glRotated(180 + rotate, 0.0, 1.0, 0.0) // Rotate the wings to be with the player.
        GL11.glTranslated(
            0.0,
            -(if (playerUsesFullHeight) 1.45 else 1.25) / scale,
            0.0
        ) // Move wings correct amount up.
        GL11.glTranslated(0.0, 0.0, 0.2 / scale)

        if (mc.thePlayer.isSneaking()) {
            GL11.glTranslated(0.0, 0.125 / scale, 0.0)
        }

        GL11.glColor3f(1f, 1f, 1f)
        mc.textureManager.bindTexture(location)

        for (j in 0..1) {
            GL11.glEnable(GL11.GL_CULL_FACE)
            val f11 = (System.currentTimeMillis() % 1000) / 1000f * (Math.PI.toFloat()) * 2.0f
            wing.rotateAngleX = Math.toRadians(-80.0).toFloat() - cos(f11.toDouble()).toFloat() * 0.2f
            wing.rotateAngleY = Math.toRadians(20.0).toFloat() + sin(f11.toDouble()).toFloat() * 0.4f
            wing.rotateAngleZ = Math.toRadians(20.0).toFloat()
            wingTip.rotateAngleZ = -((sin((f11 + 2.0f).toDouble()) + 0.5).toFloat()) * 0.75f
            wing.render(0.0625f)
            GL11.glScalef(-1.0f, 1.0f, 1.0f)

            if (j == 0) {
                GL11.glCullFace(1028)
            }
        }
        GL11.glCullFace(1029)
        GL11.glDisable(GL11.GL_CULL_FACE)
        GL11.glColor3f(255f, 255f, 255f)
        GL11.glPopMatrix()
    }

    private fun interpolate(yaw1: Float, yaw2: Float, percent: Float): Float {
        var f = (yaw1 + (yaw2 - yaw1) * percent) % 360

        if (f < 0) {
            f += 360f
        }

        return f
    }
}