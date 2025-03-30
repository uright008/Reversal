/*
 Copyright Alan Wood 2021
 None of this code to be reused without my written permission
 Intellectual Rights owned by Alan Wood
 */
package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "ClickGui", localizedName = "module.ClickGui.name", description = "Opens a Gui where you can toggle modules and change their settings",
        localizedDescription = "module.ClickGui.desc", category = Category.RENDER, defaultKey = Keyboard.KEY_RSHIFT)
public final class ClickGui extends Module {

    private final ModeValue mode = new ModeValue("Mode", this, "Modern", "Modern");
    public final BoolValue customColor = new BoolValue("Custom Color", this, false);
    public final ColorValue colorValue = new ColorValue("Color", this);
    public final NumberValue scrollSpeed = new NumberValue("Scroll Speed", this, 4.0, 0.5, 10.0, 1.0);
    public final BoolValue bubbles = new BoolValue("Bubbles", this, false);

    @Override
    public void onUpdateAlwaysInGui() {
        colorValue.hidden = !customColor.enabled;
    }

    @Override
    protected void onEnable() {
        switch (mode.getMode()) {
            case "MomoTalk": {
            //    mc.displayGuiScreen(Reversal.mmtClickGUI);
                break;
            }

            case "Modern": {
                mc.displayGuiScreen(Reversal.modernClickGUI);
                break;
            }
        }

        this.setEnabled(false);
    }

    @Override
    protected void onDisable() {
        Reversal.saveAll();
    }
}
