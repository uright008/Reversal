package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;

@ModuleInfo(name = "TextGui", chineseName = "客户端标题", description = "Display a text on your hud",
        chineseDescription = "显示客户端LOGO", category = Category.HUD)
public class TextGui extends Module {
    public final ColorValue colorValue = new ColorValue("Color", this);
    public final BoolValue custom = new BoolValue("Custom Name", this,true);
    public TextGui() {
        setCanBeEdited(true);
        setX(1);
        setY(5);
        setWidth(50);
        setHeight(30);
    }
}
