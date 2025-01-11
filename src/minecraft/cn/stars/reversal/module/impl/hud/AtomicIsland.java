package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.ui.atmoic.Atomic;

@ModuleInfo(name = "AtomicIsland", chineseName = "原子岛", description = "Display an island-like bar", chineseDescription = "显示一个像岛屿的条子", category = Category.HUD)
public class AtomicIsland extends Module {
    public AtomicIsland() {
        setCanBeEdited(false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        Atomic.INSTANCE.render(event);
    }

    @Override
    protected void onDisable() {
        Atomic.width = 0;
        Atomic.height = 0;
    }
}
