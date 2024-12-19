package cn.stars.reversal.module;

import cn.stars.reversal.event.impl.ClickEvent;
import cn.stars.reversal.event.impl.TickEvent;
import cn.stars.reversal.module.impl.hud.CPSCounter;
import cn.stars.reversal.util.misc.ModuleInstance;
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
}
