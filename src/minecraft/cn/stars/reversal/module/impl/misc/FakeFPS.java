package cn.stars.reversal.module.impl.misc;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.NoteValue;
import cn.stars.reversal.value.impl.NumberValue;

@ModuleInfo(name = "FakeFPS", localizedName = "module.FakeFPS.name", description = "Spoof your client FPS", localizedDescription = "module.FakeFPS.desc", category = Category.MISC)
public class FakeFPS extends Module {
    public final NoteValue note = new NoteValue("如果无法精确设置数值,可使用指令调整: '.FakeFPS <设置名> <值>'", this);
    public final BoolValue randomized = new BoolValue("Randomized", this, false);
    public final NumberValue fps = new NumberValue("FPS", this, 100, 1, 114514, 1);
    public final NumberValue minimumFps = new NumberValue("Minimum FPS", this, 100, 1, 114514, 1);
    public final NumberValue maximumFps = new NumberValue("Maximum FPS", this, 100, 1, 114514, 1);

    @Override
    public void onUpdateAlwaysInGui() {
        fps.hidden = randomized.enabled;
        minimumFps.hidden = !randomized.enabled;
        maximumFps.hidden = !randomized.enabled;
    }
}
