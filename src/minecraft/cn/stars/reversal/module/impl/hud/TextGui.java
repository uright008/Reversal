package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.ModeValue;

@ModuleInfo(name = "TextGui", localizedName = "module.TextGui.name", description = "Display a text on your hud",
        localizedDescription = "module.TextGui.desc", category = Category.HUD)
public class TextGui extends Module {
    public final ModeValue mode = new ModeValue("Mode", this, "Simple", "Minecraft", "Modern", "Simple", "Empathy", "ThunderHack", "Shader");
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
