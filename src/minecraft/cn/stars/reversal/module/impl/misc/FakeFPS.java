package cn.stars.reversal.module.impl.misc;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.NumberValue;

@ModuleInfo(name = "FakeFPS", chineseName = "虚假帧率", description = "Spoof your client FPS", chineseDescription = "伪装你的客户端帧率", category = Category.MISC)
public class FakeFPS extends Module {
    public final NumberValue fps = new NumberValue("FPS", this, 100, 1, 1919810, 1);
}
