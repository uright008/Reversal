package cn.stars.reversal.module.impl.addons;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;

@ModuleInfo(name = "WaveyCapes", localizedName = "飘扬披风", description = "Make your cape waving look better",
        localizedDescription = "使你的披风飘动更真实", category = Category.ADDONS)
public class WaveyCapes extends Module {
    public static int abc = 1;
    private final BoolValue wind = new BoolValue("Wind", this, true);
    private final ModeValue style = new ModeValue("Cape Style", this, "Smooth", "Blocky", "Smooth");
    private final ModeValue cm = new ModeValue("Cape Movement", this, "Vanilla", "Basic simulation", "Vanilla");
    private final NumberValue gravity = new NumberValue("Gravity", this,  25, 5, 32, 1);
    private final NumberValue hm = new NumberValue("Height Multiplier", this, 6, 4 , 16, 1);

}
