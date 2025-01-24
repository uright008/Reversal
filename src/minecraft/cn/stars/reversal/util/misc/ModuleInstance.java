package cn.stars.reversal.util.misc;

import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.impl.client.Optimization;
import cn.stars.reversal.module.impl.addons.SkinLayers3D;
import cn.stars.reversal.module.impl.client.ClientSettings;
import cn.stars.reversal.module.impl.hud.HUD;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.module.impl.client.Chat;
import cn.stars.reversal.module.impl.render.ClickGui;
import cn.stars.reversal.module.impl.client.HurtCam;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import cn.stars.reversal.value.impl.TextValue;
import lombok.NonNull;

@NonNull
public class ModuleInstance {
    public static Module getModule(String moduleName) {
        return Reversal.moduleManager.getModule(moduleName);
    }
    public static <T extends Module> T getModule(Class<T> clazz) {
        return (T) Reversal.moduleManager.getByClass(clazz);
    }
    public static ModeValue getMode(String moduleName, String settingName) throws ClassCastException {
        return (ModeValue) Reversal.moduleManager.getSetting(moduleName, settingName);
    }
    public static BoolValue getBool(String moduleName, String settingName) throws ClassCastException {
        return (BoolValue) Reversal.moduleManager.getSetting(moduleName, settingName);
    }
    public static TextValue getText(String moduleName, String settingName) throws ClassCastException {
        return (TextValue) Reversal.moduleManager.getSetting(moduleName, settingName);
    }
    public static NumberValue getNumber(String moduleName, String settingName) throws ClassCastException {
        return (NumberValue) Reversal.moduleManager.getSetting(moduleName, settingName);
    }

    public static boolean canDrawHUD() {
        if (getModule(HUD.class).isEnabled()) {
            if (getModule(HUD.class).display_when_debugging.enabled) {
                return true;
            } else return !RainyAPI.mc.gameSettings.showDebugInfo;
        }
        return false;
    }

    /**
     * 客户端特殊功能
     */
    public static boolean isSpecialModule(Module module) {
        return module instanceof ClickGui || module instanceof PostProcessing || module instanceof ClientSettings || module instanceof SkinLayers3D || module instanceof HurtCam || module instanceof Optimization || module instanceof Chat;
    }
}
