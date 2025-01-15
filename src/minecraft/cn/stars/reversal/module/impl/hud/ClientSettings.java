package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;

@ModuleInfo(name = "ClientSettings", chineseName = "主界面设置", description = "Some settings to change your hud.",
        chineseDescription = "客户端的整体视觉效果设置", category = Category.HUD)
public final class ClientSettings extends Module {
    public final ModeValue theme = new ModeValue("Theme", this, "Simple",
            "Minecraft", "Reversal", "Modern", "Simple", "Empathy");
    public final ModeValue colorType = new ModeValue("Color Type", this, "Rainbow", "Rainbow", "Double", "Fade", "Static");
    public final ModeValue listAnimation = new ModeValue("List Animation", this, "Reversal", "Reversal", "Slide");
    public final NumberValue redValue = new NumberValue("Red", this, 19, 0, 255, 1);
    public final NumberValue greenValue = new NumberValue("Green", this, 150, 0, 255, 1);
    public final NumberValue blueValue = new NumberValue("Blue", this, 255, 0, 255, 1);
    public final NumberValue redValue2 = new NumberValue("Red2", this, 19, 0, 255, 1);
    public final NumberValue greenValue2 = new NumberValue("Green2", this, 150, 0, 255, 1);
    public final NumberValue blueValue2 = new NumberValue("Blue2", this, 255, 0, 255, 1);
    public final NumberValue indexTimes = new NumberValue("Index Times", this, 1, 1, 10, 0.1);
    public final NumberValue indexSpeed = new NumberValue("Index Speed", this, 1, 1, 5, 0.1);

    public final BoolValue showNotifications = new BoolValue("Show Notifications", this, false);
    public final BoolValue chinese = new BoolValue("Chinese", this, false);

    public final BoolValue thunderHack = new BoolValue("ThunderHack", this, true);
    public final BoolValue empathyGlow = new BoolValue("Empathy Glow", this, true);

    public static int red1, green1, blue1, red2, green2, blue2;

    public ClientSettings() {
        setWidth(0);
        setHeight(0);
        setCanBeEdited(false);
    }

    @Override
    public void onUpdateAlways() {
        red1 = (int) redValue.getValue();
        green1 = (int) greenValue.getValue();
        blue1 = (int) blueValue.getValue();

        red2 = (int) redValue2.getValue();
        green2 = (int) greenValue2.getValue();
        blue2 = (int) blueValue2.getValue();

        thunderHack.hidden = !theme.getMode().equals("Modern");
        empathyGlow.hidden = !theme.getMode().equals("Empathy");

        if (!isEnabled()) toggleModule();

        setSuffix(theme.getMode());
    }
}
