package cn.stars.reversal.module.impl.client;

import cn.stars.reversal.Reversal;
import com.github.skystardust.InputMethodBlocker.NativeUtils;
import cn.stars.addons.rawinput.RawMouseHelper;
import cn.stars.reversal.event.impl.OpenGUIEvent;
import cn.stars.reversal.event.impl.ValueChangedEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.ui.clickgui.modern.ModernClickGUI;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NoteValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.util.MouseHelper;

@ModuleInfo(name = "ClientSettings", chineseName = "主界面设置", description = "Some settings to change your hud.",
        chineseDescription = "客户端的整体视觉效果设置", category = Category.CLIENT)
public final class ClientSettings extends Module {
    public final NoteValue note1 = new NoteValue("< COLOR SETTINGS >", this);
    public final ModeValue theme = new ModeValue("Theme", this, "Simple",
            "Minecraft", "Reversal", "Modern", "Simple", "Empathy");
    public final ModeValue colorType = new ModeValue("Color Type", this, "Rainbow", "Rainbow", "Double", "Fade", "Static");
    public final NumberValue redValue = new NumberValue("Red", this, 19, 0, 255, 1);
    public final NumberValue greenValue = new NumberValue("Green", this, 150, 0, 255, 1);
    public final NumberValue blueValue = new NumberValue("Blue", this, 255, 0, 255, 1);
    public final NumberValue redValue2 = new NumberValue("Red2", this, 19, 0, 255, 1);
    public final NumberValue greenValue2 = new NumberValue("Green2", this, 150, 0, 255, 1);
    public final NumberValue blueValue2 = new NumberValue("Blue2", this, 255, 0, 255, 1);
    public final NumberValue indexTimes = new NumberValue("Index Times", this, 1, 1, 10, 0.1);
    public final NumberValue indexSpeed = new NumberValue("Index Speed", this, 1, 1, 5, 0.1);

    public final NoteValue note2 = new NoteValue("< SPECIFIC SETTINGS >", this);
    public final ModeValue listAnimation = new ModeValue("List Animation", this, "Reversal", "Reversal", "Slide");
    public final BoolValue thunderHack = new BoolValue("ThunderHack", this, true);
    public final BoolValue empathyGlow = new BoolValue("Empathy Glow", this, true);

    public final NoteValue note3 = new NoteValue("< CLIENT SETTINGS >", this);
    public final BoolValue chinese = new BoolValue("Chinese", this, false);
    public final BoolValue showNotifications = new BoolValue("Show Notifications", this, false);
    public final BoolValue hudTextWithBracket = new BoolValue("Hud Text With Bracket", this, false);

    public final NoteValue note4 = new NoteValue("< MINECRAFT SETTINGS >", this);
    public final BoolValue loadingScreenBackground = new BoolValue("Loading Screen Background", this, false);
    public final BoolValue rawInput = new BoolValue("Raw Input", this, false);
    public final BoolValue inputMethodBlocker = new BoolValue("Input Method Blocker", this, false);


    public static int red1, green1, blue1, red2, green2, blue2;

    public ClientSettings() {
    }

    @Override
    public void onUpdateAlways() {
        // Update this module
        red1 = (int) redValue.getValue();
        green1 = (int) greenValue.getValue();
        blue1 = (int) blueValue.getValue();

        red2 = (int) redValue2.getValue();
        green2 = (int) greenValue2.getValue();
        blue2 = (int) blueValue2.getValue();

        thunderHack.hidden = !theme.getMode().equals("Modern");
        empathyGlow.hidden = !theme.getMode().equals("Empathy");

        if (this.enabled) this.enabled = false;
    }

    @Override
    public void onValueChanged(ValueChangedEvent event) {
        // For Raw Input
        if (event.setting == rawInput) {
            if (rawInput.enabled) {
                mc.mouseHelper = new RawMouseHelper();
                ReversalLogger.info("Switched mc.mouseHelper to RawMouseHelper.");
            }
            else {
                mc.mouseHelper = new MouseHelper();
                ReversalLogger.info("Switched mc.mouseHelper to MouseHelper.");
            }
        }
    }

    @Override
    public void onOpenGUI(OpenGUIEvent event) {
        if (inputMethodBlocker.enabled) {
            final GuiScreen screen = event.getNewScreen();
            if (screen == null || screen instanceof GuiControls || screen instanceof GuiMultiplayer || screen instanceof GuiSelectWorld) {
                NativeUtils.inactiveInputMethod("");
            }
            else if (screen instanceof GuiChat || screen instanceof GuiEditSign || screen instanceof GuiCommandBlock || screen instanceof GuiCreateWorld || screen instanceof GuiScreenBook || screen instanceof GuiRenameWorld ||
                    screen instanceof GuiScreenAddServer || screen instanceof GuiScreenServerList || screen instanceof ModernClickGUI) {
                NativeUtils.activeInputMethod("");
            }
        }
    }

    @Override
    public void onLoad() {
        if (rawInput.enabled) {
            mc.mouseHelper = new RawMouseHelper();
            ReversalLogger.info("Switched mc.mouseHelper to RawMouseHelper.");
        }
        else {
            mc.mouseHelper = new MouseHelper();
            ReversalLogger.info("Switched mc.mouseHelper to MouseHelper.");
        }
    }
}
