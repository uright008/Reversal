package cn.stars.reversal.module;

import cn.stars.reversal.event.impl.ClickEvent;
import cn.stars.reversal.event.impl.GUIClosedEvent;
import cn.stars.reversal.event.impl.TickEvent;
import cn.stars.reversal.module.impl.hud.CPSCounter;
import cn.stars.reversal.util.misc.ModuleInstance;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.Display;
import org.lwjgl.system.MemoryUtil;

/**
 * Events which are always handled in background.
 */
public class ResidentProcessor {
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
    }

    public void onGuiClosed(GUIClosedEvent event) {
        GLFW.glfwSetCursor(Display.getWindow(), MemoryUtil.NULL);
    }
}
