package cn.stars.reversal.module.impl.client

import cn.stars.reversal.event.impl.Render2DEvent
import cn.stars.reversal.module.Category
import cn.stars.reversal.module.Module
import cn.stars.reversal.module.ModuleInfo
import cn.stars.reversal.util.render.RenderUtil
import cn.stars.reversal.value.impl.ModeValue
import cn.stars.reversal.value.impl.NumberValue
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color


@ModuleInfo(name = "HurtCam", localizedName = "module.HurtCam.name", description = "Modify the shake when you get hurt", localizedDescription = "module.HurtCam.desc", category = Category.CLIENT)
class HurtCam : Module() {
    val mode = ModeValue("Mode", this, "Vanilla", "Cancel", "Vanilla", "FPS")
    private val fpsTimeValue = NumberValue("FPS Time", this, 1000.0, 0.0, 1500.0, 1.0)
    private val fpsHeightValue = NumberValue("FPS Height", this,25.0, 10.0, 50.0, 1.0)
    private var sr = ScaledResolution(mc)

    override fun onUpdateAlways() {
        if (this.enabled) this.enabled = false;
    }

    override fun onUpdateAlwaysInGui() {
        fpsTimeValue.hidden = !mode.mode.equals("FPS")
        fpsHeightValue.hidden = !mode.mode.equals("FPS")
    }

    override fun onRender2D(event: Render2DEvent) {
        sr = ScaledResolution(mc)
        if (hurt == 0L) return

        val passedTime = System.currentTimeMillis() - hurt
        if (passedTime> fpsTimeValue.value) {
            hurt = 0L
            return
        }

        val color = getColor((((fpsTimeValue.value - passedTime) / fpsTimeValue.value.toFloat()) * 255).toInt())
        val color1 = getColor(0)
        val width = sr.scaledWidth
        val height = sr.scaledHeight

        RenderUtil.drawGradientRect(0, 0, width, fpsHeightValue.value.toInt(), color.rgb, color1.rgb)
        RenderUtil.drawGradientRect(0, height - fpsHeightValue.value.toInt(), width, height, color1.rgb, color.rgb)
    }

    private fun getColor(alpha: Int): Color {
        return Color(220,0,0, alpha)
    }

    companion object {
        @JvmField
        var hurt = 0L
    }
}
