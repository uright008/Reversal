package cn.stars.reversal.module.impl.addons;

import cn.stars.reversal.Reversal
import cn.stars.reversal.event.impl.Render3DEvent
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.wrapper.WrapperFreeLook
import net.minecraft.client.resources.I18n
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "FreeLook", localizedName = "module.FreeLook.name", description = "Move around freely", localizedDescription = "module.FreeLook.desc", category = Category.ADDONS)
class FreeLook : Module() {

    init {
        shouldCallNotification = false
    }

    override fun onEnable() {
        if (this.keyBind == Keyboard.KEY_NONE) {
            Reversal.showMsg(I18n.format("module.FreeLook.msg"))
            toggleModule()
            return
        }
        using = true
    }

    override fun onDisable() {
        using = false
    }

    override fun onRender3D(event: Render3DEvent) {
        if (mc.currentScreen != null) return
        if (!perspectiveToggled) {
            if (Keyboard.isKeyDown(this.keyBind)) {
                perspectiveToggled = true
                cameraYaw = mc.thePlayer.rotationYaw
                cameraPitch = mc.thePlayer.rotationPitch
                previousPerspective = mc.gameSettings.hideGUI
                mc.gameSettings.thirdPersonView = 1
            } else {
                toggleModule()
            }
        } else if (!Keyboard.isKeyDown(this.keyBind)) {
            perspectiveToggled = false
            mc.gameSettings.thirdPersonView = if (previousPerspective) 1 else 0
            toggleModule()
        }
    }

    companion object {
        @JvmField
        var using = false
        @JvmField
        var perspectiveToggled = false
        @JvmField
        var cameraYaw = 0f
        @JvmField
        var cameraPitch = 0f
        private var previousPerspective = false
        @JvmStatic
        fun getCameraYaw(): Float {
            return WrapperFreeLook.getCameraYaw()
        }

        @JvmStatic
        fun getCameraPitch(): Float {
            return WrapperFreeLook.getCameraPitch()
        }

        @JvmStatic
        val cameraPrevYaw: Float
            get() = WrapperFreeLook.getCameraPrevYaw()
        @JvmStatic
        val cameraPrevPitch: Float
            get() = WrapperFreeLook.getCameraPrevPitch()

        @JvmStatic
        fun overrideMouse(): Boolean {
            return WrapperFreeLook.overrideMouse()
        }
    }
}
