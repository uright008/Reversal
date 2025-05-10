package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.event.impl.UpdateEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.reversal.pool.impl.PooledRunnable;
import cn.stars.reversal.util.reversal.pool.impl.TrackableRunnable;
import cn.stars.reversal.value.impl.*;

@ModuleInfo(name = "TestElement", localizedName = "module.TestElement.name", description = "Only for test",
        localizedDescription = "module.TestElement.desc", category = Category.HUD)
public class TestElement extends Module {
    private final NoteValue note = new NoteValue("测试功能,请勿开启!", this);
    private final TextValue textValue = new TextValue("TextValue", this, "123");
    private final CustomValue customValue = new CustomValue("CustomValue", this, () -> Reversal.showMsg("好好好"));
    private final ColorValue colorValue = new ColorValue("Color", this);
    public TestElement() {
        setCanBeEdited(true);
        setX(100);
        setY(100);
        setWidth(100);
        setHeight(100);
    }
    private final TimeUtil timeUtil = new TimeUtil();

    @Override
    public void onRender2D(Render2DEvent event) {
    }

    @Override
    public void onUpdate(UpdateEvent event) {
    //    Reversal.showMsg(Reversal.runnablePool.toString());
    }
}
