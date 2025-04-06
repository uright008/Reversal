package cn.stars.reversal.module;

import cn.stars.reversal.RainyAPI;
import cn.stars.addons.optimization.util.FastTrig;
import cn.stars.reversal.event.impl.*;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.module.impl.client.Hotbar;
import cn.stars.reversal.module.impl.client.HurtCam;
import cn.stars.reversal.module.impl.hud.CPSCounter;
import cn.stars.reversal.util.misc.ModuleInstance;
import net.minecraft.client.Minecraft;
import net.optifine.cache.OptifineCustomItemCache;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

/**
 * Events which are always handled in background.
 */
public class ResidentProcessor {
    private final Minecraft mc = Minecraft.getMinecraft();

    public void onUpdateAlways() {
    }

    public void onClick(ClickEvent event) {
        if (event.getType() == ClickEvent.ClickType.LEFT) {
            ModuleInstance.getModule(CPSCounter.class).Lclicks.add(System.currentTimeMillis());
        }
        if (event.getType() == ClickEvent.ClickType.RIGHT) {
            ModuleInstance.getModule(CPSCounter.class).Rclicks.add(System.currentTimeMillis());
        }
    }

    public void onTick(TickEvent event) {
        ModuleInstance.getModule(CPSCounter.class).Lclicks.removeIf(l -> l < System.currentTimeMillis() - 1000L);
        ModuleInstance.getModule(CPSCounter.class).Rclicks.removeIf(t -> t < System.currentTimeMillis() - 1000L);

        // Process optifine performance tick
        if (mc.thePlayer != null) {
            OptifineCustomItemCache.INSTANCE.onTick();
        }
    }

    public void onGuiClosed(GUIClosedEvent event) {
        // Fix text field cursor problems
        GLFW.glfwSetCursor(RainyAPI.window, MemoryUtil.NULL);
    }

    public void onRender2D(Render2DEvent event) {
        ModuleInstance.getModule(HurtCam.class).onRender2D(event);
    }

    public void onShader3D(Shader3DEvent event) {
        ModuleInstance.getModule(Hotbar.class).onShader3D(event);
    }

    public void onValueChanged(ValueChangedEvent event) {
        ModuleInstance.getModule(ClientSettings.class).onValueChanged(event);
    }

    public void onOpenGUI(OpenGUIEvent event) {
        ModuleInstance.getModule(ClientSettings.class).onOpenGUI(event);
    }
}
