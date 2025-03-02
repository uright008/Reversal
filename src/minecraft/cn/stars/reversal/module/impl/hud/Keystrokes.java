package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.ModeValue;

@ModuleInfo(name = "Keystrokes", chineseName = "按键显示", description = "Show the WASD operation on screen.",
        chineseDescription = "显示你的按键操作", category = Category.HUD)
public class Keystrokes extends Module {
    public final ModeValue modeValue = new ModeValue("Mode", this, "Modern", "Modern", "Minecraft");
    public final ColorValue colorValue = new ColorValue("Key Down Color", this);
    public final BoolValue showCpsValue = new BoolValue("Show CPS", this, false);
    public final BoolValue shadow = new BoolValue("Shadow", this,false);
    public Keystrokes() {
        setCanBeEdited(true);
        setX(1);
        setY(5);
        setWidth(100);
        setHeight(100);
    }


}
